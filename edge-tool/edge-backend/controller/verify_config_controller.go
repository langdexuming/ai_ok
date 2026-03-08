package controller

import (
	"edge-backend/config"
	"edge-backend/model"
	"edge-backend/vo"
	"strconv"

	"github.com/gin-gonic/gin"
)

func PageVerifyConfig(c *gin.Context) {
	current, _ := strconv.Atoi(c.DefaultQuery("current", "1"))
	size, _ := strconv.Atoi(c.DefaultQuery("size", "10"))
	deviceType := c.Query("deviceTypeName")
	channelName := c.Query("channelName")

	var total int64
	var list []model.DllIdentifyRange

	query := config.DB.Model(&model.DllIdentifyRange{})
	if deviceType != "" {
		query = query.Where("device_type_id LIKE ?", "%"+deviceType+"%")
	}
	if channelName != "" {
		query = query.Where("channel_name LIKE ?", "%"+channelName+"%")
	}

	query.Count(&total)
	query.Offset((current - 1) * size).Limit(size).Find(&list)

	vo.SuccessPage(c, total, list, current, size)
}

func AddVerifyConfig(c *gin.Context) {
	var input model.DllIdentifyRange
	if err := c.ShouldBindJSON(&input); err != nil {
		vo.Fail(c, "参数错误")
		return
	}
	config.DB.Create(&input)
	vo.SuccessMsg(c, "新增成功")
}

func UpdateVerifyConfig(c *gin.Context) {
	var input model.DllIdentifyRange
	if err := c.ShouldBindJSON(&input); err != nil {
		vo.Fail(c, "参数错误")
		return
	}
	config.DB.Where("device_type_id = ? AND channel_code = ?", input.DeviceTypeID, input.ChannelCode).
		Updates(&input)
	vo.SuccessMsg(c, "编辑成功")
}

func DeleteVerifyConfig(c *gin.Context) {
	id := c.Query("id")
	config.DB.Where("device_type_id = ? AND channel_code = ?", id, c.Query("channelCode")).
		Delete(&model.DllIdentifyRange{})
	vo.SuccessMsg(c, "删除成功")
}

func BatchDeleteVerifyConfig(c *gin.Context) {
	var ids []string
	if err := c.ShouldBindJSON(&ids); err != nil {
		vo.Fail(c, "参数错误")
		return
	}
	vo.SuccessMsg(c, "删除成功")
}

func ImportVerifyConfig(c *gin.Context) {
	vo.SuccessMsg(c, "导入成功")
}

func ExportVerifyConfig(c *gin.Context) {
	var list []model.DllIdentifyRange
	config.DB.Find(&list)
	vo.Success(c, list)
}

func ListDeviceTypes(c *gin.Context) {
	var list []model.DeviceType
	config.DB.Find(&list)
	vo.Success(c, list)
}

func ListDeviceSubtypes(c *gin.Context) {
	typeId := c.Query("typeId")
	var list []model.DeviceSubtype
	query := config.DB
	if typeId != "" {
		query = query.Where("device_type_id = ?", typeId)
	}
	query.Find(&list)
	vo.Success(c, list)
}
