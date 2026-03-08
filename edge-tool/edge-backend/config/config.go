package config

import (
	"log"
	"os"

	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
)

var DB *gorm.DB

var AppConfig = struct {
	Port       string
	DBPath     string
	SyncCron   string
	UploadPath string
}{
	Port:       "9090",
	DBPath:     "./i-edge.db",
	SyncCron:   "0 2 * * *",
	UploadPath: "./upload",
}

func InitConfig() {
	if port := os.Getenv("EDGE_PORT"); port != "" {
		AppConfig.Port = port
	}
	if dbPath := os.Getenv("EDGE_DB_PATH"); dbPath != "" {
		AppConfig.DBPath = dbPath
	}
}

func InitDB() {
	var err error
	DB, err = gorm.Open(sqlite.Open(AppConfig.DBPath), &gorm.Config{})
	if err != nil {
		log.Fatalf("数据库连接失败: %v", err)
	}

	DB.AutoMigrate(&OmcConfig{}, &IdentifyResult{}, &IdentifyRealtimeData{})
	log.Println("数据库初始化完成")
}

type OmcConfig struct {
	ID           uint   `gorm:"primaryKey;autoIncrement" json:"id"`
	IPAddress    string `json:"ipAddress"`
	Port         string `json:"port"`
	Username     string `json:"username"`
	Password     string `json:"password"`
	LastSyncTime string `json:"lastSyncTime"`
	CreateTime   string `json:"createTime"`
	UpdateTime   string `json:"updateTime"`
}

func (OmcConfig) TableName() string { return "tab_omc_config" }

type IdentifyResult struct {
	ID            uint   `gorm:"primaryKey;autoIncrement" json:"id"`
	SessionID     string `json:"sessionId"`
	SerialNum     int    `json:"serialNum"`
	DeviceAddress int    `json:"deviceAddress"`
	DeviceType    string `json:"deviceType"`
	DeviceSubtype string `json:"deviceSubtype"`
	LibraryName   string `json:"libraryName"`
	Status        int    `json:"status"`
	FailReason    string `json:"failReason"`
	IdentifyTime  string `json:"identifyTime"`
	CreateTime    string `json:"createTime"`
}

func (IdentifyResult) TableName() string { return "tab_identify_result" }

type IdentifyRealtimeData struct {
	ID            uint   `gorm:"primaryKey;autoIncrement" json:"id"`
	SessionID     string `json:"sessionId"`
	DeviceType    string `json:"deviceType"`
	DeviceSubtype string `json:"deviceSubtype"`
	DeviceCode    string `json:"deviceCode"`
	ChannelName   string `json:"channelName"`
	ChannelCode   string `json:"channelCode"`
	ChannelType   int    `json:"channelType"`
	CollectValue  string `json:"collectValue"`
	CollectTime   string `json:"collectTime"`
}

func (IdentifyRealtimeData) TableName() string { return "tab_identify_realtime_data" }
