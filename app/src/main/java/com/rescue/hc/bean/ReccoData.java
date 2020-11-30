package com.rescue.hc.bean;

import java.io.Serializable;

import lombok.Data;

/**
 * @author DevCheng
 * @description
 * @date 2018/07/10
 */
@Data
public class ReccoData implements Serializable {
	private static final long serialVersionUID = -6743567631108323094L;
	/**
	 * 呼救器设备编号
	 */
	private String devEui;

	/**
	 * 子机编号
	 */
	private String reccoId;

	/**
	 * 温度 (环境)
	*/
	 private Float temperature;

	/**
	 * 经度
	 */
	private Double longitude;
	/**
	 * 纬度
	 */
	private Double latitude;
	/**
	 * 报警状态
	 */
	private Byte alarmState;
	/**
	 * 上次命令
	 */
	private Byte command=0;

	/**
	 * 姿态
	 */
	private Byte gesture;


	/**
	 * 电量
	 */
	private Byte electric;


}
