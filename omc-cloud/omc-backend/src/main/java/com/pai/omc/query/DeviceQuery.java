package com.pai.omc.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备查询参数
 *
 * @author make java
 * @since 2026-03-08
 */
@Data
@ApiModel("设备查询参数")
public class DeviceQuery {

    @ApiModelProperty("IP地址")
    private String ipAddress;

    @ApiModelProperty("设备编码")
    private String deviceCode;

    @ApiModelProperty("站点名称")
    private String siteName;

    @ApiModelProperty("当前页码")
    private Long current = 1L;

    @ApiModelProperty("每页大小")
    private Long size = 10L;
}
