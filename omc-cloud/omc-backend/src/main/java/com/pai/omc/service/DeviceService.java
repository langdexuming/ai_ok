package com.pai.omc.service;

import com.pai.omc.common.PageResult;
import com.pai.omc.dto.DeviceDTO;
import com.pai.omc.dto.ImportResultDTO;
import com.pai.omc.dto.SubnetSearchDTO;
import com.pai.omc.query.DeviceQuery;
import com.pai.omc.vo.DeviceVO;
import com.pai.omc.vo.DeviceTypeVO;
import com.pai.omc.vo.DeviceSubtypeVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 设备管理服务接口
 *
 * @author make java
 * @since 2026-03-08
 */
public interface DeviceService {

    /**
     * 搜索当前网段设备
     *
     * @return 搜索到的设备列表
     */
    List<DeviceVO> searchCurrentSubnet();

    /**
     * 搜索其他网段设备
     *
     * @param dto 网段参数
     * @return 搜索到的设备列表
     */
    List<DeviceVO> searchOtherSubnet(SubnetSearchDTO dto);

    /**
     * 加入设备列表
     *
     * @param deviceVO 设备信息
     */
    void addToDeviceList(DeviceVO deviceVO);

    /**
     * 批量加入设备列表
     *
     * @param deviceList 设备列表
     */
    void batchAddToDeviceList(List<DeviceVO> deviceList);

    /**
     * 分页查询设备列表
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<DeviceVO> pageDevice(DeviceQuery query);

    /**
     * 新增设备
     *
     * @param dto 设备信息
     * @return 设备详情
     */
    DeviceVO addDevice(DeviceDTO dto);

    /**
     * 编辑设备
     *
     * @param dto 设备信息
     * @return 设备详情
     */
    DeviceVO updateDevice(DeviceDTO dto);

    /**
     * 删除设备
     *
     * @param id 设备ID
     */
    void deleteDevice(Long id);

    /**
     * 批量删除设备
     *
     * @param ids ID列表
     */
    void batchDeleteDevice(List<Long> ids);

    /**
     * 导入设备(Excel,增量)
     *
     * @param file Excel文件
     * @return 导入结果
     */
    ImportResultDTO importDevice(MultipartFile file);

    /**
     * 导出设备
     *
     * @param query    查询参数
     * @param response HTTP响应
     */
    void exportDevice(DeviceQuery query, HttpServletResponse response);

    /**
     * 获取设备类型列表
     *
     * @return 设备类型列表
     */
    List<DeviceTypeVO> listDeviceTypes();

    /**
     * 获取设备子类列表
     *
     * @param typeId 设备类型ID
     * @return 设备子类列表
     */
    List<DeviceSubtypeVO> listDeviceSubtypes(String typeId);
}
