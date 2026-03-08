package com.pai.omc.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 动态库视图对象
 * @author make java
 * @since 2026-03-08
 */
@Data
@ApiModel("动态库信息")
public class DynamicLibraryVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("设备类型名称")
    private String deviceTypeName;

    @ApiModelProperty("设备厂家")
    private String manufacturer;

    @ApiModelProperty("设备型号")
    private String deviceModel;

    @ApiModelProperty("动态库名称")
    private String libraryName;

    @ApiModelProperty("版本号")
    private String version;

    @ApiModelProperty("是否有动态库文件")
    private Integer hasFile;
}
