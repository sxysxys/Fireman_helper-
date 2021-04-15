LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

TARGET_PLATFORM := android-3
LOCAL_MODULE    := zyapi_common
LOCAL_SRC_FILES := api.c mtgpio.c serialport.c pub_func.c
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)
