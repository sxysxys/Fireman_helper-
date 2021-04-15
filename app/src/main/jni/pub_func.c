/*
 * pub_func.c
 *
 *  Created on: 2014-11-6
 *      Author: kongli
 */
#include "pub_def.h"
#include "log.h"
// 取高字节
byte GetHighByte(int data)
{
	return (byte)(data >> 8);
}
// 取低字节
byte GetLowByte(int data)
{
	return (byte) (data & 0xff);
}
//取低字
byte GetLowWord(int data)
{
	return ((byte)(((byte)data) & 0xffff));

}
//取高字
byte GetHighWord(int data)
{
	return ((byte)((((byte)data) >> 16) & 0xffff));

}

int makeWord(byte a, byte b)
{
	//UInt16 i = H_bit;
	//i = i << 8;
	//但是你串口一次只能传送8个位，那么就分高字节低字节，一个字节8位，你先把高8位放在一个16位的变量里i = xx，然后执行i<<8；再i |= xx;这就合并了。你的i就是存了对方16位的数据

	return (a &0xff)|( ((b&0xff )<< 8));
}

/*char* byteArrayToHexStr(byte* data, int size){
	if (data == NULL || size <= 0) return "";
	char* out = (char*)malloc(size*3+10);
	bzero(out, size*3+10);
	int i;
	for (i = 0; i < size; i++){
		out[]
	}
}*/
