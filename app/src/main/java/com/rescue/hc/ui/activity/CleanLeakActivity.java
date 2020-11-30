package com.rescue.hc.ui.activity;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


/**
 * <pre>
 * 解决InputMethodManager 导致的内存泄露及解决方案
 * @author Created by szc
 * @date on 2018/11/06
 * @descibe none
 * </pre>
 */
public class CleanLeakActivity extends AppCompatActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
				//500毫秒后结束
			}
		}, 500);
	}
}
