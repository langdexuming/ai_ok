package com.pai.omc.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 总使用频率查询参数
 *
 * @author make java
 * @since 2026-03-08
 */
@Data
@ApiModel("总使用频率查询参数")
public class TotalFrequencyQuery {

    @ApiModelProperty("设备类型")
    private String deviceType;

    @ApiModelProperty("设备子类")
    private String deviceSubtype;

    @ApiModelProperty("动态库名称")
    private String libraryName;

    @ApiModelProperty("当前页码")
    private Long current = 1L;

    @ApiModelProperty("每页大小")
    private Long size = 10L;
}
