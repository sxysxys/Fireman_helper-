package com.rescue.hc.enums;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/11/02
 * @descibe none
 * </pre>
 */
public enum ReccoCmdEnum {
	NONE_CMD(0, 0, "无"),
	CALL_CMD(1, 1, "呼叫"),
	RETREAT_CMD(2, 2, "撤退");



	private int code;
	private int cmd;
	private String msg;

	ReccoCmdEnum(int code, int cmd, String msg) {
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
