package com.pai.omc.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 校核配置视图对象
 * @author make java
 * @since 2026-03-08
 */
@Data
@ApiModel("校核配置")
public class VerifyConfigVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("设备类型名称")
    private String deviceTypeName;

    @ApiModelProperty("设备子类名称")
    private String deviceSubtypeName;

    @ApiModelProperty("信号名称")
    private String channelName;

    @ApiModelProperty("信号ID")
    private String channelCode;

    @ApiModelProperty("上限")
    private BigDecimal upperLimit;

    @ApiModelProperty("下限")
    private BigDecimal lowerLimit;
}
