package com.rescue.hc.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.rescue.hc.R;
import com.rescue.hc.bean.command.ReccoCmdStatusInfo;
import com.rescue.hc.lib.util.ConvertUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/09/06
 * @descibe none
 * </pre>
 */
public class BulkSendDialog extends Dialog implements View.OnClickListener, View.OnTouchListener {
	private onTouchListener listener;
	private Context context;
	private ReccoCmdStatusInfo cmdStatusInfo;

	public BulkSendDialog(@NonNull Context context) {
		super(context);
	}

	public BulkSendDialog(@NonNull Context context, int themeResId) {
		super(context, themeResId);
	}

	protected BulkSendDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public BulkSendDialog(Context context, ReccoCmdStatusInfo cmdStatusInfo, int themeResId, onTouchListener listener) {
		super(context, themeResId);
		this.listener = listener;
		this.context = context;
		this.cmdStatusInfo = cmdStatusInfo;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bulk_recco_send);
		setCanceledOnTouchOutside(true);
		ImageView imgAttack = findViewById(R.id.attack);
		imgAttack.setOnClickListener(this);
		imgAttack.setOnTouchListener(this);
		ImageView imgRetreat = findViewById(R.id.retreat);
		imgRetreat.setOnClickListener(this);
		imgRetreat.setOnTouchListener(this);



	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.attack:
				if (listener != null) {
					listener.onClick(this, new ReccoCmdStatusInfo(ConvertUtils.getReccoCmdType(1)));
				}
				break;
			case R.id.retreat:
				if (listener != null) {
					listener.onClick(this, new ReccoCmdStatusInfo(ConvertUtils.getReccoCmdType(2)));
				}
				break;
//			case R.id.set:
//				if (listener != null) {
//					listener.onClick(this, new ReccoCmdStatusInfo(ConvertUtils.getWatchCmdType(2)));
//				}
//				break;
//			case R.id.stand:
//				if (listener != null) {
//					listener.onClick(this, new ReccoCmdStatusInfo(ConvertUtils.getWatchCmdType(3)));
//				}
//				break;
//			case R.id.clear_cmd:
//				if (listener != null) {
//					listener.onClick(this, new ReccoCmdStatusInfo(ConvertUtils.getWatchCmdType(4)));
//				}
//				break;
			default:
				break;
		}
	}


	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		Animation animDown = AnimationUtils.loadAnimation(context, R.anim.photo_scale_down);
		Animation animUp = AnimationUtils.loadAnimation(context, R.anim.photo_scale_up);
		ImageView layout = (ImageView) view;
		switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				layout.startAnimation(animUp);
				animUp.setFillAfter(true);
				break;
			case MotionEvent.ACTION_UP:
				layout.startAnimation(animDown);
				animDown.setFillAfter(true);
				break;
			default:
				break;
		}
		return false;
	}

	public interface onTouchListener {
		void onClick(Dialog dialog, ReccoCmdStatusInfo cmdStatusInfo);
	}
}
