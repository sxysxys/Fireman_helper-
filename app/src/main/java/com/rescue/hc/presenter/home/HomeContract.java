package com.rescue.hc.presenter.home;

import com.rescue.hc.presenter.IPresenter;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/10/29
 * @descibe none
 * </pre>
 */
public class HomeContract {
	public interface View {
		void hideTip();

//		void bindSuccess(String watchIds);
//
//		void unbindSuccess(String watchIds);
//
//		void bindFailed();
//
//		void unbindFailed();
	}

	public interface Presenter extends IPresenter {
		void delayTipShow();

//		void bindPad(String watchIds);
//
//		void unbindPad(String watchIds);
	}
}
