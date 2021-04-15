/*
 * pub_def.h
 *
 *  Created on: 2014-11-6
 *      Author: kongli
 */

#ifndef PUB_DEF_H_
#define PUB_DEF_H_

typedef unsigned char byte;
typedef unsigned char boolean;

#define TRUE    1
#define FALSE   0


byte GetHighByte(int data);
byte GetLowByte(int data);
byte GetLowWord(int data);
byte GetHighWord(int data);
int makeWord(byte a, byte b);
#endif /* PUB_DEF_H_ */
