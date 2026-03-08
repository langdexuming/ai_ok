package com.pai.omc.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 导入结果
 *
 * @author make java
 * @since 2026-03-08
 */
@Data
@ApiModel("导入结果")
public class ImportResultDTO {

    @ApiModelProperty("是否成功")
    private Boolean success;

    @ApiModelProperty("成功数量")
    private Integer successCount;

    @ApiModelProperty("失败数量")
    private Integer failCount;

    @ApiModelProperty("失败原因")
    private String failMessage;

    public static ImportResultDTO success(Integer count) {
        ImportResultDTO dto = new ImportResultDTO();
        dto.setSuccess(true);
        dto.setSuccessCount(count);
        dto.setFailCount(0);
        return dto;
    }

    public static ImportResultDTO fail(String message) {
        ImportResultDTO dto = new ImportResultDTO();
        dto.setSuccess(false);
        dto.setSuccessCount(0);
        dto.setFailMessage(message);
        return dto;
    }
}
