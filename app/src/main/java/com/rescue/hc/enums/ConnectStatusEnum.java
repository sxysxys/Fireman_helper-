package com.rescue.hc.enums;

/**
 * @author DevCheng
 * @description 消防员与手表绑定状态枚举类
 * @date 2018/07/12
 */

public enum ConnectStatusEnum {

	FIREMAN_CONNECTED(0, "未连接"),
	FIREMAN_DISCONNECTED(1, "已连接未绑定"),
	FIREMAN_BIND(2,"已连接已绑定")
	;

	private int code;

	private String msg;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	ConnectStatusEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
