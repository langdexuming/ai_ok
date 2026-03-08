package com.pai.omc.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 网段搜索参数
 *
 * @author make java
 * @since 2026-03-08
 */
@Data
@ApiModel("网段搜索参数")
public class SubnetSearchDTO {

    @NotEmpty(message = "至少设置一个网段")
    @ApiModelProperty(value = "IP段列表", required = true)
    private List<String> ipRanges;
}
