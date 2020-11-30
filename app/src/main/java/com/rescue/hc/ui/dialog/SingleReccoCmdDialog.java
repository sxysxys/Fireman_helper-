package com.rescue.hc.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.rescue.hc.R;
import com.rescue.hc.bean.command.ReccoCmdStatusInfo;
import com.rescue.hc.enums.ReccoCmdEnum;
import com.rescue.hc.lib.util.ConvertUtils;
import com.rescue.hc.ui.view.CircleMenuLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


/**
 * <pre>
 * @author Created by szc
 * @date on 2018/09/07
 * @descibe none
 * </pre>
 */
public class SingleReccoCmdDialog extends Dialog {
	private CircleMenuLayout mCircleMenuLayout;
	private ImageView imgCircle;
	private Context context;
	private onSingleCmdListener listener;
	private ReccoCmdStatusInfo cmdStatusInfo;

	private String[] mItemTexts = new String[]{"单体呼叫 ", "单体撤退"};
	private int[] mItemImgs = new int[]{R.mipmap.ic_5,
			R.mipmap.ic_back};

	public SingleReccoCmdDialog(@NonNull Context context) {
		super(context);
	}

	public SingleReccoCmdDialog(@NonNull Context context, int themeResId) {
		super(context, themeResId);
	}

	protected SingleReccoCmdDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public SingleReccoCmdDialog(Context context, ReccoCmdStatusInfo cmdStatusInfo, int themeResId, onSingleCmdListener listener) {
		super(context, themeResId);
		this.context = context;
		this.listener = listener;
		this.cmdStatusInfo = cmdStatusInfo;
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_cmd);
		setCanceledOnTouchOutside(true);
		imgCircle = findViewById(R.id.img_circle);
		//setCmd();
		mCircleMenuLayout = findViewById(R.id.id_menulayout);
		mCircleMenuLayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);


		mCircleMenuLayout.setOnMenuItemClickListener(new CircleMenuLayout.OnMenuItemClickListener() {

			@Override
			public void itemClick(View view, int pos) {

				sendData(new ReccoCmdStatusInfo(ConvertUtils.getReccoCmdType(pos+1)));
				//ToastUtils.info(String.valueOf(ConvertUtils.getReccoCmdType(pos+1)), false);
			}

			@Override
			public void itemCenterClick(View view) {
				dismiss();
//				Toast.makeText(context,
//						"you can do something just like ccb  ",
//						Toast.LENGTH_SHORT).show();
			}
		});

	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	private void setCmd() {
		if (cmdStatusInfo.getCmd() == ReccoCmdEnum.CALL_CMD.getCmd()) {
			imgCircle.setBackground(context.getResources().getDrawable(R.mipmap.ic_5));
		} else if (cmdStatusInfo.getCmd() == ReccoCmdEnum.RETREAT_CMD.getCmd()) {
			imgCircle.setBackground(context.getResources().getDrawable(R.mipmap.ic_back));
		}



	}

	private void sendData(ReccoCmdStatusInfo cmdStatusInfo) {
		if (listener != null) {
			listener.onClick(this, cmdStatusInfo);
		}
	}

	public interface onSingleCmdListener {
		void onClick(Dialog dialog, ReccoCmdStatusInfo cmdStatusInfo);
	}
}
