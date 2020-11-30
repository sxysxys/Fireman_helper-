package com.rescue.hc.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rescue.hc.R;
import com.rescue.hc.bean.command.Fireman;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * <pre>
 * @author Created by szc
 * @date on 2018/09/08
 * @descibe none
 * </pre>
 */
public class MessageDialog extends Dialog implements View.OnClickListener {
	private String message;
	private onMessageClickListener listener;
	private Fireman fireman;
	private TextView tvContent;
	private ImageView imgLogo;
	private int id;
	private boolean isDisplay = true;

	public MessageDialog(@NonNull Context context) {
		super(context);
	}

	public MessageDialog(@NonNull Context context, int themeResId) {
		super(context, themeResId);
	}

	protected MessageDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public MessageDialog(Context context, int themeResId, onMessageClickListener listener) {
		super(context, themeResId);
		this.listener = listener;
	}

	public MessageDialog setContent(String message) {
		this.message = message;
		if (tvContent != null) {
			tvContent.setText(message);
		}
		return this;
	}

	public MessageDialog setFireman(Fireman fireman) {
		this.fireman = fireman;
		return this;
	}

	public MessageDialog setId(int id) {
		this.id = id;
		return this;
	}

	public MessageDialog setImgVisible(boolean isDisplay) {
		this.isDisplay = isDisplay;
		return this;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_dialog);
		setCanceledOnTouchOutside(true);
		initView();

	}


	private void initView() {
		imgLogo = findViewById(R.id.iv_logo);
		TextView tvSure = findViewById(R.id.tv_sure);
		TextView tvCancel = findViewById(R.id.tv_cancel);
		tvContent = findViewById(R.id.tv_content);
		tvSure.setOnClickListener(this);
		tvCancel.setOnClickListener(this);
		if (!TextUtils.isEmpty(message)) {
			tvContent.setText(message);
		}
		if (!isDisplay) {
			imgLogo.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_cancel:
				if (listener != null) {
					listener.onClick(this, false, fireman, id);
				}
				break;
			case R.id.tv_sure:
				if (listener != null) {
					listener.onClick(this, true, fireman, id);
				}
				break;
			default:
				break;
		}
	}

	public interface onMessageClickListener {
		void onClick(Dialog dialog, boolean confirm, Fireman fireman, int id);
	}
}
