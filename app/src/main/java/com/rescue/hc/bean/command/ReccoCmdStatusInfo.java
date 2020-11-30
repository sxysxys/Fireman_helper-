package com.rescue.hc.bean.command;

import lombok.Data;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/11/02
 * @descibe none
 * </pre>
 */
@Data
public class ReccoCmdStatusInfo {
	/**
	 * 0无  1 呼叫 2 撤退
	 */
	private int cmd;
	public ReccoCmdStatusInfo(int cmd) {
		this.cmd = cmd;
	}
}
