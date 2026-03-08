package com.pai.omc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 串口使用频率实体
 * @author make java
 * @since 2026-03-08
 */
@Data
@TableName("omc_serial_frequency")
public class SerialFrequency {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer serialNum;
    private Integer deviceAddress;
    private String deviceType;
    private String deviceSubtype;
    private String deviceModel;
    private String libraryName;
    private BigDecimal frequency;
    private Integer count;
    private Integer totalCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
