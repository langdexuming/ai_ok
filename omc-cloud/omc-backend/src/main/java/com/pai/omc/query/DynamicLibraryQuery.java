package com.pai.omc.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 动态库查询参数
 *
 * @author make java
 * @since 2026-03-08
 */
@Data
@ApiModel("动态库查询参数")
public class DynamicLibraryQuery {

    @ApiModelProperty("设备类型")
    private String deviceTypeName;

    @ApiModelProperty("设备厂家")
    private String manufacturer;

    @ApiModelProperty("动态库名称")
    private String libraryName;

    @ApiModelProperty("当前页码")
    private Long current = 1L;

    @ApiModelProperty("每页大小")
    private Long size = 10L;
}
