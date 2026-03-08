package com.pai.omc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 设备子类实体
 * @author make java
 * @since 2026-03-08
 */
@Data
@TableName("omc_device_subtype")
public class DeviceSubtype {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String subtypeId;
    private String subtypeName;
    private String typeId;
    private Integer protocolType;
}
