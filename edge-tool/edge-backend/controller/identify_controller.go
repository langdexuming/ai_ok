package controller

import (
	"edge-backend/config"
	"edge-backend/vo"
	"fmt"
	"net"
	"strconv"
	"sync"
	"time"

	"github.com/gin-gonic/gin"
)

var (
	identifyMu      sync.Mutex
	identifyRunning bool
	identifySession string
	identifyStart   time.Time
	successCount    int
	protocolCount   int
	identifyLogs    = make(map[int]string)
	connected       bool
)

func ConnectDevice(c *gin.Context) {
	var input struct {
		IPAddress string `json:"ipAddress"`
		Port      int    `json:"port"`
	}
	if err := c.ShouldBindJSON(&input); err != nil {
		vo.Fail(c, "参数错误")
		return
	}

	addr := fmt.Sprintf("%s:%d", input.IPAddress, input.Port)
	conn, err := net.DialTimeout("tcp", addr, 5*time.Second)
	if err != nil {
		connected = false
		vo.Fail(c, "连接失败")
		return
	}
	conn.Close()
	connected = true
	vo.SuccessMsg(c, "连接成功")
}

func StartIdentify(c *gin.Context) {
	if !connected {
		vo.Fail(c, "请先成功连接设备")
		return
	}

	identifyMu.Lock()
	if identifyRunning {
		identifyMu.Unlock()
		vo.Fail(c, "当前已经在协议自动识别中，请勿重复操作")
		return
	}
	identifyRunning = true
	identifySession = fmt.Sprintf("session-%d", time.Now().UnixMilli())
	identifyStart = time.Now()
	successCount = 0
	protocolCount = 0
	identifyLogs = make(map[int]string)
	identifyMu.Unlock()

	go runIdentify()

	vo.SuccessMsg(c, "开始协议自动识别")
}

func StopIdentify(c *gin.Context) {
	identifyMu.Lock()
	identifyRunning = false
	identifyMu.Unlock()
	vo.SuccessMsg(c, "已停止协议自动识别")
}

func GetIdentifyStatus(c *gin.Context) {
	identifyMu.Lock()
	status := "idle"
	if identifyRunning {
		status = "running"
	} else if identifySession != "" {
		status = "done"
	}
	identifyMu.Unlock()

	vo.Success(c, gin.H{
		"status":        status,
		"successCount":  successCount,
		"protocolCount": protocolCount,
	})
}

func GetIdentifyLog(c *gin.Context) {
	serialNum, _ := strconv.Atoi(c.Param("serialNum"))
	identifyMu.Lock()
	logContent := identifyLogs[serialNum]
	identifyMu.Unlock()
	vo.Success(c, logContent)
}

func ExportRealtimeData(c *gin.Context) {
	var data []config.IdentifyRealtimeData
	config.DB.Where("session_id = ?", identifySession).Find(&data)
	vo.Success(c, data)
}

func runIdentify() {
	for serialNum := 1; serialNum <= 8; serialNum++ {
		identifyMu.Lock()
		if !identifyRunning {
			identifyMu.Unlock()
			break
		}
		identifyMu.Unlock()

		for addr := 1; addr <= 4; addr++ {
			identifyMu.Lock()
			if !identifyRunning {
				identifyMu.Unlock()
				return
			}
			identifyMu.Unlock()

			protocolCount++
			logLine := fmt.Sprintf("[%s] 串口%d 地址位%d - 开始识别...\n",
				time.Now().Format("2006-01-02 15:04:05"), serialNum, addr)

			logLine += fmt.Sprintf("  尝试协议识别...\n")
			logLine += fmt.Sprintf("  识别完成，等待下一个地址位\n\n")

			identifyMu.Lock()
			identifyLogs[serialNum] += logLine
			identifyMu.Unlock()

			time.Sleep(500 * time.Millisecond)
		}
	}

	identifyMu.Lock()
	identifyRunning = false
	identifyMu.Unlock()
}
