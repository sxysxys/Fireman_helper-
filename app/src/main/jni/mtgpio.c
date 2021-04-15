/*
 * mtgpio.c
 *
 *  Created on: 2014-11-6
 *      Author: kong
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
#include "log.h"

#define GPIO_IOC_MAGIC 0x90

#define GPIO_IOCQMODE           _IOR(GPIO_IOC_MAGIC, 0x01, uint32_t)
#define GPIO_IOCTMODE0          _IOW(GPIO_IOC_MAGIC, 0x02, uint32_t)
#define GPIO_IOCTMODE1          _IOW(GPIO_IOC_MAGIC, 0x03, uint32_t)
#define GPIO_IOCTMODE2          _IOW(GPIO_IOC_MAGIC, 0x04, uint32_t)
#define GPIO_IOCTMODE3          _IOW(GPIO_IOC_MAGIC, 0x05, uint32_t)
#define GPIO_IOCQDIR            _IOR(GPIO_IOC_MAGIC, 0x06, uint32_t)
#define GPIO_IOCSDIRIN          _IOW(GPIO_IOC_MAGIC, 0x07, uint32_t)
#define GPIO_IOCSDIROUT         _IOW(GPIO_IOC_MAGIC, 0x08, uint32_t)
#define GPIO_IOCQPULLEN         _IOR(GPIO_IOC_MAGIC, 0x09, uint32_t)
#define GPIO_IOCSPULLENABLE     _IOW(GPIO_IOC_MAGIC, 0x0A, uint32_t)
#define GPIO_IOCSPULLDISABLE    _IOW(GPIO_IOC_MAGIC, 0x0B, uint32_t)
#define GPIO_IOCQPULL           _IOR(GPIO_IOC_MAGIC, 0x0C, uint32_t)
#define GPIO_IOCSPULLDOWN       _IOW(GPIO_IOC_MAGIC, 0x0D, uint32_t)
#define GPIO_IOCSPULLUP         _IOW(GPIO_IOC_MAGIC, 0x0E, uint32_t)
#define GPIO_IOCQINV            _IOR(GPIO_IOC_MAGIC, 0x0F, uint32_t)
#define GPIO_IOCSINVENABLE      _IOW(GPIO_IOC_MAGIC, 0x10, uint32_t)
#define GPIO_IOCSINVDISABLE     _IOW(GPIO_IOC_MAGIC, 0x11, uint32_t)
#define GPIO_IOCQDATAIN         _IOR(GPIO_IOC_MAGIC, 0x12, uint32_t)
#define GPIO_IOCQDATAOUT        _IOR(GPIO_IOC_MAGIC, 0x13, uint32_t)
#define GPIO_IOCSDATALOW        _IOW(GPIO_IOC_MAGIC, 0x14, uint32_t)
#define GPIO_IOCSDATAHIGH       _IOW(GPIO_IOC_MAGIC, 0x15, uint32_t)

#define GPIO_IOCVERIFY       _IOW(GPIO_IOC_MAGIC, 0xff, uint32_t)

#define MTGPIO_DEV "/dev/mtgpio"
static int mFD = -1;

int openGpioDev(void) {
	mFD = open(MTGPIO_DEV, O_RDONLY);
	if (mFD <= 0){
		LOGE("openGpioDev->open failed!");
		return -1;
	}
	LOGD("openGpioDev->mFD:%d",mFD);
	if (ioctl(mFD, GPIO_IOCVERIFY, 0) != 0){
		LOGE("openGpioDev->verify fail!!!");
		close(mFD);
		mFD = -1;
		return -2;
	}

	return 0;
}

void closeGpioDev(void) {
	if (mFD <= 0) {
		LOGE("don't open gpio dev!");
		return;
	}
	close(mFD);
}

int setGpioMode(unsigned int pin, unsigned int mode) {
	int ret;

	if (mFD <= 0) {
		LOGE("don't open gpio dev!");
		return -1;
	}
	pin |= 0x80000000;
	switch(mode)
	{
	case 0:
		ret = ioctl(mFD, GPIO_IOCTMODE0, pin);
		break;
	case 1:
		ret = ioctl(mFD, GPIO_IOCTMODE1, pin);
		break;
	case 2:
		ret = ioctl(mFD, GPIO_IOCTMODE2, pin);
		break;
	case 3:
		ret = ioctl(mFD, GPIO_IOCTMODE3, pin);
		break;
	default:
		LOGE("mode param error!!");
		ret = -1;
		break;
	}
	return ret;
}

int setGpioDir(unsigned int pin, unsigned int dir) {
	if (mFD <= 0) {
		LOGE("don't open gpio dev!");
		return -1;
	}
	pin |= 0x80000000;
	return ioctl(mFD, (dir?GPIO_IOCSDIROUT:GPIO_IOCSDIRIN), pin);
}

int setGpioPullEnable(unsigned int pin, unsigned int enbale) {
	if (mFD <= 0) {
		LOGE("don't open gpio dev!");
		return -1;
	}
	pin |= 0x80000000;
	return ioctl(mFD, (enbale?GPIO_IOCSPULLENABLE:GPIO_IOCSPULLDISABLE), pin);
}

int setGpioPullSelect(unsigned int pin, unsigned int updown) {
	if (mFD <= 0) {
		LOGE("don't open gpio dev!");
		return -1;
	}
	pin |= 0x80000000;
	return ioctl(mFD, (updown?GPIO_IOCSPULLDOWN:GPIO_IOCSPULLUP), pin);
}

int setGpioOut(unsigned int pin, unsigned int out) {
	if (mFD <= 0) {
		LOGE("don't open gpio dev!");
		return -1;
	}
	pin |= 0x80000000;
	return ioctl(mFD, (out?GPIO_IOCSDATAHIGH:GPIO_IOCSDATALOW), pin);
}

int getGpioIn(unsigned int pin) {
	if (mFD <= 0) {
		LOGE("don't open gpio dev!");
		return -1;
	}
	pin |= 0x80000000;
	return ioctl(mFD, GPIO_IOCQDATAIN, pin);
}
