/*
 * log.h
 *
 *  Created on: 2014-11-6
 *      Author: kongli
 */

#ifndef LOG_H_
#define LOG_H_

#include "android/log.h"

static const char* TAG = "CommonApi";

#define DEBUG
/*serialport*/
//#define SERIALPORT_LOGV
#define SERIALPORT_LOGD


#ifdef DEBUG
	#define LOGV(fmt, args...) __android_log_print(ANDROID_LOG_VERBOSE,  TAG, fmt, ##args)
	#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
	#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
	#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)
#else
	#define LOGV(fmt, args...)
	#define LOGI(fmt, args...)
	#define LOGD(fmt, args...)
	#define LOGE(fmt, args...)
#endif

#endif /* LOG_H_ */
