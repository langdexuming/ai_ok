package router

import (
	"edge-backend/controller"
	"edge-backend/middleware"

	"github.com/gin-gonic/gin"
)

func SetupRouter() *gin.Engine {
	r := gin.Default()
	r.Use(middleware.Cors())

	api := r.Group("/api")
	{
		api.GET("/omc-config", controller.GetOmcConfig)
		api.POST("/omc-config/save", controller.SaveOmcConfig)
		api.POST("/omc-config/sync", controller.SyncFromOmc)

		api.POST("/identify/connect", controller.ConnectDevice)
		api.POST("/identify/start", controller.StartIdentify)
		api.POST("/identify/stop", controller.StopIdentify)
		api.GET("/identify/status", controller.GetIdentifyStatus)
		api.GET("/identify/log/:serialNum", controller.GetIdentifyLog)
		api.GET("/identify/export", controller.ExportRealtimeData)

		api.GET("/library/page", controller.PageLibrary)
		api.POST("/library/import-list", controller.ImportLibraryList)
		api.POST("/library/import-file", controller.ImportLibraryFiles)
		api.GET("/library/export", controller.ExportLibrary)
		api.GET("/library/download/:id", controller.DownloadLibraryFile)
		api.GET("/library/view/:id", controller.ViewLibraryConfig)
		api.GET("/frequency/total/page", controller.PageTotalFrequency)
		api.POST("/frequency/total/import", controller.ImportTotalFrequency)
		api.GET("/frequency/total/export", controller.ExportTotalFrequency)
		api.GET("/frequency/serial/page", controller.PageSerialFrequency)
		api.POST("/frequency/serial/import", controller.ImportSerialFrequency)
		api.GET("/frequency/serial/export", controller.ExportSerialFrequency)

		api.GET("/verify-config/page", controller.PageVerifyConfig)
		api.POST("/verify-config/add", controller.AddVerifyConfig)
		api.PUT("/verify-config/update", controller.UpdateVerifyConfig)
		api.DELETE("/verify-config/delete", controller.DeleteVerifyConfig)
		api.DELETE("/verify-config/batch-delete", controller.BatchDeleteVerifyConfig)
		api.POST("/verify-config/import", controller.ImportVerifyConfig)
		api.GET("/verify-config/export", controller.ExportVerifyConfig)

		api.GET("/device-type/list", controller.ListDeviceTypes)
		api.GET("/device-subtype/list", controller.ListDeviceSubtypes)
	}

	return r
}
