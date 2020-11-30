package com.rescue.hc.bean.util;

import lombok.Data;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/11/06
 * @descibe none
 * </pre>
 */
@Data
public class ToastInfo {
	private int type;
	private String msg;
	private boolean withIcon;

	public ToastInfo(int type, String msg, boolean withIcon) {
		this.type = type;
		this.msg = msg;
		this.withIcon = withIcon;
	}
}
