package com.pai.omc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pai.omc.common.PageResult;
import com.pai.omc.dto.ImportResultDTO;
import com.pai.omc.dto.VerifyConfigDTO;
import com.pai.omc.entity.VerifyConfig;
import com.pai.omc.exception.BusinessException;
import com.pai.omc.mapper.VerifyConfigMapper;
import com.pai.omc.query.VerifyConfigQuery;
import com.pai.omc.service.VerifyConfigService;
import com.pai.omc.vo.VerifyConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 校核配置服务实现
 *
 * @author make java
 * @since 2026-03-08
 */
@Slf4j
@Service
public class VerifyConfigServiceImpl implements VerifyConfigService {

    @Resource
    private VerifyConfigMapper verifyConfigMapper;

    @Override
    public PageResult<VerifyConfigVO> pageVerifyConfig(VerifyConfigQuery query) {
        LambdaQueryWrapper<VerifyConfig> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getDeviceTypeName())) {
            wrapper.like(VerifyConfig::getDeviceTypeName, query.getDeviceTypeName());
        }
        if (StrUtil.isNotBlank(query.getDeviceSubtypeName())) {
            wrapper.like(VerifyConfig::getDeviceSubtypeName, query.getDeviceSubtypeName());
        }
        if (StrUtil.isNotBlank(query.getChannelName())) {
            wrapper.like(VerifyConfig::getChannelName, query.getChannelName());
        }
        wrapper.orderByAsc(VerifyConfig::getDeviceTypeName, VerifyConfig::getDeviceSubtypeName,
                VerifyConfig::getChannelCode);

        Page<VerifyConfig> page = verifyConfigMapper.selectPage(
                new Page<>(query.getCurrent(), query.getSize()), wrapper);

        List<VerifyConfigVO> voList = page.getRecords().stream()
                .map(entity -> BeanUtil.copyProperties(entity, VerifyConfigVO.class))
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), voList, page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addVerifyConfig(VerifyConfigDTO dto) {
        LambdaQueryWrapper<VerifyConfig> qw = new LambdaQueryWrapper<>();
        qw.eq(VerifyConfig::getDeviceSubtypeId, dto.getDeviceSubtypeId());
        qw.eq(VerifyConfig::getChannelCode, dto.getChannelCode());
        Long count = verifyConfigMapper.selectCount(qw);
        if (count > 0) {
            throw new BusinessException("该设备子类+信号ID已存在");
        }

        VerifyConfig config = BeanUtil.copyProperties(dto, VerifyConfig.class);
        verifyConfigMapper.insert(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVerifyConfig(VerifyConfigDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("ID不能为空");
        }

        LambdaQueryWrapper<VerifyConfig> qw = new LambdaQueryWrapper<>();
        qw.eq(VerifyConfig::getDeviceSubtypeId, dto.getDeviceSubtypeId());
        qw.eq(VerifyConfig::getChannelCode, dto.getChannelCode());
        qw.ne(VerifyConfig::getId, dto.getId());
        Long count = verifyConfigMapper.selectCount(qw);
        if (count > 0) {
            throw new BusinessException("该设备子类+信号ID已存在");
        }

        VerifyConfig config = BeanUtil.copyProperties(dto, VerifyConfig.class);
        verifyConfigMapper.updateById(config);
    }

    @Override
    public void deleteVerifyConfig(Long id) {
        verifyConfigMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteVerifyConfig(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请先选择数据");
        }
        verifyConfigMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultDTO importVerifyConfig(MultipartFile file) {
        try {
            return ImportResultDTO.success(0);
        } catch (Exception e) {
            log.error("导入校核配置异常：", e);
            throw new BusinessException("导入失败：" + e.getMessage());
        }
    }

    @Override
    public void exportVerifyConfig(VerifyConfigQuery query, HttpServletResponse response) {
        try {
            LambdaQueryWrapper<VerifyConfig> wrapper = new LambdaQueryWrapper<>();
            if (StrUtil.isNotBlank(query.getDeviceTypeName())) {
                wrapper.like(VerifyConfig::getDeviceTypeName, query.getDeviceTypeName());
            }
            if (StrUtil.isNotBlank(query.getDeviceSubtypeName())) {
                wrapper.like(VerifyConfig::getDeviceSubtypeName, query.getDeviceSubtypeName());
            }
            if (StrUtil.isNotBlank(query.getChannelName())) {
                wrapper.like(VerifyConfig::getChannelName, query.getChannelName());
            }
            List<VerifyConfig> list = verifyConfigMapper.selectList(wrapper);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("校核配置.xlsx", StandardCharsets.UTF_8.name()));

            com.alibaba.excel.EasyExcel.write(response.getOutputStream(), VerifyConfig.class)
                    .sheet("校核配置")
                    .doWrite(list);
        } catch (Exception e) {
            log.error("导出校核配置异常：", e);
            throw new BusinessException("导出失败");
        }
    }

    @Override
    public List<VerifyConfigVO> getAllVerifyConfig() {
        List<VerifyConfig> list = verifyConfigMapper.selectList(new LambdaQueryWrapper<>());
        return list.stream()
                .map(entity -> BeanUtil.copyProperties(entity, VerifyConfigVO.class))
                .collect(Collectors.toList());
    }
}
