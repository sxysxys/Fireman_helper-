/*
 * api.c
 *
 *  Created on: 2014-11-6
 *      Author: kongli
 */

#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>
#include <linux/ioctl.h>
#include <unistd.h>
#include <termios.h>

#include "api.h"
#include "log.h"
#include "mtgpio.h"
#include "serialport.h"

#define LIB_VERSION     "v1.0.1"
//onLoad方法，在System.loadLibrary()执行时被调用
jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	LOGV("zyapi_common so JNI_OnLoad ~~!!");
	int ret = openGpioDev();
	if (ret != 0) {
		LOGV("zyapi_common so load failed, ret:%d",ret);
		return -1;
	}
	LOGV("zyapi_common so load success");
	return JNI_VERSION_1_4;
}

//onUnLoad方法，在JNI组件被释放时调用
void JNI_OnUnload(JavaVM* vm, void* reserved){
	LOGD("CommonApi so JNI_OnUnload ~~!!");
	closeGpioDev();
}

/*
 * Class:     android_zyapi_CommonApi
 * Method:    GetLibVersion
 * Signature: ()I
 */
JNIEXPORT jstring JNICALL Java_android_zyapi_CommonApi_GetLibVersion
  (JNIEnv *env, jobject thiz){
	return (*env)->NewStringUTF(env, LIB_VERSION);;
}

JNIEXPORT int JNICALL Java_android_zyapi_CommonApi_setGpioMode
  (JNIEnv *env, jobject thiz, jint pin, jint mode)
{
	return setGpioMode(pin, mode);
}

JNIEXPORT int JNICALL Java_android_zyapi_CommonApi_setGpioDir
  (JNIEnv *env, jobject thiz, jint pin, jint dir)
{
	return setGpioDir(pin, dir);
}

JNIEXPORT int JNICALL Java_android_zyapi_CommonApi_setGpioPullEnable
  (JNIEnv *env, jobject thiz, jint pin, jint enbale)
{
	return setGpioPullEnable(pin, enbale);
}

JNIEXPORT int JNICALL Java_android_zyapi_CommonApi_setGpioPullSelect
  (JNIEnv *env, jobject thiz, jint pin, jint updown)
{
	return setGpioPullSelect(pin, updown);
}

JNIEXPORT int JNICALL Java_android_zyapi_CommonApi_setGpioOut
  (JNIEnv *env, jobject thiz, jint pin, jint out)
{
	return setGpioOut(pin, out);
}

JNIEXPORT int JNICALL Java_android_zyapi_CommonApi_getGpioIn
  (JNIEnv *env, jobject thiz, jint pin)
{
	return getGpioIn(pin);
}


JNIEXPORT jint JNICALL Java_android_zyapi_CommonApi_openCom
  (JNIEnv *env, jobject thiz, jstring port, jint speed, jint bits, jchar event, jint stop)
{
	const char* tmp = (*env)->GetStringUTFChars(env, port, NULL);
	int ret = openCom(tmp, speed, bits, event, stop);
	(*env)->ReleaseStringUTFChars(env, port, tmp);
	return ret;
}

JNIEXPORT jint JNICALL Java_android_zyapi_CommonApi_openComEx
  (JNIEnv *env, jobject thiz, jstring port, jint speed, jint bits, jchar event, jint stop, jint flags)
{
	const char* tmp = (*env)->GetStringUTFChars(env, port, NULL);
	int ret = openComEx(tmp, speed, bits, event, stop, flags);
	(*env)->ReleaseStringUTFChars(env, port, tmp);
	return ret;
}


JNIEXPORT void JNICALL Java_android_zyapi_CommonApi_closeCom
  (JNIEnv *env, jobject thiz, jint fd)
{
	closeCom(fd);
}


JNIEXPORT jint JNICALL Java_android_zyapi_CommonApi_readCom
  (JNIEnv *env, jobject thiz, jint fd, jbyteArray data, jint size)
{
	jint len = (*env)->GetArrayLength(env, data);
	jbyte* dataBuf = (*env)->GetByteArrayElements(env, data, JNI_FALSE);
	int ret = readCom(fd, dataBuf, size>len?len:size);
	if (ret > 0){
		//(*env)->SetByteArrayRegion(env, data, 0, len, mReadBuf);
	}
	(*env)->ReleaseByteArrayElements(env, data, dataBuf, JNI_FALSE);
	return ret;
}

JNIEXPORT jint JNICALL Java_android_zyapi_CommonApi_readComEx
  (JNIEnv *env, jobject thiz, jint fd, jbyteArray data, jint size, jint sec, jint usec)
{
	jint len = (*env)->GetArrayLength(env, data);
	jbyte* dataBuf = (*env)->GetByteArrayElements(env, data, JNI_FALSE);
	int ret = readComEx(fd, dataBuf, size>len?len:size, sec, usec);
	if (ret > 0){
		//(*env)->SetByteArrayRegion(env, data, 0, len, mReadBuf);
	}
	(*env)->ReleaseByteArrayElements(env, data, dataBuf, JNI_FALSE);
	return ret;
}

JNIEXPORT jint JNICALL Java_android_zyapi_CommonApi_writeCom
  (JNIEnv *env, jobject thiz, jint fd, jbyteArray data, jint size)
{
	jint len = (*env)->GetArrayLength(env, data);
	jbyte* dataBuf = (*env)->GetByteArrayElements(env, data, JNI_FALSE);
	int ret = writeCom(fd, dataBuf, size);
	(*env)->ReleaseByteArrayElements(env, data, dataBuf, JNI_FALSE);
	return ret;
}


