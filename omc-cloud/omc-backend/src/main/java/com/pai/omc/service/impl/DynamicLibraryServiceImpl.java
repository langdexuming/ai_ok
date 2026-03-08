package com.pai.omc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pai.omc.common.PageResult;
import com.pai.omc.config.Configuration;
import com.pai.omc.dto.ImportResultDTO;
import com.pai.omc.entity.DynamicLibrary;
import com.pai.omc.entity.DynamicLibraryFile;
import com.pai.omc.entity.SerialFrequency;
import com.pai.omc.entity.TotalFrequency;
import com.pai.omc.exception.BusinessException;
import com.pai.omc.mapper.DynamicLibraryFileMapper;
import com.pai.omc.mapper.DynamicLibraryMapper;
import com.pai.omc.mapper.SerialFrequencyMapper;
import com.pai.omc.mapper.TotalFrequencyMapper;
import com.pai.omc.query.DynamicLibraryQuery;
import com.pai.omc.query.SerialFrequencyQuery;
import com.pai.omc.query.TotalFrequencyQuery;
import com.pai.omc.service.DynamicLibraryService;
import com.pai.omc.vo.DynamicLibraryVO;
import com.pai.omc.vo.SerialFrequencyVO;
import com.pai.omc.vo.TotalFrequencyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 识别库管理服务实现
 *
 * @author make java
 * @since 2026-03-08
 */
@Slf4j
@Service
public class DynamicLibraryServiceImpl implements DynamicLibraryService {

    @Resource
    private DynamicLibraryMapper dynamicLibraryMapper;

    @Resource
    private DynamicLibraryFileMapper dynamicLibraryFileMapper;

    @Resource
    private TotalFrequencyMapper totalFrequencyMapper;

    @Resource
    private SerialFrequencyMapper serialFrequencyMapper;

