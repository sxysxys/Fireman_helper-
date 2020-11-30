package com.rescue.hc.event;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/10/29
 * @descibe none
 * </pre>
 */
public class EventMessage<T> {
	private int code;
	private Object data;

	public EventMessage(Object data, int code) {
		this.data = data;
		this.code = code;
	}

	public EventMessage() {
	}


	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
