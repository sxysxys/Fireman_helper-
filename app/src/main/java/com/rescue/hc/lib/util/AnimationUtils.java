package com.rescue.hc.lib.util;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

/**
 * @author DevCheng
 * @description 动画工具类
 * @date 2018/07/12
 */

public class AnimationUtils {

	/**
	 * 从不透明到透明
	 * @param duration
	 * @return
	 */
	public static Animation getAlphaAnimationIn(int duration) {

		//实例化 AlphaAnimation 主要是改变透明度
		//透明度 从 1-不透明 0-完全透明
		Animation animation = new AlphaAnimation(1.0f, 0);

		//设置动画插值器 被用来修饰动画效果,定义动画的变化率
		animation.setInterpolator(new DecelerateInterpolator());

		//设置动画执行时间
		animation.setDuration(duration);
		return animation;
	}

	/**
	 * 从透明到不透明
	 * @param duration
	 * @return
	 */
	public static Animation getAlphaAnimationOut(int duration) {

		//实例化 AlphaAnimation 主要是改变透明度
		//透明度 从 1-不透明 0-完全透明
		Animation animation = new AlphaAnimation(0, 1.0f);

		//设置动画插值器 被用来修饰动画效果,定义动画的变化率
		animation.setInterpolator(new DecelerateInterpolator());

		//设置动画执行时间
		animation.setDuration(duration);
		return animation;
	}

}
