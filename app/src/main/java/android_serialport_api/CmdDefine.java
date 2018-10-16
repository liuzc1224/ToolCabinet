package android_serialport_api;

public enum CmdDefine {
//添加枚举的指定常量
	// ------------------------------请求---------------------------------------//
	/// <summary>
	/// 连接读写器的请求
	/// </summary>
	SEND_CONNECT(0x01),
	/// <summary>
	/// 保活的请求
	/// </summary>
	SEND_KEEPALIVE(0x03),
	/// <summary>
	/// 开始盘点的请求
	/// </summary>
	SEND_START_INVENTORY(0x07),
	/// <summary>
	/// 读标签的请求
	/// </summary>
	SEND_READ_TAG(0x09),
	/// <summary>
	/// 写标签的请求
	/// </summary>
	SEND_WRITE_TAG(0x0B),
	/// <summary>
	/// 停止盘点的请求
	/// </summary>
	SEND_STOP_INVENTORY(0x15),
	/// <summary>
	/// 设置天线配置的请求
	/// </summary>
	SEND_SET_ANTENNA_CONFIGURE(0x17),
	/// <summary>
	/// 获取天线配置的请求
	/// </summary>
	SEND_GET_ANTENNA_CONFIGURE(0x19),
	/// <summary>
	/// 设置GPO输出状态的请求
	/// </summary>
	SEND_SET_GPO_STATE(0x35),
	/// <summary>
	/// 获取GPI状态的请求
	/// </summary>
	SEND_GET_GPI_STATE(0x37),
	/// <summary>
	/// 设置盘点上报的请求
	/// </summary>
	SEND_SET_INVENTORY_REPORT(0x3E),
	/// <summary>
	/// 天线驻波比的请求
	/// </summary>
	SEND_GET_BOBBI_CONFIGURE(0x23),
	/// <summary>
	/// 时间同步的请求
	/// </summary>
	SEND_SET_TIME_SYNCHRO(0xA4),
	/// <summary>
	/// 设置蜂鸣器状态的请求
	/// </summary>
	SEND_SET_HUMMER(0xA0),

	// ------------------------------响应---------------------------------------//
	/// <summary>
	/// 连接读写器的响应
	/// </summary>
	RECEIVE_CONNECT(0x02),
	/// <summary>
	/// 保活的响应
	/// </summary>
	RECEIVE_KEEPALIVE(0x04),
	/// <summary>
	/// 盘点的响应
	/// </summary>
	RECEIVE_START_INVENTORY(0x08),
	/// <summary>
	/// 读标签的响应
	/// </summary>
	RECEIVE_READ_TAG(0x0A),
	/// <summary>
	/// 写标签的响应
	/// </summary>
	RECEIVE_WRITE_TAG(0x0C),
	/// <summary>
	/// 停止盘点的响应
	/// </summary>
	RECEIVE_STOP_INVENTORY(0x16),
	/// <summary>
	/// 设置天线配置的响应
	/// </summary>
	RECEIVE_SET_ANTENNA_CONFIGURE(0x18),
	/// <summary>
	/// 获取天线配置的响应
	/// </summary>
	RECEIVE_GET_ANTENNA_CONFIGURE(0x1A),
	/// <summary>
	/// 设置GPO输出状态的响应
	/// </summary>
	RECEIVE_SET_GPO_STATE(0x36),
	/// <summary>
	/// 获取GPI状态的响应
	/// </summary>
	RECEIVE_GET_GPI_STATE(0x38),
	/// <summary>
	/// 盘点上报数据
	/// </summary>
	RECEIVE_INVENTORY_REPORT(0x39),
	/// <summary>
	/// 操作命令数据上报
	/// </summary>
	RECEIVE_OPERATE_REPORT(0x3A),
	/// <summary>
	/// 设置盘点上报的响应
	/// </summary>
	RECEIVE_SET_INVENTORY_REPORT(0x3F),
	/// <summary>
	/// 不需要回复
	/// </summary>
	NO_RECEIVE(0xFF),
	/// <summary>
	/// 天线驻波比响应
	/// </summary>
	RECEIVE_GET_BOBBI_CONFIGURE(0x24),
	/// <summary>
	/// 时间同步的响应
	/// </summary>
	RECEIVE_SET_TIME_SYNCHRO(0xA5),
	/// <summary>
	/// 设置蜂鸣器状态的响应
	/// </summary>
	RECEIVE_SET_HUMMER(0xA1);

	private int isate = 0;

	private CmdDefine(int value) {
		isate = value;
	}

	public int GetValue() {
		return isate;
	}
}
