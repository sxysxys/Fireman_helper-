package com.rescue.hc.enums;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/11/02
 * @descibe none
 * </pre>
 */
public enum ReccoAlarmStateEnum {
	NORMAL_STATE(0, 0, "正常状态"),
	PRE_ALARM_STATE(1, 1, "预报警"),
	AUTOMATIC_STRONG_ALRAM_STATE(2, 2, "自动强报警"),
	MANUAL_STRONG_ALRAM_STATE(3, 3, "手动强报警"),
	HIGH_TEMPERATURE_ALARM_STATE(5, 5, "高温报警"),
	LOW_VOLTAGE_ALARM_STATE(4, 4, "低电压报警");



	private int code;
	private int cmd;
	private String msg;

	ReccoAlarmStateEnum(int code, int cmd, String msg) {
		this.code = code;
		this.cmd = cmd;
		this.msg = msg;
	}


	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
