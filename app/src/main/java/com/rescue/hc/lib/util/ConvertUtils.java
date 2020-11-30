package com.rescue.hc.lib.util;


import com.rescue.hc.enums.FiremanPoseEnum;
import com.rescue.hc.enums.ReccoAlarmStateEnum;
import com.rescue.hc.enums.ReccoCmdEnum;

/**
 * @author DevCheng
 * @description 转化的工具类
 * @date 2018/07/10
 */

public class ConvertUtils {

	/**
	 * 获取呼救器报警状态
	 * @param type
	 * @return
	 */
	public static String getReccoAlarmState(int type)
	{
		String str = "";
		if (type == ReccoAlarmStateEnum.NORMAL_STATE.getCode()) {
			str = ReccoAlarmStateEnum.NORMAL_STATE.getMsg();
		} else if (type == ReccoAlarmStateEnum.PRE_ALARM_STATE.getCode()) {
			str = ReccoAlarmStateEnum.PRE_ALARM_STATE.getMsg();
		} else if (type == ReccoAlarmStateEnum.AUTOMATIC_STRONG_ALRAM_STATE.getCode()) {
			str = ReccoAlarmStateEnum.AUTOMATIC_STRONG_ALRAM_STATE.getMsg();
		} else if (type == ReccoAlarmStateEnum.MANUAL_STRONG_ALRAM_STATE.getCode()) {
			str = ReccoAlarmStateEnum.MANUAL_STRONG_ALRAM_STATE.getMsg();
		} else if (type == ReccoAlarmStateEnum.HIGH_TEMPERATURE_ALARM_STATE.getCode()) {
			str = ReccoAlarmStateEnum.HIGH_TEMPERATURE_ALARM_STATE.getMsg();
		} else if (type == ReccoAlarmStateEnum.LOW_VOLTAGE_ALARM_STATE.getCode()) {
			str = ReccoAlarmStateEnum.LOW_VOLTAGE_ALARM_STATE.getMsg();
		}
		return str;
	}

	public static int getReccoCmdType(int type) {
		int cmd = 0;
		if (type == ReccoCmdEnum.NONE_CMD.getCode()) {
			cmd = ReccoCmdEnum.NONE_CMD.getCmd();
		} else if (type == ReccoCmdEnum.CALL_CMD.getCode()) {
			cmd = ReccoCmdEnum.CALL_CMD.getCmd();
		} else if (type == ReccoCmdEnum.RETREAT_CMD.getCode()) {
			cmd = ReccoCmdEnum.RETREAT_CMD.getCmd();
		}
		return cmd;
	}

	public static String getReccoGesture(int type) {
		String str = "";
		if (type == FiremanPoseEnum.STAND_STATE.getCode()) {
			str = FiremanPoseEnum.STAND_STATE.getMsg();
		} else if (type == FiremanPoseEnum.WALK_STATE.getCode()) {
			str = FiremanPoseEnum.WALK_STATE.getMsg();
		} else if (type == FiremanPoseEnum.LEFT_SIDE_STATE.getCode()) {
			str = FiremanPoseEnum.LEFT_SIDE_STATE.getMsg();
		}else if (type == FiremanPoseEnum.RIGHT_SIDE_STATE.getCode()) {
			str = FiremanPoseEnum.RIGHT_SIDE_STATE.getMsg();
		}else if (type == FiremanPoseEnum.LIE_SUPINE_STATE.getCode()) {
			str = FiremanPoseEnum.LIE_SUPINE_STATE.getMsg();
		}else if (type == FiremanPoseEnum.PROSTRATE_STATE.getCode()) {
			str = FiremanPoseEnum.PROSTRATE_STATE.getMsg();
		}
		else if (type == FiremanPoseEnum.RUN_STATE.getCode()) {
			str = FiremanPoseEnum.RUN_STATE.getMsg();
		}
		return str;
	}








}
