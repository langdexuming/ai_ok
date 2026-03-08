package com.pai.omc.controller;

import com.pai.omc.common.PageResult;
import com.pai.omc.common.Result;
import com.pai.omc.dto.ImportResultDTO;
import com.pai.omc.query.DynamicLibraryQuery;
import com.pai.omc.query.TotalFrequencyQuery;
import com.pai.omc.query.SerialFrequencyQuery;
import com.pai.omc.service.DynamicLibraryService;
import com.pai.omc.vo.DynamicLibraryVO;
import com.pai.omc.vo.TotalFrequencyVO;
import com.pai.omc.vo.SerialFrequencyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 识别库管理控制器
 *
 * @author make java
 * @since 2026-03-08
 */
@Slf4j
@RestController
@RequestMapping("/api")
@Api(tags = "识别库管理")
public class DynamicLibraryController {

    @Resource
    private DynamicLibraryService dynamicLibraryService;

    /**
     * 分页查询动态库列表
     *
     * @param query 查询参数
     * @return 分页结果
     */
    @GetMapping("/library/page")
    @ApiOperation("分页查询动态库列表")
    public Result<PageResult<DynamicLibraryVO>> pageDynamicLibrary(DynamicLibraryQuery query) {
        return Result.success(dynamicLibraryService.pageDynamicLibrary(query));
    }

    /**
     * 导入动态库列表
     *
     * @param file CSV文件
     * @return 导入结果
     */
    @PostMapping("/library/import-list")
    @ApiOperation("导入动态库列表(CSV)")
    public Result<ImportResultDTO> importLibraryList(@RequestParam("file") MultipartFile file) {
        return Result.success(dynamicLibraryService.importLibraryList(file));
    }

    /**
     * 导入动态库文件
     *
     * @param files 动态库文件
     * @return 导入结果
     */
    @PostMapping("/library/import-file")
    @ApiOperation("导入动态库文件")
    public Result<ImportResultDTO> importLibraryFiles(@RequestParam("files") MultipartFile[] files) {
        return Result.success(dynamicLibraryService.importLibraryFiles(files));
    }

    /**
     * 导出动态库列表
     *
     * @param query    查询参数
     * @param response HTTP响应
     */
    @GetMapping("/library/export")
    @ApiOperation("导出动态库列表(CSV)")
    public void exportLibraryList(DynamicLibraryQuery query, HttpServletResponse response) {
        dynamicLibraryService.exportLibraryList(query, response);
    }

    /**
     * 下载动态库文件
     *
     * @param id       动态库ID
     * @param response HTTP响应
     */
    @GetMapping("/library/download/{id}")
    @ApiOperation("下载动态库文件")
    public void downloadLibraryFile(@PathVariable Long id, HttpServletResponse response) {
        dynamicLibraryService.downloadLibraryFile(id, response);
    }

    /**
     * 查看动态库配置
     *
     * @param id 动态库ID
     * @return 配置内容
     */
    @GetMapping("/library/view/{id}")
    @ApiOperation("查看动态库配置")
    public Result<String> viewLibraryConfig(@PathVariable Long id) {
        return Result.success(dynamicLibraryService.viewLibraryConfig(id));
    }

    /**
     * 分页查询总使用频率
     *
     * @param query 查询参数
     * @return 分页结果
     */
    @GetMapping("/frequency/total/page")
    @ApiOperation("分页查询总使用频率")
    public Result<PageResult<TotalFrequencyVO>> pageTotalFrequency(TotalFrequencyQuery query) {
        return Result.success(dynamicLibraryService.pageTotalFrequency(query));
    }

    /**
     * 导入总使用频率
     *
     * @param file Excel文件
     * @return 导入结果
     */
    @PostMapping("/frequency/total/import")
    @ApiOperation("导入总使用频率(Excel,全量)")
    public Result<ImportResultDTO> importTotalFrequency(@RequestParam("file") MultipartFile file) {
        return Result.success(dynamicLibraryService.importTotalFrequency(file));
    }

    /**
     * 导出总使用频率
     *
     * @param query    查询参数
     * @param response HTTP响应
     */
    @GetMapping("/frequency/total/export")
    @ApiOperation("导出总使用频率")
    public void exportTotalFrequency(TotalFrequencyQuery query, HttpServletResponse response) {
        dynamicLibraryService.exportTotalFrequency(query, response);
    }

    /**
     * 分页查询串口使用频率
     *
     * @param query 查询参数
     * @return 分页结果
     */
    @GetMapping("/frequency/serial/page")
    @ApiOperation("分页查询串口使用频率")
    public Result<PageResult<SerialFrequencyVO>> pageSerialFrequency(SerialFrequencyQuery query) {
        return Result.success(dynamicLibraryService.pageSerialFrequency(query));
    }

    /**
     * 导入串口使用频率
     *
     * @param file Excel文件
     * @return 导入结果
     */
    @PostMapping("/frequency/serial/import")
    @ApiOperation("导入串口使用频率(Excel,全量)")
    public Result<ImportResultDTO> importSerialFrequency(@RequestParam("file") MultipartFile file) {
        return Result.success(dynamicLibraryService.importSerialFrequency(file));
    }

    /**
     * 导出串口使用频率
     *
     * @param query    查询参数
     * @param response HTTP响应
     */
    @GetMapping("/frequency/serial/export")
    @ApiOperation("导出串口使用频率")
    public void exportSerialFrequency(SerialFrequencyQuery query, HttpServletResponse response) {
        dynamicLibraryService.exportSerialFrequency(query, response);
    }

    /**
     * 获取所有识别库数据(供边端同步)
     *
     * @return 动态库列表
     */
    @GetMapping("/omc/library/list")
    @ApiOperation("获取所有识别库数据(供边端同步)")
    public Result<List<DynamicLibraryVO>> getAllLibraryData() {
        return Result.success(dynamicLibraryService.getAllLibraryData());
    }
}
