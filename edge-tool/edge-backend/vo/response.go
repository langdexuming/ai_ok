package vo

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

type Response struct {
	Code    int         `json:"code"`
	Message string      `json:"message"`
	Data    interface{} `json:"data"`
}

type PageResult struct {
	Total   int64       `json:"total"`
	Records interface{} `json:"records"`
	Current int         `json:"current"`
	Size    int         `json:"size"`
}

func Success(c *gin.Context, data interface{}) {
	c.JSON(http.StatusOK, Response{Code: 200, Message: "操作成功", Data: data})
}

func SuccessMsg(c *gin.Context, msg string) {
	c.JSON(http.StatusOK, Response{Code: 200, Message: msg, Data: nil})
}

func Fail(c *gin.Context, msg string) {
	c.JSON(http.StatusOK, Response{Code: 500, Message: msg, Data: nil})
}

func SuccessPage(c *gin.Context, total int64, records interface{}, current, size int) {
	c.JSON(http.StatusOK, Response{Code: 200, Message: "操作成功", Data: PageResult{Total: total, Records: records, Current: current, Size: size}})
}
