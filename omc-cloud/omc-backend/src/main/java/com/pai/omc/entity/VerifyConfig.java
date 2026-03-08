package com.pai.omc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 校核配置实体
 * @author make java
 * @since 2026-03-08
 */
@Data
@TableName("omc_verify_config")
public class VerifyConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String deviceTypeId;
    private String deviceTypeName;
    private String deviceSubtypeId;
    private String deviceSubtypeName;
    private String channelName;
    private String channelCode;
    private BigDecimal upperLimit;
    private BigDecimal lowerLimit;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
