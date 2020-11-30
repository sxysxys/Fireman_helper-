package com.rescue.hc.lib.component;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/10/31
 * @descibe none
 * </pre>
 */
public class UartTag {
	public static final int baudRate = 115200;
	public static final int dataBits = 8;

	public static final int maxReadBytes = 21;
	public static final int readDuration = 100;
	public static final int writeTimeoutMills = 200;

	public static final byte dataFrameHeader = (byte) 0xFE;
	public static final byte dataFrameTail = (byte) 0xEF;

	public static final byte readFrameHeader = 0x24;
	public static final byte writeFrameHeader = 0x25;

	public static final byte writeFrameAddr = (byte) 0xC1;
	public static final byte readFrameAddr = 0x40;

	public static final int capatity=32;

	/**
	 * 气压基值
	 */
	public static final int PressureValue = 100000;

	public static final int spTrainTime = 4000;

	/**
	 * 10s更新一次主界面 防止连接全部丢失 而不发生更新主界面情况
	 */
	public static final int homeTrainTime = (spTrainTime / 1000) * 5;

	public static final int bdHwei = 16;

	/**
	 * 轮训2圈
	 */
	public static final int connectCounter = 2;

	/**
	 * 对于未绑定的id 每4s轮训发送
	 */
	public static final int bindCounter = 5;

}
