package com.pai.omc.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备视图对象
 * @author make java
 * @since 2026-03-08
 */
@Data
@ApiModel("设备信息")
public class DeviceVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("IP地址")
    private String ipAddress;

    @ApiModelProperty("端口")
    private Integer port;

    @ApiModelProperty("设备编码")
    private String deviceCode;

    @ApiModelProperty("设备名称")
    private String deviceName;

    @ApiModelProperty("软件版本")
    private String softwareVersion;

    @ApiModelProperty("B接口版本")
    private String bInterfaceVersion;

    @ApiModelProperty("站点名称")
    private String siteName;
}
