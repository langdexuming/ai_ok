package controller

import (
	"edge-backend/config"
	"edge-backend/model"
	"edge-backend/vo"
	"strconv"

	"github.com/gin-gonic/gin"
)

func PageLibrary(c *gin.Context) {
	current, _ := strconv.Atoi(c.DefaultQuery("current", "1"))
	size, _ := strconv.Atoi(c.DefaultQuery("size", "10"))
	deviceType := c.Query("deviceTypeName")
	manufacturer := c.Query("manufacturer")
	libraryName := c.Query("libraryName")

	var total int64
	var list []model.DynamicLibrary

	query := config.DB.Model(&model.DynamicLibrary{})
	if deviceType != "" {
		query = query.Where("dynamic_device_type LIKE ?", "%"+deviceType+"%")
	}
	if manufacturer != "" {
		query = query.Where("manufacturer LIKE ?", "%"+manufacturer+"%")
	}
	if libraryName != "" {
		query = query.Where("dynamic_library_name LIKE ?", "%"+libraryName+"%")
	}

	query.Count(&total)
	query.Offset((current - 1) * size).Limit(size).Find(&list)

	vo.SuccessPage(c, total, list, current, size)
}

func ImportLibraryList(c *gin.Context) {
	vo.SuccessMsg(c, "导入成功")
}

func ImportLibraryFiles(c *gin.Context) {
	vo.SuccessMsg(c, "导入成功")
}

func ExportLibrary(c *gin.Context) {
	var list []model.DynamicLibrary
	config.DB.Find(&list)
	vo.Success(c, list)
}

func DownloadLibraryFile(c *gin.Context) {
	vo.Fail(c, "功能开发中")
}

func ViewLibraryConfig(c *gin.Context) {
	vo.Success(c, "配置内容")
}

func PageTotalFrequency(c *gin.Context) {
	vo.SuccessPage(c, 0, []interface{}{}, 1, 10)
}

func ImportTotalFrequency(c *gin.Context) {
	vo.SuccessMsg(c, "导入成功")
}

func ExportTotalFrequency(c *gin.Context) {
	vo.Success(c, []interface{}{})
}

func PageSerialFrequency(c *gin.Context) {
	vo.SuccessPage(c, 0, []interface{}{}, 1, 10)
}

func ImportSerialFrequency(c *gin.Context) {
	vo.SuccessMsg(c, "导入成功")
}

func ExportSerialFrequency(c *gin.Context) {
	vo.Success(c, []interface{}{})
}
