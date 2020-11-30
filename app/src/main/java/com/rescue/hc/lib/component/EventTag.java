package com.rescue.hc.lib.component;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/10/29
 * @descibe none
 * </pre>
 */
public class EventTag {
	public static final int NFC_INFO = 1001;
	public static final int GET_SP_DATA = 1002;
	public static final int TOAST_INFO = 1003;
	public static final int SET_NFC_SUBMIT = 1004;
	public static final int SET_NFC_CANCEL = 1005;
	public static final int SET_NFC_STATUS = 1006;
	public static final int TOAST_INFO_STATUS = 1007;
	public static final int HOME_GET_SP = 1008;
	public static final int UPDATE_HOME_ALL_DATA = 1009;

	public static  final  int GET_DATA_TEST=1031;

	/**
	 * 写入NFC数据 更新
	 */
	public static final int NFC_UPDATE_FIREMAN = 1010;
	public static final int LOCATION_ID = 1011;
	public static final int UPDATE_HOME_SINGLE_DATA = 1012;
	public static final int WEB_COMMAND = 1013;
	public static final int JOIN_FIREMAN = 1014;
	public static final int LEAVE_FIREMAN = 1015;
	public static final String NOTIFICATION = "1016";
	public static final int LOGIN_FINISH = 1016;

	/**
	 * 初始化串口
	 */
	public static final int INIT_USB_PORT=1017;


	/**
	 * 传递在线人员到分组界面
	 */
	public static final int BUS_ON_LINE_RECCO=2001;

	/**
	 * 更新分组信息到主界面
	 */
	public static final int BUS_UPDATE_GROUP_INFO=2002;

	public static final int BUS_SEND_CMD_DATA=2003;
}
