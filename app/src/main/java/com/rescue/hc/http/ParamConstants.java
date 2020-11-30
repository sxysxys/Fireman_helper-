package com.rescue.hc.http;

/**
 * http入参定义常量表
 * Created by januswong on 2018/1/29.
 */

public class ParamConstants {

    /**    发送验证码-type：1：新用户注册 2：用户修改密码   **/
    public static final String CAPTCHA_TYPE_REGISTER = "1";
    public static final String CAPTCHA_TYPE_FORGETPWD = "2";

    /**    修改密码-type：修改方式: 0:手机验证码 1:旧密码修改 **/
    public static final String MODIFYPWD_TYPE_FORGETPWD = "0";
    public static final String MODIFYPWD_TYPE_OLDPWD = "1";


    //来源 0:安卓 1:IOS 2:web
    public static final String  APP_SOURCE = "0";

    //1:绑定 0:解绑
    public static final String BINDING_TYPE_BIND = "1";
    public static final String BINDING_TYPE_UNBIND = "0";

    //绑定设备 来源 0:泰科 1:信达 2:自主研发
    public static final String BINDING_SOURCE_TAIKE = "0";
    public static final String BINDING_SOURCE_XINDA = "1";
    public static final String BINDING_SOURCE_SELF = "2";

    //int,性别 0:男 1:女 2:私密
    public static final int USERGENDER_MALE = 0;
    public static final int USERGENDER_FEMALE = 1;
    public static final int USERGENDER_PRIVATE = 2;

    //type：0查全部，1查我拥有的设备
    public static final int DEVICE_LIST_ALL = 0;
    public static final int DEVICE_LIST_OWN = 1;

    //"int，横竖屏标志位 1：竖屏 0：横屏"
    public static final int SCREEN_VERTI = 1;
    public static final int SCREEN_HORIZON = 0;

    //1：由竖屏切换成横屏2：由横屏切换成竖屏
    public static final int SWITCHSCREEN_HORIZON = 1;
    public static final int SWITCHSCREEN_VERTI = 2;
}
