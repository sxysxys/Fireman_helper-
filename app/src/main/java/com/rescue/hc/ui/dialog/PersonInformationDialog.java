package com.rescue.hc.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.rescue.hc.R;
import com.rescue.hc.bean.command.Fireman;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/09/06
 * @descibe none
 * </pre>
 */
public class PersonInformationDialog extends Dialog implements View.OnClickListener {
	private OnCloseListener listener;

	private TextView tvSerialNumber, tvGender, tvAge, tvHeight, tvWeight, tvBlood, tvEnlistTime, tvGrade, tvPosition;
	private EditText tvName;
	private TextView tvCancel, tvSubmit;
	private Fireman fireman;

	public PersonInformationDialog(@NonNull Context context) {
		super(context);
	}

	public PersonInformationDialog(@NonNull Context context, int themeResId) {
		super(context, themeResId);
	}

	protected PersonInformationDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public PersonInformationDialog(Context context, int themeResId, Fireman fireman, OnCloseListener listener) {
		super(context, themeResId);
		this.listener = listener;
		this.fireman = fireman;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fireman_info_layout);
		setCanceledOnTouchOutside(true);
		initView();
	}

	private void initView() {
		tvSerialNumber = findViewById(R.id.tv_info_serial_number);
		tvName = findViewById(R.id.tv_info_name);
		tvGender = findViewById(R.id.tv_info_gender);
		tvAge = findViewById(R.id.tv_info_age);
		tvHeight = findViewById(R.id.tv_info_height);
		tvWeight = findViewById(R.id.tv_info_weight);
		tvBlood = findViewById(R.id.tv_info_blood_type);
		tvEnlistTime = findViewById(R.id.tv_info_enlist_time);
		tvGrade = findViewById(R.id.tv_info_grade);
		tvPosition = findViewById(R.id.tv_info_position);
		tvCancel = findViewById(R.id.tv_info_cancel);
		tvSubmit = findViewById(R.id.tv_info_submit);
		tvCancel.setOnClickListener(this);
		tvSubmit.setOnClickListener(this);
		setInfoMeg(fireman);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_info_cancel:
				if (listener != null) {
					listener.onClick(this,fireman, false);
				}
				break;
			case R.id.tv_info_submit:
				if (listener != null) {

					fireman.setSerialNumber(tvSerialNumber.getText().toString().trim());
					fireman.setName(tvName.getText().toString().trim());
					listener.onClick(this,fireman,true);
				}
				break;
			default:
				break;
		}
	}

	@SuppressLint("SetTextI18n")
	private void setInfoMeg(Fireman fireman) {
		tvSerialNumber.setText(fireman.getSerialNumber());
		tvName.setText(fireman.getName());
		tvGender.setText(fireman.getGender());
		tvAge.setText(fireman.getBirth());
		tvHeight.setText(fireman.getHeight() + " cm");
		tvWeight.setText(fireman.getWeight() + " kg");
		tvBlood.setText(fireman.getBloodType());
		tvEnlistTime.setText(fireman.getEnlistTime());
		tvGrade.setText(fireman.getGrade());
		tvPosition.setText(fireman.getPosition());
	}


	public interface OnCloseListener {
		void onClick(Dialog dialog,Fireman fireman, boolean confirm);
	}
}
