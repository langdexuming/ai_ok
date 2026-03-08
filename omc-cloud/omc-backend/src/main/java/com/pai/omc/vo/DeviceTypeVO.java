package com.pai.omc.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备类型视图对象
 * @author make java
 * @since 2026-03-08
 */
@Data
@ApiModel("设备类型")
public class DeviceTypeVO {

    @ApiModelProperty("类型ID")
    private String typeId;

    @ApiModelProperty("类型名称")
    private String typeName;
}
