package com.pai.omc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 统一配置管理
 *
 * @author make java
 * @since 2026-03-08
 */
@Component
public class Configuration {

    /** 文件上传路径 */
    public static String UPLOAD_PATH;

    /** 设备配置数据同步间隔(天) */
    public static Integer SYNC_INTERVAL_DAYS;

    /** 设备搜索超时时间(毫秒) */
    public static Integer DEVICE_SEARCH_TIMEOUT;

    @Value("${omc.upload.path:/data/omc/upload}")
    public void setUploadPath(String uploadPath) {
        UPLOAD_PATH = uploadPath;
    }

    @Value("${omc.sync.interval-days:10}")
    public void setSyncIntervalDays(Integer syncIntervalDays) {
        SYNC_INTERVAL_DAYS = syncIntervalDays;
    }

    @Value("${omc.device.search-timeout:5000}")
    public void setDeviceSearchTimeout(Integer deviceSearchTimeout) {
        DEVICE_SEARCH_TIMEOUT = deviceSearchTimeout;
    }
}
