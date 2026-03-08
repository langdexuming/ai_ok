package com.pai.omc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 设备配置数据实体
 * @author make java
 * @since 2026-03-08
 */
@Data
@TableName("omc_device_config")
public class DeviceConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long deviceId;
    private String deviceCode;
    private String subDeviceCode;
    private String deviceType;
    private String deviceSubtype;
    private String deviceModel;
    private String libraryName;
    private Integer serialNum;
    private Integer deviceAddress;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
