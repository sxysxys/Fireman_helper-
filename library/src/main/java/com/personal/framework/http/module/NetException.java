package com.personal.framework.http.module;

import java.io.IOException;

/**
 * com.feeyo.vz.pro.exception
 * Author : erik
 * Email  : erik7@126.com
 * Date   : 17/5/26
 * Version 1.0
 * Description:
 * <p>
 * <p>
 * Copyright (c) 2017 erik
 * All Rights Reserved.
 */

public class NetException extends IOException {

    private final int code;

    private final String data;

    public NetException(int code, String msg, String data) {
        super(msg);
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getData() {
        return data;
    }
}
