package controller

import (
	"edge-backend/config"
	"edge-backend/vo"
	"time"

	"github.com/gin-gonic/gin"
)

func GetOmcConfig(c *gin.Context) {
	var cfg config.OmcConfig
	result := config.DB.First(&cfg)
	if result.Error != nil {
		vo.Success(c, nil)
		return
	}
	vo.Success(c, cfg)
}

func SaveOmcConfig(c *gin.Context) {
	var input config.OmcConfig
	if err := c.ShouldBindJSON(&input); err != nil {
		vo.Fail(c, "参数错误")
		return
	}

	var existing config.OmcConfig
	result := config.DB.First(&existing)
	now := time.Now().Format("2006-01-02 15:04:05")

	if result.Error != nil {
		input.CreateTime = now
		input.UpdateTime = now
		config.DB.Create(&input)
	} else {
		existing.IPAddress = input.IPAddress
		existing.Port = input.Port
		existing.Username = input.Username
		existing.Password = input.Password
		existing.UpdateTime = now
		config.DB.Save(&existing)
	}

	vo.SuccessMsg(c, "配置成功")
}

func SyncFromOmc(c *gin.Context) {
	vo.SuccessMsg(c, "同步完成")
}
