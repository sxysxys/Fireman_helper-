package com.rescue.hc.bean.command;


import com.rescue.hc.bean.ReccoData;

import lombok.Data;

/**
 * @author DevCheng
 * @description 消防员相关绑定表现层数据
 * @date 2018/07/10
 */

@Data
public class FiremanReccoVo {

	/**
	 * 消防员个人信息
	 */
	private Fireman fireman;
	/**
	 * 呼救器采集的数据
	 */
	private ReccoData reccoData;

	/**
	 * 单个人员的命令状态，记录的是点击以后的
	 */
	private ReccoCmdStatusInfo cmdStatusInfo;

	/**
	 * 轮训次数
	 */
	private int queryCounter;

	/**
	 * 动画状态
	 */
	private byte animationState;

}
