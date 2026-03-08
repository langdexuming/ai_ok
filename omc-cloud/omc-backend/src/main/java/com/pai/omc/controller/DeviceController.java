package com.pai.omc.controller;

import com.pai.omc.common.PageResult;
import com.pai.omc.common.Result;
import com.pai.omc.dto.DeviceDTO;
import com.pai.omc.dto.ImportResultDTO;
import com.pai.omc.dto.SubnetSearchDTO;
import com.pai.omc.query.DeviceQuery;
import com.pai.omc.service.DeviceService;
import com.pai.omc.vo.DeviceSubtypeVO;
import com.pai.omc.vo.DeviceTypeVO;
import com.pai.omc.vo.DeviceVO;
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
 * 设备管理控制器
 *
 * @author make java
 * @since 2026-03-08
 */
@Slf4j
@RestController
@RequestMapping("/api/device")
@Api(tags = "设备管理")
public class DeviceController {

    @Resource
    private DeviceService deviceService;

    /**
     * 搜索当前网段设备
     *
     * @return 搜索到的设备列表
     */
    @PostMapping("/search-current")
    @ApiOperation("搜索当前网段设备")
    public Result<List<DeviceVO>> searchCurrentSubnet() {
        return Result.success(deviceService.searchCurrentSubnet());
    }

    /**
     * 搜索其他网段设备
     *
     * @param dto 网段参数
     * @return 搜索到的设备列表
     */
    @PostMapping("/search-other")
    @ApiOperation("搜索其他网段设备")
    public Result<List<DeviceVO>> searchOtherSubnet(@Valid @RequestBody SubnetSearchDTO dto) {
        return Result.success(deviceService.searchOtherSubnet(dto));
    }

    /**
     * 加入设备列表
     *
     * @param deviceVO 设备信息
     * @return 操作结果
     */
    @PostMapping("/add-to-list")
    @ApiOperation("加入设备列表")
    public Result<Void> addToDeviceList(@RequestBody DeviceVO deviceVO) {
        deviceService.addToDeviceList(deviceVO);
        return Result.success();
    }

    /**
     * 批量加入设备列表
     *
     * @param deviceList 设备列表
     * @return 操作结果
     */
    @PostMapping("/batch-add-to-list")
    @ApiOperation("批量加入设备列表")
    public Result<Void> batchAddToDeviceList(@RequestBody List<DeviceVO> deviceList) {
        deviceService.batchAddToDeviceList(deviceList);
        return Result.success();
    }

    /**
     * 分页查询设备列表
     *
     * @param query 查询参数
     * @return 分页结果
     */
    @GetMapping("/page")
    @ApiOperation("分页查询设备列表")
    public Result<PageResult<DeviceVO>> page(DeviceQuery query) {
        return Result.success(deviceService.pageDevice(query));
    }

    /**
     * 新增设备
     *
     * @param dto 设备信息
     * @return 设备详情
     */
    @PostMapping("/add")
    @ApiOperation("新增设备")
    public Result<DeviceVO> add(@Valid @RequestBody DeviceDTO dto) {
        return Result.success(deviceService.addDevice(dto));
    }

    /**
     * 编辑设备
     *
     * @param dto 设备信息
     * @return 设备详情
     */
    @PutMapping("/update")
    @ApiOperation("编辑设备")
    public Result<DeviceVO> update(@Valid @RequestBody DeviceDTO dto) {
        return Result.success(deviceService.updateDevice(dto));
    }

    /**
     * 删除设备
     *
     * @param id 设备ID
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    @ApiOperation("删除设备")
    public Result<Void> delete(@RequestParam Long id) {
        deviceService.deleteDevice(id);
        return Result.success("删除成功", null);
    }

    /**
     * 批量删除设备
     *
     * @param ids ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch-delete")
    @ApiOperation("批量删除设备")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        deviceService.batchDeleteDevice(ids);
        return Result.success("删除成功", null);
    }

    /**
     * 导入设备
     *
     * @param file Excel文件
     * @return 导入结果
     */
    @PostMapping("/import")
    @ApiOperation("导入设备(Excel)")
    public Result<ImportResultDTO> importDevice(@RequestParam("file") MultipartFile file) {
        return Result.success(deviceService.importDevice(file));
    }

    /**
     * 导出设备
     *
     * @param query    查询参数
     * @param response HTTP响应
     */
    @GetMapping("/export")
    @ApiOperation("导出设备(Excel)")
    public void export(DeviceQuery query, HttpServletResponse response) {
        deviceService.exportDevice(query, response);
    }

    /**
     * 获取设备类型列表
     *
     * @return 设备类型列表
     */
    @GetMapping("/device-type/list")
    @ApiOperation("获取设备类型列表")
    public Result<List<DeviceTypeVO>> listDeviceTypes() {
        return Result.success(deviceService.listDeviceTypes());
    }

    /**
     * 获取设备子类列表
     *
     * @param typeId 设备类型ID
     * @return 设备子类列表
     */
    @GetMapping("/device-subtype/list")
    @ApiOperation("获取设备子类列表")
    public Result<List<DeviceSubtypeVO>> listDeviceSubtypes(@RequestParam(required = false) String typeId) {
        return Result.success(deviceService.listDeviceSubtypes(typeId));
    }
}
