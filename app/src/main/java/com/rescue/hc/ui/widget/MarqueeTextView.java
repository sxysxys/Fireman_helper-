package com.rescue.hc.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author DevCheng
 *
 * XML方式：对于一个大View中有很多子View来说，同一时刻只能有一个子View获得focus！
 * 也就是说当前这一屏上，最多只能有一个view能有跑马灯效果，而不能多个View同事都有跑马灯效果
 *
 * 让多个View显示跑马灯效果：自定义TextView 重写isFocused()函数，让他放回true也就是一直获取了
 *
 * @description
 * @date 2018/07/11
 */

public class MarqueeTextView extends AppCompatTextView {
	public MarqueeTextView(Context context) {
		super(context);
	}

	public MarqueeTextView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public MarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public boolean isFocused() {
		return true;
	}

}
