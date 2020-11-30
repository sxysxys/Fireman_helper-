package com.rescue.hc.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rescue.hc.R;
import com.rescue.hc.bean.command.Fireman;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/11/04
 * @descibe none
 * </pre>
 */
public class NfcInfoSetDialog extends Dialog implements View.OnClickListener {
	private NfcInfoSetDialog.OnCloseListener listener;
	private EditText tvName, tvGender, tvAge, tvHeight, tvWeight, tvBlood, tvEnlistTime, tvGrade, tvPosition;
	private TextView tvSerialNumber, tvCancel, tvSubmit;
	private LinearLayout emptyLl, infoLl;

	public NfcInfoSetDialog(@NonNull Context context) {
		super(context);
	}

	public NfcInfoSetDialog(@NonNull Context context, int themeResId) {
		super(context, themeResId);
	}

	protected NfcInfoSetDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public NfcInfoSetDialog(Context context, int themeResId, NfcInfoSetDialog.OnCloseListener listener) {
		super(context, themeResId);
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_info);
		setCanceledOnTouchOutside(false);
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
		emptyLl = findViewById(R.id.empty_ll);
		infoLl = findViewById(R.id.info_ll);
		tvCancel = findViewById(R.id.tv_info_cancel);
		tvSubmit = findViewById(R.id.tv_info_submit);
		tvCancel.setOnClickListener(this);
		tvSubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_info_cancel:
				if (listener != null) {
					listener.onClick(this, false, null);
				}
				break;
			case R.id.tv_info_submit:
				if (listener != null) {
					listener.onClick(this, true, getFireman());
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void onBackPressed() {

	}

	@SuppressLint("SetTextI18n")
	public void setInfoMeg(Fireman fireman) {
		emptyLl.setVisibility(View.GONE);
		infoLl.setVisibility(View.VISIBLE);
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

	public void setTvSubmit() {
		if (emptyLl.getVisibility() == View.VISIBLE) {
			return;
		}
		setEditTextFocus(false);
		tvSubmit.setText("请靠近手表写入");
	}

	public boolean getDisplayStatus() {
		return emptyLl.getVisibility() != View.VISIBLE;
	}

	public void setEditTextFocus(boolean status) {
		tvName.setFocusable(status);
		tvGender.setFocusable(status);
		tvAge.setFocusable(status);
		tvHeight.setFocusable(status);
		tvWeight.setFocusable(status);
		tvBlood.setFocusable(status);
		tvEnlistTime.setFocusable(status);
		tvGrade.setFocusable(status);
		tvPosition.setFocusable(status);
		if (status) {
			getFocus(tvName);
			getFocus(tvGender);
			getFocus(tvAge);
			getFocus(tvHeight);
			getFocus(tvWeight);
			getFocus(tvBlood);
			getFocus(tvEnlistTime);
			getFocus(tvGrade);
			getFocus(tvPosition);
		}

	}

	private void getFocus(EditText editText) {
		editText.setFocusableInTouchMode(true);
		editText.requestFocus();
		editText.requestFocusFromTouch();
	}

	public void clearInfoMeg() {
		if (emptyLl.getVisibility() == View.VISIBLE) {
			return;
		}
		emptyLl.setVisibility(View.VISIBLE);
		infoLl.setVisibility(View.GONE);
		tvSerialNumber.setText("");
		tvName.setText("");
		tvGender.setText("");
		tvAge.setText("");
		tvHeight.setText("");
		tvWeight.setText("");
		tvBlood.setText("");
		tvEnlistTime.setText("");
		tvGrade.setText("");
		tvPosition.setText("");
		setEditTextFocus(true);
		tvSubmit.setText("确定");
	}

	public Fireman getFireman() {
		if (!getDisplayStatus()) {
			return null;
		}
		Fireman fireman = new Fireman();
		fireman.setSerialNumber(tvSerialNumber.getText().toString().trim());
		fireman.setName(tvName.getText().toString().replace(" ", ""));
		fireman.setGender(tvGender.getText().toString().replace(" ", ""));
		fireman.setBirth(tvAge.getText().toString().replace(" ", ""));
		String height = tvHeight.getText().toString().replace(" ", "");
		fireman.setHeight(height.substring(0, height.length() - 2));
		String weight = tvWeight.getText().toString().replace(" ", "");
		fireman.setWeight(weight.substring(0, weight.length() - 2));
		fireman.setBloodType(tvBlood.getText().toString().replace(" ", ""));
		fireman.setEnlistTime(tvEnlistTime.getText().toString().replace(" ", ""));
		fireman.setGrade(tvGrade.getText().toString().replace(" ", ""));
		fireman.setPosition(tvPosition.getText().toString().replace(" ", ""));
		return fireman;
	}

	public interface OnCloseListener {
		void onClick(Dialog dialog, boolean confirm, Fireman fireman);
	}
}