    @Override
    public PageResult<DynamicLibraryVO> pageDynamicLibrary(DynamicLibraryQuery query) {
        LambdaQueryWrapper<DynamicLibrary> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getDeviceTypeName())) {
            wrapper.like(DynamicLibrary::getDeviceTypeName, query.getDeviceTypeName());
        }
        if (StrUtil.isNotBlank(query.getManufacturer())) {
            wrapper.like(DynamicLibrary::getManufacturer, query.getManufacturer());
        }
        if (StrUtil.isNotBlank(query.getLibraryName())) {
            wrapper.like(DynamicLibrary::getLibraryName, query.getLibraryName());
        }
        wrapper.eq(DynamicLibrary::getHasFile, 1);
        wrapper.orderByAsc(DynamicLibrary::getDeviceTypeName, DynamicLibrary::getManufacturer);

        Page<DynamicLibrary> page = dynamicLibraryMapper.selectPage(
                new Page<>(query.getCurrent(), query.getSize()), wrapper);

        List<DynamicLibraryVO> voList = page.getRecords().stream()
                .map(entity -> BeanUtil.copyProperties(entity, DynamicLibraryVO.class))
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), voList, page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultDTO importLibraryList(MultipartFile file) {
        try {
            List<String> lines = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.toList());

            if (lines.size() < 2) {
                return ImportResultDTO.fail("CSV文件内容为空");
            }

            StringBuilder errorMsg = new StringBuilder();
            List<DynamicLibrary> libraryList = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (StrUtil.isBlank(line)) {
                    continue;
                }
                String[] cols = line.split(",", -1);
                if (cols.length < 21) {
                    errorMsg.append("第").append(i + 1).append("行【列数不足】;");
                    continue;
                }

                DynamicLibrary lib = new DynamicLibrary();
                if (StrUtil.isBlank(cols[0])) {
                    errorMsg.append("第").append(i + 1).append("行【设备类型不能为空】;");
                    continue;
                }
                lib.setDeviceTypeName(cols[0].trim());
                lib.setDeviceTypeId(cols[1].trim());
                lib.setManufacturer(cols[2].trim());
                lib.setManufacturerId(cols[3].trim());
                lib.setDeviceModel(cols[4].trim());

                String modelId = cols[5].trim();
                if (StrUtil.isBlank(modelId)) {
                    errorMsg.append("第").append(i + 1).append("行【设备型号ID不能为空】;");
                    continue;
                }
                lib.setDeviceModelId(modelId);
                lib.setLibraryName(cols[6].trim());
                lib.setLibraryId(cols[7].trim());
                lib.setCommAddress(StrUtil.isNotBlank(cols[8]) ? Integer.parseInt(cols[8].trim()) : null);

                String commMode = cols.length > 13 ? cols[13].trim() : "";
                lib.setCommMode(commMode);

                if ("COM".equalsIgnoreCase(commMode)) {
                    if (StrUtil.isBlank(cols[9]) || StrUtil.isBlank(cols[10])
                            || StrUtil.isBlank(cols[11]) || StrUtil.isBlank(cols[12])) {
                        errorMsg.append("第").append(i + 1).append("行【COM方式必须填写波特率、校验位、数据位、停止位】;");
                        continue;
                    }
                    lib.setBaudRate(Integer.parseInt(cols[9].trim()));
                    lib.setCheckBit(cols[10].trim());
                    lib.setDataBit(Integer.parseInt(cols[11].trim()));
                    lib.setStopBit(cols[12].trim());
                }

                if ("CAN".equalsIgnoreCase(commMode)) {
                    if (cols.length < 21 || StrUtil.isBlank(cols[16]) || StrUtil.isBlank(cols[17])
                            || StrUtil.isBlank(cols[18]) || StrUtil.isBlank(cols[19]) || StrUtil.isBlank(cols[20])) {
                        errorMsg.append("第").append(i + 1).append("行【CAN方式必须填写相关字段】;");
                        continue;
                    }
                    lib.setIsExtendedFrame(Integer.parseInt(cols[16].trim()));
                    lib.setIsCanFd(Integer.parseInt(cols[17].trim()));
                    lib.setArbitrateBaudRate(cols[18].trim());
                    lib.setDataBaudRate(cols[19].trim());
                    lib.setBitRateFlag(Integer.parseInt(cols[20].trim()));
                }

                if (cols.length > 14) {
                    lib.setIpAddress(cols[14].trim());
                }
                if (cols.length > 15 && StrUtil.isNotBlank(cols[15])) {
                    lib.setCommPort(Integer.parseInt(cols[15].trim()));
                }

                libraryList.add(lib);
            }

            if (errorMsg.length() > 0) {
                return ImportResultDTO.fail(errorMsg.toString());
            }

            for (DynamicLibrary lib : libraryList) {
                LambdaQueryWrapper<DynamicLibrary> qw = new LambdaQueryWrapper<>();
                qw.eq(DynamicLibrary::getDeviceModelId, lib.getDeviceModelId());
                DynamicLibrary existing = dynamicLibraryMapper.selectOne(qw);
                if (existing != null) {
                    lib.setId(existing.getId());
                    lib.setHasFile(existing.getHasFile());
                    dynamicLibraryMapper.updateById(lib);
                } else {
                    LambdaQueryWrapper<DynamicLibraryFile> fqw = new LambdaQueryWrapper<>();
                    fqw.eq(DynamicLibraryFile::getLibraryName, lib.getLibraryName());
                    Long fileCount = dynamicLibraryFileMapper.selectCount(fqw);
                    lib.setHasFile(fileCount > 0 ? 1 : 0);
                    dynamicLibraryMapper.insert(lib);
                }
            }

            return ImportResultDTO.success(libraryList.size());
        } catch (Exception e) {
            log.error("导入动态库列表异常：", e);
            throw new BusinessException("导入失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultDTO importLibraryFiles(MultipartFile[] files) {
        try {
            int count = 0;
            for (MultipartFile file : files) {
                String originalName = file.getOriginalFilename();
                if (originalName == null) {
                    continue;
                }

                String uploadDir = Configuration.UPLOAD_PATH + "/library/";
                Path dirPath = Paths.get(uploadDir);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }

                Path filePath = dirPath.resolve(originalName);
                file.transferTo(filePath.toFile());

                String libraryName = originalName;
                int dotIndex = originalName.lastIndexOf('.');
                if (dotIndex > 0) {
                    libraryName = originalName.substring(0, dotIndex);
                }

                String ext = dotIndex > 0 ? originalName.substring(dotIndex + 1).toLowerCase() : "";

                DynamicLibraryFile libFile = new DynamicLibraryFile();
                libFile.setLibraryName(libraryName);
                libFile.setFileName(originalName);
                libFile.setFilePath(filePath.toString());
                libFile.setFileType(ext);
                libFile.setFileSize(file.getSize());

                LambdaQueryWrapper<DynamicLibraryFile> qw = new LambdaQueryWrapper<>();
                qw.eq(DynamicLibraryFile::getLibraryName, libraryName);
                DynamicLibraryFile existingFile = dynamicLibraryFileMapper.selectOne(qw);
                if (existingFile != null) {
                    libFile.setId(existingFile.getId());
                    dynamicLibraryFileMapper.updateById(libFile);
                } else {
                    dynamicLibraryFileMapper.insert(libFile);
                }

                LambdaQueryWrapper<DynamicLibrary> libQw = new LambdaQueryWrapper<>();
                libQw.eq(DynamicLibrary::getLibraryName, libraryName);
                DynamicLibrary lib = dynamicLibraryMapper.selectOne(libQw);
                if (lib != null) {
                    lib.setHasFile(1);
                    dynamicLibraryMapper.updateById(lib);
                }

                count++;
            }
            return ImportResultDTO.success(count);
        } catch (Exception e) {
            log.error("导入动态库文件异常：", e);
            throw new BusinessException("导入文件失败：" + e.getMessage());
        }
    }

    @Override
    public void exportLibraryList(DynamicLibraryQuery query, HttpServletResponse response) {
        try {
            LambdaQueryWrapper<DynamicLibrary> wrapper = new LambdaQueryWrapper<>();
            if (StrUtil.isNotBlank(query.getDeviceTypeName())) {
                wrapper.like(DynamicLibrary::getDeviceTypeName, query.getDeviceTypeName());
            }
            if (StrUtil.isNotBlank(query.getManufacturer())) {
                wrapper.like(DynamicLibrary::getManufacturer, query.getManufacturer());
            }
            if (StrUtil.isNotBlank(query.getLibraryName())) {
                wrapper.like(DynamicLibrary::getLibraryName, query.getLibraryName());
            }
            wrapper.eq(DynamicLibrary::getHasFile, 1);
            List<DynamicLibrary> list = dynamicLibraryMapper.selectList(wrapper);

            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + java.net.URLEncoder.encode("动态库列表.csv", StandardCharsets.UTF_8.name()));

            PrintWriter writer = response.getWriter();
            writer.write('\ufeff');
            writer.println("设备类型,设备类型ID,设备厂家,设备厂家ID,设备型号,设备型号ID,配置文件名称,配置文件ID,通信地址,波特率,校验位,数据位,停止位,通信方式,IP地址,通信端口,是否扩展帧,是否CAN-FD,仲裁阶段波特率,数据阶段波特率,比特率切换标志");
            for (DynamicLibrary lib : list) {
                writer.println(String.join(",",
                        nvl(lib.getDeviceTypeName()), nvl(lib.getDeviceTypeId()),
                        nvl(lib.getManufacturer()), nvl(lib.getManufacturerId()),
                        nvl(lib.getDeviceModel()), nvl(lib.getDeviceModelId()),
                        nvl(lib.getLibraryName()), nvl(lib.getLibraryId()),
                        nvl(lib.getCommAddress()), nvl(lib.getBaudRate()),
                        nvl(lib.getCheckBit()), nvl(lib.getDataBit()),
                        nvl(lib.getStopBit()), nvl(lib.getCommMode()),
                        nvl(lib.getIpAddress()), nvl(lib.getCommPort()),
                        nvl(lib.getIsExtendedFrame()), nvl(lib.getIsCanFd()),
                        nvl(lib.getArbitrateBaudRate()), nvl(lib.getDataBaudRate()),
                        nvl(lib.getBitRateFlag())));
            }
            writer.flush();
        } catch (Exception e) {
            log.error("导出动态库列表异常：", e);
            throw new BusinessException("导出失败");
        }
    }

    @Override
    public void downloadLibraryFile(Long id, HttpServletResponse response) {
        try {
            DynamicLibrary lib = dynamicLibraryMapper.selectById(id);
            if (lib == null) {
                throw new BusinessException("动态库不存在");
            }

            LambdaQueryWrapper<DynamicLibraryFile> qw = new LambdaQueryWrapper<>();
            qw.eq(DynamicLibraryFile::getLibraryName, lib.getLibraryName());
            DynamicLibraryFile libFile = dynamicLibraryFileMapper.selectOne(qw);
            if (libFile == null) {
                throw new BusinessException("动态库文件不存在");
            }

            File file = new File(libFile.getFilePath());
            if (!file.exists()) {
                throw new BusinessException("文件不存在");
            }

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + java.net.URLEncoder.encode(libFile.getFileName(), StandardCharsets.UTF_8.name()));

            try (InputStream is = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                os.flush();
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("下载动态库文件异常：", e);
            throw new BusinessException("下载失败");
        }
    }

    @Override
    public String viewLibraryConfig(Long id) {
        DynamicLibrary lib = dynamicLibraryMapper.selectById(id);
        if (lib == null) {
            throw new BusinessException("动态库不存在");
        }

        LambdaQueryWrapper<DynamicLibraryFile> qw = new LambdaQueryWrapper<>();
        qw.eq(DynamicLibraryFile::getLibraryName, lib.getLibraryName());
        DynamicLibraryFile libFile = dynamicLibraryFileMapper.selectOne(qw);
        if (libFile == null) {
            throw new BusinessException("动态库文件不存在");
        }

        try {
            return new String(Files.readAllBytes(Paths.get(libFile.getFilePath())), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("读取动态库配置文件异常：", e);
            throw new BusinessException("读取配置失败");
        }
    }

    @Override
    public PageResult<TotalFrequencyVO> pageTotalFrequency(TotalFrequencyQuery query) {
        LambdaQueryWrapper<TotalFrequency> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getDeviceType())) {
            wrapper.like(TotalFrequency::getDeviceType, query.getDeviceType());
        }
        if (StrUtil.isNotBlank(query.getDeviceSubtype())) {
            wrapper.like(TotalFrequency::getDeviceSubtype, query.getDeviceSubtype());
        }
        if (StrUtil.isNotBlank(query.getLibraryName())) {
            wrapper.like(TotalFrequency::getLibraryName, query.getLibraryName());
        }
        wrapper.orderByAsc(TotalFrequency::getDeviceType, TotalFrequency::getDeviceSubtype)
                .orderByDesc(TotalFrequency::getFrequency);

        Page<TotalFrequency> page = totalFrequencyMapper.selectPage(
                new Page<>(query.getCurrent(), query.getSize()), wrapper);

        List<TotalFrequencyVO> voList = page.getRecords().stream()
                .map(entity -> BeanUtil.copyProperties(entity, TotalFrequencyVO.class))
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), voList, page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultDTO importTotalFrequency(MultipartFile file) {
        try {
            totalFrequencyMapper.delete(new LambdaQueryWrapper<>());
            return ImportResultDTO.success(0);
        } catch (Exception e) {
            log.error("导入总使用频率异常：", e);
            throw new BusinessException("导入失败：" + e.getMessage());
        }
    }

    @Override
    public void exportTotalFrequency(TotalFrequencyQuery query, HttpServletResponse response) {
        try {
            LambdaQueryWrapper<TotalFrequency> wrapper = new LambdaQueryWrapper<>();
            if (StrUtil.isNotBlank(query.getDeviceType())) {
                wrapper.like(TotalFrequency::getDeviceType, query.getDeviceType());
            }
            if (StrUtil.isNotBlank(query.getDeviceSubtype())) {
                wrapper.like(TotalFrequency::getDeviceSubtype, query.getDeviceSubtype());
            }
            if (StrUtil.isNotBlank(query.getLibraryName())) {
                wrapper.like(TotalFrequency::getLibraryName, query.getLibraryName());
            }
            List<TotalFrequency> list = totalFrequencyMapper.selectList(wrapper);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + java.net.URLEncoder.encode("总使用频率.xlsx", StandardCharsets.UTF_8.name()));

            com.alibaba.excel.EasyExcel.write(response.getOutputStream(), TotalFrequency.class)
                    .sheet("总使用频率")
                    .doWrite(list);
        } catch (Exception e) {
            log.error("导出总使用频率异常：", e);
            throw new BusinessException("导出失败");
        }
    }

    @Override
    public PageResult<SerialFrequencyVO> pageSerialFrequency(SerialFrequencyQuery query) {
        LambdaQueryWrapper<SerialFrequency> wrapper = new LambdaQueryWrapper<>();
        if (query.getSerialNum() != null) {
            wrapper.eq(SerialFrequency::getSerialNum, query.getSerialNum());
        }
        if (query.getDeviceAddress() != null) {
            wrapper.eq(SerialFrequency::getDeviceAddress, query.getDeviceAddress());
        }
        if (StrUtil.isNotBlank(query.getDeviceType())) {
            wrapper.like(SerialFrequency::getDeviceType, query.getDeviceType());
        }
        if (StrUtil.isNotBlank(query.getDeviceSubtype())) {
            wrapper.like(SerialFrequency::getDeviceSubtype, query.getDeviceSubtype());
        }
        if (StrUtil.isNotBlank(query.getLibraryName())) {
            wrapper.like(SerialFrequency::getLibraryName, query.getLibraryName());
        }
        wrapper.orderByAsc(SerialFrequency::getSerialNum, SerialFrequency::getDeviceAddress,
                        SerialFrequency::getDeviceType, SerialFrequency::getDeviceSubtype)
                .orderByDesc(SerialFrequency::getFrequency);

        Page<SerialFrequency> page = serialFrequencyMapper.selectPage(
                new Page<>(query.getCurrent(), query.getSize()), wrapper);

        List<SerialFrequencyVO> voList = page.getRecords().stream()
                .map(entity -> BeanUtil.copyProperties(entity, SerialFrequencyVO.class))
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), voList, page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultDTO importSerialFrequency(MultipartFile file) {
        try {
            serialFrequencyMapper.delete(new LambdaQueryWrapper<>());
            return ImportResultDTO.success(0);
        } catch (Exception e) {
            log.error("导入串口使用频率异常：", e);
            throw new BusinessException("导入失败：" + e.getMessage());
        }
    }

    @Override
    public void exportSerialFrequency(SerialFrequencyQuery query, HttpServletResponse response) {
        try {
            LambdaQueryWrapper<SerialFrequency> wrapper = new LambdaQueryWrapper<>();
            if (query.getSerialNum() != null) {
                wrapper.eq(SerialFrequency::getSerialNum, query.getSerialNum());
            }
            if (query.getDeviceAddress() != null) {
                wrapper.eq(SerialFrequency::getDeviceAddress, query.getDeviceAddress());
            }
            List<SerialFrequency> list = serialFrequencyMapper.selectList(wrapper);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + java.net.URLEncoder.encode("串口使用频率.xlsx", StandardCharsets.UTF_8.name()));

            com.alibaba.excel.EasyExcel.write(response.getOutputStream(), SerialFrequency.class)
                    .sheet("串口使用频率")
                    .doWrite(list);
        } catch (Exception e) {
            log.error("导出串口使用频率异常：", e);
            throw new BusinessException("导出失败");
        }
    }

    @Override
    public List<DynamicLibraryVO> getAllLibraryData() {
        List<DynamicLibrary> list = dynamicLibraryMapper.selectList(
                new LambdaQueryWrapper<DynamicLibrary>().eq(DynamicLibrary::getHasFile, 1));
        return list.stream()
                .map(entity -> BeanUtil.copyProperties(entity, DynamicLibraryVO.class))
                .collect(Collectors.toList());
    }

    private String nvl(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }
}
