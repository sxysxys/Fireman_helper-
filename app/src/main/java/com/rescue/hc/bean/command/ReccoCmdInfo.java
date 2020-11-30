package com.rescue.hc.bean.command;

import lombok.Data;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/10/31
 * @descibe none
 * </pre>
 */
@Data
public class ReccoCmdInfo {

	/**
	 * 呼救器设备号
	 */
	private String devEui;


	/**
	 *呼救器命令状态
	 */
	private ReccoCmdStatusInfo cmdStatusInfo;

}
