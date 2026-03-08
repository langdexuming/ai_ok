package com.pai.omc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 设备类型实体
 * @author make java
 * @since 2026-03-08
 */
@Data
@TableName("omc_device_type")
public class DeviceType {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String typeId;
    private String typeName;
    private Integer protocolType;
}
