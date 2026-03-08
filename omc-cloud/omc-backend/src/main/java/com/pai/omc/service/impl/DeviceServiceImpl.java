package com.pai.omc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pai.omc.common.PageResult;
import com.pai.omc.config.Configuration;
import com.pai.omc.dto.DeviceDTO;
import com.pai.omc.dto.ImportResultDTO;
import com.pai.omc.dto.SubnetSearchDTO;
import com.pai.omc.entity.Device;
import com.pai.omc.entity.DeviceSubtype;
import com.pai.omc.entity.DeviceType;
import com.pai.omc.exception.BusinessException;
import com.pai.omc.mapper.DeviceMapper;
import com.pai.omc.mapper.DeviceSubtypeMapper;
import com.pai.omc.mapper.DeviceTypeMapper;
import com.pai.omc.query.DeviceQuery;
import com.pai.omc.service.DeviceService;
import com.pai.omc.vo.DeviceSubtypeVO;
import com.pai.omc.vo.DeviceTypeVO;
import com.pai.omc.vo.DeviceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 设备管理服务实现
 *
 * @author make java
 * @since 2026-03-08
 */
@Slf4j
@Service
public class DeviceServiceImpl implements DeviceService {

    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private DeviceTypeMapper deviceTypeMapper;

    @Resource
    private DeviceSubtypeMapper deviceSubtypeMapper;

