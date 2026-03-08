package com.pai.omc.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 动态库文件实体
 * @author make java
 * @since 2026-03-08
 */
@Data
@TableName("omc_dynamic_library_file")
public class DynamicLibraryFile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String libraryName;
    private String version;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
