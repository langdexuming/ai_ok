package com.pai.omc.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 校核配置查询参数
 *
 * @author make java
 * @since 2026-03-08
 */
@Data
@ApiModel("校核配置查询参数")
public class VerifyConfigQuery {

    @ApiModelProperty("设备类型")
    private String deviceTypeName;

    @ApiModelProperty("设备子类")
    private String deviceSubtypeName;

    @ApiModelProperty("信号名称")
    private String channelName;

    @ApiModelProperty("当前页码")
    private Long current = 1L;

    @ApiModelProperty("每页大小")
    private Long size = 10L;
}
