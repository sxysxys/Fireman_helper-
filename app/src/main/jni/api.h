/*
 * CommonApi.h
 *
 *  Created on: 2014-11-6
 *      Author: kongli
 */
#include <jni.h>
/* Header for class android_zyapi_CommonApi */

#ifndef _Included_android_zyapi_CommonApi
#define _Included_android_zyapi_CommonApi
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     android_zyapi_CommonApi
 * Method:    GetLibVersion
 * Signature: ()S
 */
JNIEXPORT jstring JNICALL Java_android_zyapi_CommonApi_GetLibVersion
  (JNIEnv *, jobject);

/*
 * Class:     Java_android_zyapi_CommonApi
 * Method:    setGpioMode
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_Java_android_zyapi_CommonApi_setGpioMode
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     Java_android_zyapi_CommonApi
 * Method:    setGpioDir
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_Java_android_zyapi_CommonApi_setGpioDir
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     Java_android_zyapi_CommonApi
 * Method:    setGpioPullEnable
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_Java_android_zyapi_CommonApi_setGpioPullEnable
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     Java_android_zyapi_CommonApi
 * Method:    setGpioPullSelect
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_Java_android_zyapi_CommonApi_setGpioPullSelect
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     Java_android_zyapi_CommonApi
 * Method:    setGpioOut
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_Java_android_zyapi_CommonApi_setGpioOut
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     Java_android_zyapi_CommonApi
 * Method:    getGpioIn
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_Java_android_zyapi_CommonApi_getGpioIn
  (JNIEnv *, jobject, jint);

/*
 * Class:     Java_android_zyapi_CommonApi
 * Method:    openCom
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_Java_android_zyapi_CommonApi_openCom
  (JNIEnv *, jobject, jstring, jint, jint, jchar);

/*
 * Class:     Java_android_zyapi_CommonApi
 * Method:    openCom
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_Java_android_zyapi_CommonApi_openComEx
  (JNIEnv *env, jobject thiz, jstring port, jint speed, jint bits, jchar event, jint stop, jint flags);

/*
 * Class:     Java_android_zyapi_CommonApi
 * Method:    closeCom
 * Signature: (I)I
 */
JNIEXPORT void JNICALL Java_Java_android_zyapi_CommonApi_closeCom
  (JNIEnv *, jobject, jint);

/*
 * Class:     Java_android_zyapi_CommonApi
 * Method:    readCom
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_Java_android_zyapi_CommonApi_readCom
  (JNIEnv *, jobject, jint, jbyteArray, jint);

/*
 * Class:     Java_android_zyapi_CommonApi
 * Method:    readComEx
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_Java_android_zyapi_CommonApi_readComEx
  (JNIEnv *, jobject, jint, jbyteArray, jint, jint, jint);

/*
 * Class:     Java_android_zyapi_CommonApi
 * Method:    writeCom
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_Java_android_zyapi_CommonApi_writeCom
  (JNIEnv *, jobject, jint, jbyteArray, jint);

#ifdef __cplusplus
}
#endif
#endif /* _Included_android_zyapi_CommonApi */
