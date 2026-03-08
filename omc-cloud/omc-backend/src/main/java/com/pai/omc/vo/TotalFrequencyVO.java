package com.pai.omc.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 总使用频率视图对象
 * @author make java
 * @since 2026-03-08
 */
@Data
@ApiModel("总使用频率")
public class TotalFrequencyVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("设备类型")
    private String deviceType;

    @ApiModelProperty("设备子类")
    private String deviceSubtype;

    @ApiModelProperty("设备型号")
    private String deviceModel;

    @ApiModelProperty("动态库名称")
    private String libraryName;

    @ApiModelProperty("使用概率(%)")
    private BigDecimal frequency;
}
