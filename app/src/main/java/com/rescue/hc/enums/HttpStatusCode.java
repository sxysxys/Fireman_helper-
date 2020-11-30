package com.rescue.hc.enums;

/**
 * @author DevCheng
 * @description
 * @date 2018/07/14
 */

public enum HttpStatusCode {

	SERVICE_ERROR(500, "服务器发生错误"),
	ADDRESS_NOT_EXIST(404, "请求地址不存在"),
	SERVICE_REFUSE(403, "请求被服务器拒绝"),
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

	HttpStatusCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
