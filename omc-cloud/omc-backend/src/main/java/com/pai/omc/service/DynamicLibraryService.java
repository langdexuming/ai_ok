package com.pai.omc.service;

import com.pai.omc.common.PageResult;
import com.pai.omc.dto.ImportResultDTO;
import com.pai.omc.query.DynamicLibraryQuery;
import com.pai.omc.query.TotalFrequencyQuery;
import com.pai.omc.query.SerialFrequencyQuery;
import com.pai.omc.vo.DynamicLibraryVO;
import com.pai.omc.vo.TotalFrequencyVO;
import com.pai.omc.vo.SerialFrequencyVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 识别库管理服务接口
 *
 * @author make java
 * @since 2026-03-08
 */
public interface DynamicLibraryService {

    /**
     * 分页查询动态库列表
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<DynamicLibraryVO> pageDynamicLibrary(DynamicLibraryQuery query);

    /**
     * 导入动态库列表(CSV)
     *
     * @param file CSV文件
     * @return 导入结果
     */
    ImportResultDTO importLibraryList(MultipartFile file);

    /**
     * 导入动态库文件
     *
     * @param files 动态库文件数组
     * @return 导入结果
     */
    ImportResultDTO importLibraryFiles(MultipartFile[] files);

    /**
     * 导出动态库列表
     *
     * @param query    查询参数
     * @param response HTTP响应
     */
    void exportLibraryList(DynamicLibraryQuery query, HttpServletResponse response);

    /**
     * 下载动态库文件
     *
     * @param id       动态库ID
     * @param response HTTP响应
     */
    void downloadLibraryFile(Long id, HttpServletResponse response);

    /**
     * 查看动态库配置内容
     *
     * @param id 动态库ID
     * @return 配置文件内容
     */
    String viewLibraryConfig(Long id);

    /**
     * 分页查询总使用频率
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<TotalFrequencyVO> pageTotalFrequency(TotalFrequencyQuery query);

    /**
     * 导入总使用频率(Excel,全量)
     *
     * @param file Excel文件
     * @return 导入结果
     */
    ImportResultDTO importTotalFrequency(MultipartFile file);

    /**
     * 导出总使用频率
     *
     * @param query    查询参数
     * @param response HTTP响应
     */
    void exportTotalFrequency(TotalFrequencyQuery query, HttpServletResponse response);

    /**
     * 分页查询串口使用频率
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SerialFrequencyVO> pageSerialFrequency(SerialFrequencyQuery query);

    /**
     * 导入串口使用频率(Excel,全量)
     *
     * @param file Excel文件
     * @return 导入结果
     */
    ImportResultDTO importSerialFrequency(MultipartFile file);

    /**
     * 导出串口使用频率
     *
     * @param query    查询参数
     * @param response HTTP响应
     */
    void exportSerialFrequency(SerialFrequencyQuery query, HttpServletResponse response);

    /**
     * 获取所有识别库数据(供边端同步)
     *
     * @return 动态库列表
     */
    List<DynamicLibraryVO> getAllLibraryData();
}
