package com.rescue.hc.enums;

/**
 * 消防员姿态
 */
public enum  FiremanPoseEnum {

    STAND_STATE(0, 0, "站立"),
    WALK_STATE(1, 1, "行走"),
    PROSTRATE_STATE(4, 4, "俯卧"),
    LIE_SUPINE_STATE(5, 5, "仰卧"),
    RUN_STATE(6, 6, "跑"),
    LEFT_SIDE_STATE(2, 2, "左侧卧"),
    RIGHT_SIDE_STATE(3, 3, "右侧卧");



    private int code;
    private int cmd;
    private String msg;

    FiremanPoseEnum(int code, int cmd, String msg) {
        this.code = code;
        this.cmd = cmd;
        this.msg = msg;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
