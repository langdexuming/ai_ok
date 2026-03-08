package com.pai.omc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 设备列表实体
 * @author make java
 * @since 2026-03-08
 */
@Data
@TableName("omc_device")
public class Device {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String ipAddress;
    private Integer port;
    private String deviceCode;
    private String deviceName;
    private String softwareVersion;
    private String bInterfaceVersion;
    private String siteName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
