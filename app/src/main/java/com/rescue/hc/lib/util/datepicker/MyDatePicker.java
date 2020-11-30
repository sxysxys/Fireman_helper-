package com.rescue.hc.lib.util.datepicker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/06/28
 * @descibe none
 * </pre>
 */

public class MyDatePicker {
	private Context mContext;
	private int mYear = 2018, mMonth = 6, mDay = 20;
	private DatePickerDialog datePickerDialog;
	private UpdateUIDate mUpdateUIDate;
	private int mode;
	private String type;

	public MyDatePicker(Context context) {
		mContext = context;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd",Locale.CHINA);
		Date curDate = new Date(System.currentTimeMillis());
		String[] str = formatter.format(curDate).split("/");
		mYear = Integer.valueOf(str[0]);
		mMonth = Integer.valueOf(str[1]);
		mDay = Integer.valueOf(str[2]);
		datePickerDialog = new DatePickerDialog(mContext, mDateListener, mYear, mMonth - 1, mDay);
	}

	public void getDate(int mode, String type) {
		this.mode = mode;
		this.type = type;
		datePickerDialog.show();
	}

	private DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
							  int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			mUpdateUIDate.setDate(String.valueOf(new StringBuffer().append(mYear).append(type).
					append(String.format(Locale.CHINA, "%02d", mMonth + 1)).append(type).
					append(String.format(Locale.CHINA, "%02d", mDay))), mode);
		}
	};

	public void setUpdateUIDate(UpdateUIDate mUpdateUIDate) {
		this.mUpdateUIDate = mUpdateUIDate;
	}
}
