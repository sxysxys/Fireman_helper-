package com.rescue.hc.bean.command;

import lombok.Data;

/**
 * @author DevCheng
 * @description 消防员实体类
 * @date 2018/07/10
 */
@Data
public class Fireman {

	/**
	 *  数据库主键id
	 */
	private int id;
	/**
	 *  消防员警号
	 */
	private String serialNumber;
	/**
	 *  姓名
	 */
	private String name;
	/**
	 *  组编号
	 */
	private String groupNumber;

	/**
	 *  性别（0:女，1:男）
	 */
	private String gender;
	/**
	 *  出生日期
	 */
	private String birth;
	/**
	 *  身高（单位：cm）
	 */
	private String height;
	/**
	 *  体重（单位：kg）
	 */
	private String weight;
	/**
	 *  血型（0：A型,1:B型,2:AB型,3:o型）
	 */
	private String bloodType;
	/**
	 *  入伍时间
	 */
	private String enlistTime;

	/**
	 *  军衔
	 */
	private String grade;
	/**
	 *  职位
	 */
	private String position;

	/**
	 *  营区编号
	 */
	private Integer campId;
	/**
	 *  状态（0：未连接，1：已连接未绑定，2：已连接已绑定）
	 */
	private Integer status;

	@Override
	public String toString() {
		return serialNumber + "," + name + "," + gender + "," + birth + "," + height + "," + weight + "," + bloodType + "," + enlistTime + "," + grade + "," + position+ "," + groupNumber;
	}

}