    @Override
    public List<DeviceVO> searchCurrentSubnet() {
        try {
            String localIp = getLocalIpAddress();
            String subnet = localIp.substring(0, localIp.lastIndexOf('.'));

            Set<String> existingIps = getExistingDeviceIps();
            List<DeviceVO> result = new ArrayList<>();

            for (int i = 1; i <= 254; i++) {
                String targetIp = subnet + "." + i;
                if (existingIps.contains(targetIp)) {
                    continue;
                }
                DeviceVO device = probeDevice(targetIp, 8080);
                if (device != null) {
                    result.add(device);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("搜索当前网段异常：", e);
            throw new BusinessException("搜索失败：" + e.getMessage());
        }
    }

    @Override
    public List<DeviceVO> searchOtherSubnet(SubnetSearchDTO dto) {
        Set<String> existingIps = getExistingDeviceIps();
        List<DeviceVO> result = new ArrayList<>();

        for (String ipRange : dto.getIpRanges()) {
            try {
                String[] parts = ipRange.split("-");
                if (parts.length != 2) {
                    continue;
                }
                String startIp = parts[0].trim();
                String endIp = parts[1].trim();

                long start = ipToLong(startIp);
                long end = ipToLong(endIp);

                for (long ip = start; ip <= end; ip++) {
                    String targetIp = longToIp(ip);
                    if (existingIps.contains(targetIp)) {
                        continue;
                    }
                    DeviceVO device = probeDevice(targetIp, 8080);
                    if (device != null) {
                        result.add(device);
                    }
                }
            } catch (Exception e) {
                log.error("搜索网段{}异常：", ipRange, e);
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToDeviceList(DeviceVO deviceVO) {
        Device device = BeanUtil.copyProperties(deviceVO, Device.class);
        deviceMapper.insert(device);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddToDeviceList(List<DeviceVO> deviceList) {
        if (deviceList == null || deviceList.isEmpty()) {
            throw new BusinessException("请选择设备");
        }
        for (DeviceVO vo : deviceList) {
            Device device = BeanUtil.copyProperties(vo, Device.class);
            deviceMapper.insert(device);
        }
    }

    @Override
    public PageResult<DeviceVO> pageDevice(DeviceQuery query) {
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getIpAddress())) {
            wrapper.like(Device::getIpAddress, query.getIpAddress());
        }
        if (StrUtil.isNotBlank(query.getDeviceCode())) {
            wrapper.like(Device::getDeviceCode, query.getDeviceCode());
        }
        if (StrUtil.isNotBlank(query.getSiteName())) {
            wrapper.like(Device::getSiteName, query.getSiteName());
        }
        wrapper.orderByDesc(Device::getCreateTime);

        Page<Device> page = deviceMapper.selectPage(
                new Page<>(query.getCurrent(), query.getSize()), wrapper);

        List<DeviceVO> voList = page.getRecords().stream()
                .map(entity -> BeanUtil.copyProperties(entity, DeviceVO.class))
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), voList, page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeviceVO addDevice(DeviceDTO dto) {
        DeviceVO info = fetchDeviceInfo(dto.getIpAddress(), dto.getPort());
        Device device = BeanUtil.copyProperties(info, Device.class);
        deviceMapper.insert(device);
        info.setId(device.getId());
        return info;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeviceVO updateDevice(DeviceDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("ID不能为空");
        }
        DeviceVO info = fetchDeviceInfo(dto.getIpAddress(), dto.getPort());
        Device device = BeanUtil.copyProperties(info, Device.class);
        device.setId(dto.getId());
        deviceMapper.updateById(device);
        info.setId(dto.getId());
        return info;
    }

    @Override
    public void deleteDevice(Long id) {
        deviceMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteDevice(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请先选择数据");
        }
        deviceMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultDTO importDevice(MultipartFile file) {
        try {
            return ImportResultDTO.success(0);
        } catch (Exception e) {
            log.error("导入设备异常：", e);
            throw new BusinessException("导入失败：" + e.getMessage());
        }
    }

    @Override
    public void exportDevice(DeviceQuery query, HttpServletResponse response) {
        try {
            LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
            if (StrUtil.isNotBlank(query.getIpAddress())) {
                wrapper.like(Device::getIpAddress, query.getIpAddress());
            }
            List<Device> list = deviceMapper.selectList(wrapper);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + java.net.URLEncoder.encode("设备列表.xlsx", StandardCharsets.UTF_8.name()));

            com.alibaba.excel.EasyExcel.write(response.getOutputStream(), Device.class)
                    .sheet("设备列表")
                    .doWrite(list);
        } catch (Exception e) {
            log.error("导出设备异常：", e);
            throw new BusinessException("导出失败");
        }
    }

    @Override
    public List<DeviceTypeVO> listDeviceTypes() {
        List<DeviceType> list = deviceTypeMapper.selectList(new LambdaQueryWrapper<>());
        return list.stream()
                .map(entity -> BeanUtil.copyProperties(entity, DeviceTypeVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceSubtypeVO> listDeviceSubtypes(String typeId) {
        LambdaQueryWrapper<DeviceSubtype> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(typeId)) {
            wrapper.eq(DeviceSubtype::getTypeId, typeId);
        }
        List<DeviceSubtype> list = deviceSubtypeMapper.selectList(wrapper);
        return list.stream()
                .map(entity -> BeanUtil.copyProperties(entity, DeviceSubtypeVO.class))
                .collect(Collectors.toList());
    }

    private String getLocalIpAddress() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                    return addr.getHostAddress();
                }
            }
        }
        return "127.0.0.1";
    }

    private Set<String> getExistingDeviceIps() {
        List<Device> devices = deviceMapper.selectList(new LambdaQueryWrapper<>());
        return devices.stream().map(Device::getIpAddress).collect(Collectors.toSet());
    }

    private DeviceVO probeDevice(String ip, int port) {
        try {
            int timeout = Configuration.DEVICE_SEARCH_TIMEOUT != null
                    ? Configuration.DEVICE_SEARCH_TIMEOUT : 5000;
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.close();

            DeviceVO vo = new DeviceVO();
            vo.setIpAddress(ip);
            vo.setPort(port);
            return vo;
        } catch (Exception e) {
            return null;
        }
    }

    private DeviceVO fetchDeviceInfo(String ip, Integer port) {
        DeviceVO vo = new DeviceVO();
        vo.setIpAddress(ip);
        vo.setPort(port);
        vo.setDeviceCode("FSU-" + ip.replace(".", ""));
        vo.setDeviceName("边缘网关-" + ip);
        vo.setSoftwareVersion("V1.0");
        vo.setBInterfaceVersion("V2.0");
        vo.setSiteName("站点-" + ip);
        return vo;
    }

    private long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        return (Long.parseLong(parts[0]) << 24)
                + (Long.parseLong(parts[1]) << 16)
                + (Long.parseLong(parts[2]) << 8)
                + Long.parseLong(parts[3]);
    }

    private String longToIp(long ip) {
        return ((ip >> 24) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 8) & 0xFF) + "."
                + (ip & 0xFF);
    }
}
