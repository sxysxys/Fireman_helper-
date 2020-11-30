package com.rescue.hc.presenter.login;

import com.rescue.hc.presenter.IPresenter;

/**
 * <pre>
 * @author Created by szc
 * @date on 2018/10/29
 * @descibe none
 * </pre>
 */
public class LoginContract {
	public interface View {
		void loginSuccess(Object o);

		void loginFailed();
	}

	public interface Presenter extends IPresenter {
		void login(String name, String password);
	}
}
