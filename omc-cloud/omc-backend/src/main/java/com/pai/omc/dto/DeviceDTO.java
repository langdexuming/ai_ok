package com.pai.omc.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 设备新增/编辑参数
 *
 * @author make java
 * @since 2026-03-08
 */
@Data
@ApiModel("设备操作参数")
public class DeviceDTO {

    @ApiModelProperty("主键ID(编辑时传)")
    private Long id;

    @NotBlank(message = "IP地址不能为空")
    @Pattern(regexp = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$",
            message = "IP地址格式不正确")
    @ApiModelProperty(value = "IP地址", required = true)
    private String ipAddress;

    @NotNull(message = "端口不能为空")
    @Min(value = 0, message = "端口不能小于0")
    @Max(value = 65535, message = "端口不能大于65535")
    @ApiModelProperty(value = "端口", required = true)
    private Integer port;
}
