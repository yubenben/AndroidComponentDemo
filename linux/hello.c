/*************************************************************************
	> File Name: hello.c
	> Author: 
	> Mail: 
	> Created Time: 2016年07月14日 星期四 15时59分28秒
 ************************************************************************/

#include<stdio.h>
#include "md5.h"

#define TAG "application"
#define LOGI(...) ((void)printf( __VA_ARGS__))
#define LOGW(...) ((void)printf( __VA_ARGS__))
#define LOGD(...) ((void)printf( __VA_ARGS__))

void ByteToHexStr(const unsigned char* source, char* dest, int sourceLen);

int main(int argc, char *argv[])
{
    int count = 0;
    for(count = 1; count < argc; count++) {  
           printf("%d: %s\n", count, argv[count]);  
    }  
    if (argc < 2) {
        printf("error:  ");
    }

    printf("file  : %s\n", argv[1]);

    printf("md5=%s \n", MDFile(argv[1]));
    return 0;

}

void ByteToHexStr(const unsigned char* source, char* dest, int sourceLen)
{
    short i;
    unsigned char highByte, lowByte;
    for (i = 0; i < sourceLen; i++)
    {
        highByte = source[i] >> 4;
        lowByte = source[i] & 0x0f;
        highByte += 0x30;
        if (highByte > 0x39)
            dest[i * 2] = highByte + 0x07;
        else
            dest[i * 2] = highByte;
        lowByte += 0x30;
        if (lowByte > 0x39)
            dest[i * 2 + 1] = lowByte + 0x07;
        else
            dest[i * 2 + 1] = lowByte;
    }
    return;
}
