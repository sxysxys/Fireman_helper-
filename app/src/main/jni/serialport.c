/*
 * serialport.c
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
#include "serialport.h"
#include "log.h"
#include "pub_def.h"

static int comSetup(int fd, int nSpeed, int nBits, char nEvent, int nStop) {
    struct termios newtio,oldtio;

    if (tcgetattr(fd,&oldtio) != 0)
    {
        LOGE("tcgetattr erro!");
        return -1;
    }
    memset(&newtio, 0, sizeof(newtio));
    //设置字符大小
    newtio.c_cflag |= CLOCAL | CREAD;
    newtio.c_cflag &= ~CSIZE;
    //设置停止位
    switch(nBits)
    {
    case 7:
        newtio.c_cflag |= CS7;
        break;
    case 8:
        newtio.c_cflag |= CS8;
        break;
    }
    //设置奇偶校验位
    switch(nEvent)
    {
    case 'O'://奇数
        newtio.c_cflag |= PARENB;
        newtio.c_cflag |= PARODD;
        newtio.c_iflag |= (INPCK | ISTRIP);
        break;
    case 'E'://偶数
        newtio.c_iflag |= (INPCK | ISTRIP);
        newtio.c_cflag |= PARENB;
        newtio.c_cflag &= ~PARODD;
        break;
    case 'N'://无奇偶校验位
    default:
        newtio.c_cflag &= ~PARENB;
        break;
    }
    //设置波特率
    switch(nSpeed)
    {
    case 1200:
    	cfsetispeed(&newtio, B1200);
    	cfsetospeed(&newtio, B1200);
    	break;
    case 2400:
        cfsetispeed(&newtio, B2400);
        cfsetospeed(&newtio, B2400);
        break;
    case 4800:
        cfsetispeed(&newtio, B4800);
        cfsetospeed(&newtio, B4800);
        break;
    case 9600:
        cfsetispeed(&newtio, B9600);
        cfsetospeed(&newtio, B9600);
        break;
    case 19200:
        cfsetispeed(&newtio, B19200);
        cfsetospeed(&newtio, B19200);
        break;
    case 38400:
		cfsetispeed(&newtio, B38400);
		cfsetospeed(&newtio, B38400);
		break;
    case 57600:
		cfsetispeed(&newtio, B57600);
		cfsetospeed(&newtio, B57600);
		break;
    case 115200:
        cfsetispeed(&newtio, B115200);
        cfsetospeed(&newtio, B115200);
        break;
    case 460800:
        cfsetispeed(&newtio, B460800);
        cfsetospeed(&newtio, B460800);
        break;
    case 921600:
        cfsetispeed(&newtio, B921600);
        cfsetospeed(&newtio, B921600);
        break;
    default:
        cfsetispeed(&newtio, B9600);
        cfsetospeed(&newtio, B9600);
        break;
    }
    //设置停止位
    if (nStop == 1)
    {
        newtio.c_cflag &= ~CSTOPB;
    }
    else if (nStop == 2)
    {
        newtio.c_cflag |= CSTOPB;
    }
    //设置等待时间和最小字接收字符
    newtio.c_cc[VTIME] = 0;
    newtio.c_cc[VMIN] = 0;
    //处理未接收字符
    tcflush(fd,TCIFLUSH);
    //激活新配置
    if ((tcsetattr(fd,TCSANOW,&newtio)) != 0)
    {
        return -1;
    }
    return 0;
}

int openCom(char* port, int speed, int bits, char event, int stop) {
	return openComEx(port, speed, bits, event, stop, O_RDWR);
}

int openComEx(char* port, int speed, int bits, char event, int stop, int flags) {
#ifdef SERIALPORT_LOGD
	LOGD("port:%s(%d,%d-%c-%d), flags:%d, readBufSize:%d", port, speed, bits, event, stop, flags);
#endif
	int fd = open(port, flags);
	if (-1 == fd)
	{
		LOGE("Cant't open com port:%s",port);
		return -1;
	}
	//测试是否为终端设备
	if (isatty(fd) == 0)
	{
		LOGE("standard input is not a terminal device\n");
		close(fd);
		return -1;
	}
	if (comSetup(fd, speed, bits, event, stop) != 0)
	{
		LOGE("com setting error!");
		close(fd);
		return -1;
	}

#ifdef SERIALPORT_LOGD
	LOGD("com[fd:%d] setting success  11111.", fd);
#endif
	return fd;
}


void closeCom(int fd) {
#ifdef SERIALPORT_LOGD
	LOGD("close com. fd:%d",fd);
#endif
	close(fd);
}


int readCom(int fd, unsigned char* buf, int size) {
	if (fd < 0) {
		LOGE("fd < 0, error!!");
		return -1;
	}
	int len = read(fd,buf,size);
	//LOGD("read com. fd:%d,size:%d, ret:%d",fd,size,len);
	return len;
}

int readComEx(int fd, unsigned char* buf, int size, int sec, int usec){
	int ret = 0;
	 fd_set inset;
	struct timeval tv_timeout;

	if (fd < 0) {
		LOGE("fd < 0, error!!");
		return -1;
	}
	FD_ZERO(&inset);
	FD_SET(fd, &inset);
	tv_timeout.tv_sec  =sec;
	tv_timeout.tv_usec = usec;
#ifdef SERIALPORT_LOGV
	LOGV("readComEx->select start, sec:%d, usec:%d",sec, usec);
#endif
	if (sec == 0 && usec == 0){
		ret = select(fd+1, &inset, NULL, NULL, NULL);
	}else{
		ret = select(fd+1, &inset, NULL, NULL, &tv_timeout);
	}
#ifdef SERIALPORT_LOGV
	LOGV("readComEx->select end, return:%d",ret);
#endif
	if (ret == 0) {
		return 0;
	}else if (ret < 0){
		LOGE("readComEx->select error!");
		return ret;
	}

	ret = read(fd,buf,size);
#ifdef SERIALPORT_LOGV
	/*LOGV("read:%02x %02x %02x %02x %02x %02x %02x %02x %02x %02x %02x %02x %02x %02x %02x %02x ",
			buf[0],buf[1],buf[2],buf[3],buf[4],buf[5],buf[6],buf[7],buf[8],
			buf[9],buf[10],buf[11],buf[12],buf[13],buf[14],buf[15]);*/
	LOGV("readComEx->read->fd:%d,size:%d,readed:%d",fd,size,ret);
#endif
	return ret;
}

int writeCom(int fd, unsigned char* data, int size) {

	LOGD("writeCom");
	if (fd < 0) {
		LOGE("writeCom->fd < 0, error!!");
		return -1;
	}

	LOGD("writeCom111");
	if (data == NULL || size <= 0){
		LOGE("writeCom->data is null or size <= 0");
	}
	//  获取字节长度
	LOGD("writeCom22");
	int ret = write(fd, data, size);
	LOGD("writeCom33 ret =  %d~~size = %d",ret,size);
#ifdef SERIALPORT_LOGV
	LOGV("writeCom->fd:%d,size:%d,wrtie:%d",fd,size,ret);
#endif
	return ret;
}
