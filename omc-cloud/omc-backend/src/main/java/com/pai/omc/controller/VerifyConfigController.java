package com.pai.omc.controller;

import com.pai.omc.common.PageResult;
import com.pai.omc.common.Result;
import com.pai.omc.dto.ImportResultDTO;
import com.pai.omc.dto.VerifyConfigDTO;
import com.pai.omc.query.VerifyConfigQuery;
import com.pai.omc.service.VerifyConfigService;
import com.pai.omc.vo.VerifyConfigVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * 校核配置控制器
 *
 * @author make java
 * @since 2026-03-08
 */
@Slf4j
@RestController
@RequestMapping("/api/verify-config")
@Api(tags = "校核配置")
public class VerifyConfigController {

    @Resource
    private VerifyConfigService verifyConfigService;

    /**
     * 分页查询校核配置
     *
     * @param query 查询参数
     * @return 分页结果
     */
    @GetMapping("/page")
    @ApiOperation("分页查询校核配置")
    public Result<PageResult<VerifyConfigVO>> page(VerifyConfigQuery query) {
        return Result.success(verifyConfigService.pageVerifyConfig(query));
    }

    /**
     * 新增校核配置
     *
     * @param dto 配置信息
     * @return 操作结果
     */
    @PostMapping("/add")
    @ApiOperation("新增校核配置")
    public Result<Void> add(@Valid @RequestBody VerifyConfigDTO dto) {
        verifyConfigService.addVerifyConfig(dto);
        return Result.success("新增成功", null);
    }

    /**
     * 编辑校核配置
     *
     * @param dto 配置信息
     * @return 操作结果
     */
    @PutMapping("/update")
    @ApiOperation("编辑校核配置")
    public Result<Void> update(@Valid @RequestBody VerifyConfigDTO dto) {
        verifyConfigService.updateVerifyConfig(dto);
        return Result.success("编辑成功", null);
    }

    /**
     * 删除校核配置
     *
     * @param id 配置ID
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    @ApiOperation("删除校核配置")
    public Result<Void> delete(@RequestParam Long id) {
        verifyConfigService.deleteVerifyConfig(id);
        return Result.success("删除成功", null);
    }

    /**
     * 批量删除校核配置
     *
     * @param ids ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch-delete")
    @ApiOperation("批量删除校核配置")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        verifyConfigService.batchDeleteVerifyConfig(ids);
        return Result.success("删除成功", null);
    }

    /**
     * 导入校核配置
     *
     * @param file Excel文件
     * @return 导入结果
     */
    @PostMapping("/import")
    @ApiOperation("导入校核配置(Excel,增量)")
    public Result<ImportResultDTO> importConfig(@RequestParam("file") MultipartFile file) {
        return Result.success(verifyConfigService.importVerifyConfig(file));
    }

    /**
     * 导出校核配置
     *
     * @param query    查询参数
     * @param response HTTP响应
     */
    @GetMapping("/export")
    @ApiOperation("导出校核配置")
    public void export(VerifyConfigQuery query, HttpServletResponse response) {
        verifyConfigService.exportVerifyConfig(query, response);
    }

    /**
     * 获取所有校核配置(供边端同步)
     *
     * @return 校核配置列表
     */
    @GetMapping("/omc/verify-config/list")
    @ApiOperation("获取所有校核配置(供边端同步)")
    public Result<List<VerifyConfigVO>> getAllVerifyConfig() {
        return Result.success(verifyConfigService.getAllVerifyConfig());
    }
}
