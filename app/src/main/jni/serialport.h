/*
 * serialport.h
 *
 *  Created on: 2014-11-6
 *      Author: kongli
 */

#ifndef SERIALPORT_H_
#define SERIALPORT_H_

int openCom(char* port, int speed, int bits, char event, int stop);
int openComEx(char* port, int speed, int bits, char event, int stop, int flags);
void closeCom(int fd);
int readCom(int fd, unsigned char* buf, int size);
int writeCom(int fd, unsigned char* data, int size);

#endif /* SERIALPORT_H_ */
