package com.rescue.hc.bean.command;

import com.rescue.hc.bean.ReccoData;


/**
 * 分组信息
 */

public class GroupInfoVo extends GroupInfo{

	private ReccoData reccoData;

	public ReccoData getReccoData() {
		return reccoData;
	}

	public void setReccoData(ReccoData reccoData) {
		this.reccoData = reccoData;
	}
}
