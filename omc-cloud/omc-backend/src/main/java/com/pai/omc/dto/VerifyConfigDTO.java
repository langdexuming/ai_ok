package com.pai.omc.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 校核配置新增/编辑参数
 *
 * @author make java
 * @since 2026-03-08
 */
@Data
@ApiModel("校核配置操作参数")
public class VerifyConfigDTO {

    @ApiModelProperty("主键ID(编辑时传)")
    private Long id;

    @NotBlank(message = "设备类型ID不能为空")
    @ApiModelProperty(value = "设备类型ID", required = true)
    private String deviceTypeId;

    @NotBlank(message = "设备类型名称不能为空")
    @ApiModelProperty(value = "设备类型名称", required = true)
    private String deviceTypeName;

    @NotBlank(message = "设备子类ID不能为空")
    @ApiModelProperty(value = "设备子类ID", required = true)
    private String deviceSubtypeId;

    @NotBlank(message = "设备子类名称不能为空")
    @ApiModelProperty(value = "设备子类名称", required = true)
    private String deviceSubtypeName;

    @NotBlank(message = "信号名称不能为空")
    @Size(max = 50, message = "信号名称不能超过50个字符")
    @ApiModelProperty(value = "信号名称", required = true)
    private String channelName;

    @NotBlank(message = "信号ID不能为空")
    @Size(max = 50, message = "信号ID不能超过50个字符")
    @ApiModelProperty(value = "信号ID", required = true)
    private String channelCode;

    @NotNull(message = "上限不能为空")
    @DecimalMin(value = "-999999999", message = "上限不能小于-999999999")
    @DecimalMax(value = "999999999", message = "上限不能大于999999999")
    @ApiModelProperty(value = "上限", required = true)
    private BigDecimal upperLimit;

    @NotNull(message = "下限不能为空")
    @DecimalMin(value = "-999999999", message = "下限不能小于-999999999")
    @DecimalMax(value = "999999999", message = "下限不能大于999999999")
    @ApiModelProperty(value = "下限", required = true)
    private BigDecimal lowerLimit;
}
