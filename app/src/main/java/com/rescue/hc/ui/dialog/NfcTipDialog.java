package com.rescue.hc.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.rescue.hc.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/11/04
 * @descibe none
 * </pre>
 */
public class NfcTipDialog extends Dialog {
	public NfcTipDialog(@NonNull Context context) {
		super(context);
	}

	public NfcTipDialog(@NonNull Context context, int themeResId) {
		super(context, themeResId);
	}

	protected NfcTipDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_nfc_tip);
		setCanceledOnTouchOutside(true);
	}


}
