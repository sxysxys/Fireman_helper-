package com.rescue.hc.presenter.main;

import com.rescue.hc.presenter.IPresenter;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/11/08
 * @descibe none
 * </pre>
 */
public class MainContract {
	public interface View {

		void unbindSuccess();

		void unbindFailed();
	}

	public interface Presenter extends IPresenter {
		void unbindPad();
	}
}
