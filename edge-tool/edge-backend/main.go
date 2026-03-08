package main

import (
	"edge-backend/config"
	"edge-backend/router"
	"log"
)

func main() {
	config.InitConfig()
	config.InitDB()

	r := router.SetupRouter()
	log.Printf("边端批量配置工具后端启动，端口: %s", config.AppConfig.Port)
	if err := r.Run(":" + config.AppConfig.Port); err != nil {
		log.Fatalf("服务启动失败: %v", err)
	}
}
