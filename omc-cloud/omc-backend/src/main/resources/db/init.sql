CREATE DATABASE IF NOT EXISTS pai DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pai;

CREATE TABLE IF NOT EXISTS omc_dynamic_library (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_type_name VARCHAR(100) COMMENT '设备类型名称',
    device_type_id VARCHAR(50) COMMENT '设备类型ID',
    manufacturer VARCHAR(100) COMMENT '设备厂家',
    manufacturer_id VARCHAR(50) COMMENT '设备厂家ID',
    device_model VARCHAR(100) COMMENT '设备型号',
    device_model_id VARCHAR(50) UNIQUE COMMENT '设备型号ID',
    library_name VARCHAR(200) COMMENT '动态库名称',
    library_id VARCHAR(50) COMMENT '配置文件ID',
    version VARCHAR(50) COMMENT '版本号',
    comm_address INT COMMENT '通信地址',
    baud_rate INT COMMENT '波特率',
    check_bit VARCHAR(20) COMMENT '校验位',
    data_bit INT COMMENT '数据位',
    stop_bit VARCHAR(10) COMMENT '停止位',
    comm_mode VARCHAR(20) COMMENT '通信方式',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    comm_port INT COMMENT '通信端口',
    is_extended_frame TINYINT DEFAULT 0 COMMENT '是否扩展帧',
    is_can_fd TINYINT DEFAULT 0 COMMENT '是否CAN-FD',
    arbitrate_baud_rate VARCHAR(50) COMMENT '仲裁阶段波特率',
    data_baud_rate VARCHAR(50) COMMENT '数据阶段波特率',
    bit_rate_flag TINYINT DEFAULT 0 COMMENT '比特率切换标志',
    has_file TINYINT DEFAULT 0 COMMENT '是否有动态库文件',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '动态库信息表';

CREATE TABLE IF NOT EXISTS omc_dynamic_library_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    library_name VARCHAR(200) COMMENT '动态库名称',
    version VARCHAR(50) COMMENT '版本号',
    file_name VARCHAR(200) COMMENT '原始文件名',
    file_path VARCHAR(500) COMMENT '存储路径',
    file_type VARCHAR(20) COMMENT '文件类型',
    file_size BIGINT COMMENT '文件大小',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '动态库文件表';

CREATE TABLE IF NOT EXISTS omc_total_frequency (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_type VARCHAR(100) COMMENT '设备类型',
    device_subtype VARCHAR(100) COMMENT '设备子类',
    device_model VARCHAR(100) COMMENT '设备型号',
    library_name VARCHAR(200) COMMENT '动态库名称',
    frequency DECIMAL(10,4) COMMENT '使用概率',
    count INT COMMENT '使用数量',
    total_count INT COMMENT '该类型总数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '总使用频率表';

CREATE TABLE IF NOT EXISTS omc_serial_frequency (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    serial_num INT COMMENT '串口号',
    device_address INT COMMENT '地址位',
    device_type VARCHAR(100) COMMENT '设备类型',
    device_subtype VARCHAR(100) COMMENT '设备子类',
    device_model VARCHAR(100) COMMENT '设备型号',
    library_name VARCHAR(200) COMMENT '动态库名称',
    frequency DECIMAL(10,4) COMMENT '使用概率',
    count INT COMMENT '使用数量',
    total_count INT COMMENT '该维度总数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '串口使用频率表';

CREATE TABLE IF NOT EXISTS omc_verify_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_type_id VARCHAR(50) COMMENT '设备类型ID',
    device_type_name VARCHAR(100) COMMENT '设备类型名称',
    device_subtype_id VARCHAR(50) COMMENT '设备子类ID',
    device_subtype_name VARCHAR(100) COMMENT '设备子类名称',
    channel_name VARCHAR(100) COMMENT '信号名称',
    channel_code VARCHAR(100) COMMENT '信号ID',
    upper_limit DECIMAL(15,4) COMMENT '上限',
    lower_limit DECIMAL(15,4) COMMENT '下限',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_subtype_channel (device_subtype_id, channel_code)
) COMMENT '校核配置表';

CREATE TABLE IF NOT EXISTS omc_device (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ip_address VARCHAR(50) COMMENT 'IP地址',
    port INT COMMENT '端口',
    device_code VARCHAR(100) COMMENT '设备编码',
    device_name VARCHAR(200) COMMENT '设备名称',
    software_version VARCHAR(50) COMMENT '软件版本',
    b_interface_version VARCHAR(50) COMMENT 'B接口版本',
    site_name VARCHAR(200) COMMENT '站点名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_ip_port (ip_address, port)
) COMMENT '设备列表表';

CREATE TABLE IF NOT EXISTS omc_device_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_id BIGINT COMMENT '关联设备ID',
    device_code VARCHAR(100) COMMENT '边缘网关设备编码',
    sub_device_code VARCHAR(100) COMMENT '下挂设备编码',
    device_type VARCHAR(100) COMMENT '设备类型',
    device_subtype VARCHAR(100) COMMENT '设备子类',
    device_model VARCHAR(100) COMMENT '设备型号',
    library_name VARCHAR(200) COMMENT '动态库名称',
    serial_num INT COMMENT '串口号',
    device_address INT COMMENT '地址位',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '设备配置数据表';

CREATE TABLE IF NOT EXISTS omc_device_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type_id VARCHAR(50) UNIQUE COMMENT '类型ID',
    type_name VARCHAR(100) COMMENT '类型名称',
    protocol_type INT COMMENT '协议类型'
) COMMENT '设备类型表';

CREATE TABLE IF NOT EXISTS omc_device_subtype (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    subtype_id VARCHAR(50) UNIQUE COMMENT '子类ID',
    subtype_name VARCHAR(100) COMMENT '子类名称',
    type_id VARCHAR(50) COMMENT '关联设备类型ID',
    protocol_type INT COMMENT '协议类型'
) COMMENT '设备子类表';
