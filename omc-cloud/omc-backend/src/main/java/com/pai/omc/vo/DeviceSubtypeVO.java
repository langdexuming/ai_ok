package com.pai.omc.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备子类视图对象
 * @author make java
 * @since 2026-03-08
 */
@Data
@ApiModel("设备子类")
public class DeviceSubtypeVO {

    @ApiModelProperty("子类ID")
    private String subtypeId;

    @ApiModelProperty("子类名称")
    private String subtypeName;

    @ApiModelProperty("关联设备类型ID")
    private String typeId;
}
