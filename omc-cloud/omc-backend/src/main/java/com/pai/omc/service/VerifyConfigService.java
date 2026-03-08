package com.pai.omc.service;

import com.pai.omc.common.PageResult;
import com.pai.omc.dto.ImportResultDTO;
import com.pai.omc.dto.VerifyConfigDTO;
import com.pai.omc.query.VerifyConfigQuery;
import com.pai.omc.vo.VerifyConfigVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 校核配置服务接口
 *
 * @author make java
 * @since 2026-03-08
 */
public interface VerifyConfigService {

    /**
     * 分页查询校核配置
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<VerifyConfigVO> pageVerifyConfig(VerifyConfigQuery query);

    /**
     * 新增校核配置
     *
     * @param dto 配置信息
     */
    void addVerifyConfig(VerifyConfigDTO dto);

    /**
     * 编辑校核配置
     *
     * @param dto 配置信息
     */
    void updateVerifyConfig(VerifyConfigDTO dto);

    /**
     * 删除校核配置
     *
     * @param id 配置ID
     */
    void deleteVerifyConfig(Long id);

    /**
     * 批量删除校核配置
     *
     * @param ids ID列表
     */
    void batchDeleteVerifyConfig(List<Long> ids);

    /**
     * 导入校核配置(Excel,增量)
     *
     * @param file Excel文件
     * @return 导入结果
     */
    ImportResultDTO importVerifyConfig(MultipartFile file);

    /**
     * 导出校核配置
     *
     * @param query    查询参数
     * @param response HTTP响应
     */
    void exportVerifyConfig(VerifyConfigQuery query, HttpServletResponse response);

    /**
     * 获取所有校核配置(供边端同步)
     *
     * @return 校核配置列表
     */
    List<VerifyConfigVO> getAllVerifyConfig();
}
