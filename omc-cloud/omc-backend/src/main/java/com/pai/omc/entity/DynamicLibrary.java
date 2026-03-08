package com.pai.omc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 动态库信息实体
 * @author make java
 * @since 2026-03-08
 */
@Data
@TableName("omc_dynamic_library")
public class DynamicLibrary {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String deviceTypeName;
    private String deviceTypeId;
    private String manufacturer;
    private String manufacturerId;
    private String deviceModel;
    private String deviceModelId;
    private String libraryName;
    private String libraryId;
    private String version;
    private Integer commAddress;
    private Integer baudRate;
    private String checkBit;
    private Integer dataBit;
    private String stopBit;
    private String commMode;
    private String ipAddress;
    private Integer commPort;
    private Integer isExtendedFrame;
    private Integer isCanFd;
    private String arbitrateBaudRate;
    private String dataBaudRate;
    private Integer bitRateFlag;
    private Integer hasFile;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
