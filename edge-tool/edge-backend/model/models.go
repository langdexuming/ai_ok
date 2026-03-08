package model

type DynamicLibrary struct {
	DynamicLibraryID    int    `gorm:"column:dynamic_library_id;primaryKey" json:"id"`
	DynamicLibraryName string `gorm:"column:dynamic_library_name" json:"libraryName"`
	DynamicDeviceType  string `gorm:"column:dynamic_device_type" json:"deviceTypeName"`
	DynamicLibraryVersion string `gorm:"column:dynamic_library_version" json:"version"`
	DeviceTypeID       int    `gorm:"column:device_type_id" json:"deviceTypeId"`
	Manufacturer       string `gorm:"column:manufacturer" json:"manufacturer"`
	DeviceAddress      int    `gorm:"column:device_address" json:"deviceAddress"`
	Baud               int    `gorm:"column:baud" json:"baudRate"`
	DataBit            int    `gorm:"column:data_bit" json:"dataBit"`
	CheckBit           string `gorm:"column:check_bit" json:"checkBit"`
	StopBit            string `gorm:"column:stop_bit" json:"stopBit"`
	IsExist            int    `gorm:"column:is_exist" json:"isExist"`
	ProtocolType       int    `gorm:"column:protocol_type" json:"protocolType"`
}

func (DynamicLibrary) TableName() string { return "tab_dynamic_library" }

type DllIdentifyRange struct {
	DeviceTypeID  string `gorm:"column:device_type_id" json:"deviceTypeId"`
	ChannelCode  string `gorm:"column:channel_code" json:"channelCode"`
	ChannelName  string `gorm:"column:channel_name" json:"channelName"`
	UpperLimit   int    `gorm:"column:upper_limit" json:"upperLimit"`
	LowerLimit   int    `gorm:"column:lower_limit" json:"lowerLimit"`
	Enum         string `gorm:"column:enum" json:"enum"`
	SiteType     int    `gorm:"column:site_type" json:"siteType"`
	ProtocolType int    `gorm:"column:protocol_type" json:"protocolType"`
	ChannelType  int    `gorm:"column:channel_type" json:"channelType"`
}

func (DllIdentifyRange) TableName() string { return "tab_dll_identify_range" }

type DeviceType struct {
	DeviceTypeID   string `gorm:"column:device_type_id;primaryKey" json:"typeId"`
	DeviceTypeCode string `gorm:"column:device_type_code" json:"typeCode"`
	DeviceTypeName string `gorm:"column:device_type_name" json:"typeName"`
	ProtocolType   int    `gorm:"column:protocol_type" json:"protocolType"`
}

func (DeviceType) TableName() string { return "tab_device_type" }

type DeviceSubtype struct {
	DeviceSubtypeID   string `gorm:"column:device_subtype_id;primaryKey" json:"subtypeId"`
	DeviceSubtypeCode string `gorm:"column:device_subtype_code" json:"subtypeCode"`
	DeviceSubtypeName string `gorm:"column:device_subtype_name" json:"subtypeName"`
	DeviceTypeID      string `gorm:"column:device_type_id" json:"typeId"`
	ProtocolType      int    `gorm:"column:protocol_type" json:"protocolType"`
}

func (DeviceSubtype) TableName() string { return "tab_device_subtype" }

type Device struct {
	DeviceID         string `gorm:"column:device_id;primaryKey" json:"deviceId"`
	DeviceCode       string `gorm:"column:device_code" json:"deviceCode"`
	DeviceName       string `gorm:"column:device_name" json:"deviceName"`
	SerialNum        int    `gorm:"column:serial_num" json:"serialNum"`
	VirtualAddress   int    `gorm:"column:virtual_address" json:"virtualAddress"`
	Manufacturer     string `gorm:"column:manufacturer" json:"manufacturer"`
	DynamicLibraryID int    `gorm:"column:dynamic_library_id" json:"dynamicLibraryId"`
	DeviceAddress    int    `gorm:"column:device_address" json:"deviceAddress"`
	DeviceSubtypeID  string `gorm:"column:device_subtype_id" json:"deviceSubtypeId"`
	Enable           int    `gorm:"column:enable" json:"enable"`
}

func (Device) TableName() string { return "tab_device" }

type IEdgeInfo struct {
	InfoID     int    `gorm:"column:info_id;primaryKey" json:"infoId"`
	FsuCode    string `gorm:"column:fsu_code" json:"fsuCode"`
	FsuName    string `gorm:"column:fsu_name" json:"fsuName"`
	SiteType   int    `gorm:"column:site_type" json:"siteType"`
	SiteName   string `gorm:"column:site_name" json:"siteName"`
	SiteCode   string `gorm:"column:site_code" json:"siteCode"`
	DeviceCode string `gorm:"column:device_code" json:"deviceCode"`
}

func (IEdgeInfo) TableName() string { return "tab_i_edge_info" }
