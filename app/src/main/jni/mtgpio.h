/*
 * mtgpio.h
 *
 *  Created on: 2014-11-6
 *      Author: kongli
 */

#ifndef MTGPIO_H_
#define MTGPIO_H_

int openGpioDev(void);
void closeGpioDev(void);
int setGpioMode(int pin, int mode);
int setGpioDir(int pin, int dir);
int setGpioPullEnable(int pin, int enbale);
int setGpioPullSelect(int pin, int updown);
int setGpioOut(int pin, int out);
int getGpioIn(int pin);

#endif /* MTGPIO_H_ */
