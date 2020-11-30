package com.rescue.hc.ui.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.personal.framework.utils.StringUtil;
import com.rescue.hc.R;
import com.rescue.hc.bean.ReccoData;
import com.rescue.hc.bean.command.GroupInfoVo;
import com.rescue.hc.lib.util.ConvertUtils;

import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;

import static com.rescue.hc.lib.component.ReccoTag.DEFAULT_FIREMAN_NAME;
import static com.rescue.hc.lib.component.ReccoTag.DEFAULT_GROUP_NAME;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/11/08
 * @descibe none
 * </pre>
 */
public class GroupInfoAdapter extends BaseQuickAdapter<GroupInfoVo, BaseViewHolder> {
	public GroupInfoAdapter(int layoutResId, @Nullable List<GroupInfoVo> data) {
		super(layoutResId, data);
	}

	@Override
	protected void convert(BaseViewHolder helper, GroupInfoVo item) {

		if(item==null)
		{
			return;
		}

		ReccoData reccoData = item.getReccoData();
        helper.setText(R.id.group_number, isEmptyStr(item.getGroupNum(),DEFAULT_GROUP_NAME))
                .setText(R.id.group_person_name, isEmptyStr(item.getPersonName(),DEFAULT_FIREMAN_NAME));
		if(reccoData!=null)
		{
			helper.setText(R.id.group_person_id, reccoData.getReccoId())
					.setText(R.id.group_temperature, isEmptyStr(reccoData.getTemperature(),"0"))
					.setText(R.id.group_longitude, isEmptyStr(String.format(Locale.CHINA, "%.6f", item.getReccoData().getLongitude()),"0"))
					.setText(R.id.group_latitude, isEmptyStr(String.format(Locale.CHINA, "%.6f", item.getReccoData().getLatitude()),"0"));

			byte alarmState=item.getReccoData().getAlarmState();

			byte bit=GetBit(alarmState,7);
			if(bit==1)
			{
				helper.setText(R.id.group_alarm_state, ConvertUtils.getReccoAlarmState(5));
			}
			else
			{
				helper.setText(R.id.group_alarm_state, ConvertUtils.getReccoAlarmState((alarmState & 0x70) >> 4));
			}

		}



//		helper.addOnClickListener(R.id.task_detail)
//				.addOnClickListener(R.id.task_record)
//				.addOnClickListener(R.id.task_upload);
	}

	private byte GetBit(byte b,int index)
	{
		return (byte)(((b & (1 << index)) >0)?1:0);
	}

	private String isEmptyStr(Object str,String string) {
		if (str == null) {
			return string;
		} else if (str instanceof Float) {
			return String.format(Locale.CHINA, "%.0f", (Float) str);
		}
		else if(str instanceof String)
		{
			if(StringUtil.isEmpty(String.valueOf(str)))
			{
				return string;
			}
			else
			{
				return String.valueOf(str);
			}

		}
		else {
			return String.valueOf(str);
		}
	}
}
