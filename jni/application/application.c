/*************************************************************************
	> File Name: jnitest.c
	> Author: 
	> Mail: 
	> Created Time: 2016年07月13日 星期三 18时07分44秒
 ************************************************************************/

#include<stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <stdlib.h>
#include <fcntl.h>
#include <android/log.h>
#include <../libzip/zip.h>
#include "md5.h"

#include "com_ran_ben_androidcomponentdemo_utils_NdkJniUtils.h"

#define TAG "application"
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__))
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__))

// 签名文件的MD5
//unsigned const  char* MD5 = "111111111111111111";

void ByteToHexStr(const unsigned char* source, char* dest, int sourceLen);
int getSign(JNIEnv *env, jobject context);
/*
 *  * Class:     io_github_yanbober_ndkapplication_NdkJniUtils
 *   * Method:    getCLanguageString
 *    * Signature: ()Ljava/lang/String;
 *     */
 JNIEXPORT jint JNICALL Java_com_ran_ben_androidcomponentdemo_utils_NdkJniUtils_checkDexMD5
         (JNIEnv* env, jobject thiz, jobject application, jstring file) {

    //return  getSign(env, application);
    return checkmd5(env, file);
 }


int checkmd5(JNIEnv* env, jstring file){
   const char* filename;
   filename = (*env)->GetStringUTFChars(env, file, 0);
   if(filename == NULL) {
       return -1; /* OutOfMemoryError already thrown */
   }
   LOGD("filename=%s \n", filename);
   LOGD("md5 = %s \n", MDFile(filename));
   return 0;
}

void  Java_com_ran_ben_androidcomponentdemo_utils_NdkJniUtils_readFromAssetsLibzip
(JNIEnv* env,jclass tis,jstring assetpath,jstring filename, jstring cachedir)
{
   int i=0;
   jboolean iscopy;
   const char *mpath = (*env)->GetStringUTFChars(env, assetpath, &iscopy);
   struct zip* apkArchive=zip_open(mpath, 0, NULL);;
   (*env)->ReleaseStringUTFChars(env, filename, mpath);

   struct zip_stat fstat;
   zip_stat_init(&fstat);

   int numFiles = zip_get_num_files(apkArchive);
   LOGI("File numFiles %i \n",numFiles);
   for (i=0; i<numFiles; i++) {
     const char* name = zip_get_name(apkArchive, i, 0);

     if (name == NULL) {
      LOGE("Error reading zip file name at index %i : %s", zip_strerror(apkArchive));
      return;
    }

    zip_stat(apkArchive,name,0,&fstat);
    //LOGI("File %i:%s Size1: %u Size2: %u", i,fstat.name,fstat.size ,fstat.comp_size)  ;
   }

   const char *fname = (*env)->GetStringUTFChars(env, filename, &iscopy);
   struct zip_file* file = zip_fopen(apkArchive, fname, 0);

   if (!file) {
    LOGE("Error opening %s from APK", fname);
    return;
   }

   zip_stat(apkArchive,fname,0,&fstat);
   (*env)->ReleaseStringUTFChars(env, filename, fname);
   //char *buffer=(char *)malloc(fstat.size+1);
   //buffer[fstat.size]=0;
   //int numBytesRead =  zip_fread(file, buffer,fstat.size);;
   //LOGI(": %s\n",buffer);
   //LOGD("dex md5 = %s \n", MDString(buffer));
   //free(buffer);

   const char *cachefile = (*env)->GetStringUTFChars(env, cachedir, &iscopy);
   int fd = open(cachefile, O_RDWR | O_TRUNC | O_CREAT, 0644);
   if (fd < 0) {
       LOGE("create file %s error/n", cachefile);
       exit(101);
   }

   unsigned int  sum = 0;
   int len;
   char buf[100];
   while (sum != fstat.size) {
       len = zip_fread(file, buf, 100);
       if (len < 0) {
           LOGE("boese, boese/n");
           exit(102);
       }
       write(fd, buf, len);
       sum += len;
   }
   close(fd);
   zip_fclose(file);
   zip_close(apkArchive);
   LOGD("%s md5 = %s \n", cachefile, MDFile(cachefile));
}


int getSign(JNIEnv *env, jobject context) {
    //Context的类
    jclass context_clazz = (*env)->GetObjectClass(env, context);
    // 得到 getPackageManager 方法的 ID
    jmethodID methodID_getPackageManager = (*env)->GetMethodID(env, context_clazz,
                                                               "getPackageManager", "()Landroid/content/pm/PackageManager;");

    // 获得PackageManager对象
    jobject packageManager = (*env)->CallObjectMethod(env, context,
                                                      methodID_getPackageManager);
    // 获得 PackageManager 类
    jclass pm_clazz = (*env)->GetObjectClass(env, packageManager);
    // 得到 getPackageInfo 方法的 ID
    jmethodID methodID_pm = (*env)->GetMethodID(env, pm_clazz, "getPackageInfo",
                                                "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    //
    // 得到 getPackageName 方法的 ID
    jmethodID methodID_pack = (*env)->GetMethodID(env, context_clazz,
                                                  "getPackageName", "()Ljava/lang/String;");

    // 获得当前应用的包名
    jstring application_package = (*env)->CallObjectMethod(env, context,
                                                           methodID_pack);
    const char *str = (*env)->GetStringUTFChars(env, application_package, 0);
    LOGD("JNI", "packageName: %s\n", str);

    // 获得PackageInfo
    jobject packageInfo = (*env)->CallObjectMethod(env, packageManager,
                                                   methodID_pm, application_package, 64);

    jclass packageinfo_clazz = (*env)->GetObjectClass(env, packageInfo);
    jfieldID fieldID_signatures = (*env)->GetFieldID(env, packageinfo_clazz,
                                                     "signatures", "[Landroid/content/pm/Signature;");
    jobjectArray signature_arr = (jobjectArray)(*env)->GetObjectField(env,
                                                                      packageInfo, fieldID_signatures);
    // Signature数组中取出第一个元素
    jobject signature = (*env)->GetObjectArrayElement(env, signature_arr, 0);
    // 读signature的hashcode
    jclass signature_clazz = (*env)->GetObjectClass(env, signature);
    jmethodID methodID_hashcode = (*env)->GetMethodID(env, signature_clazz,
                                                      "hashCode", "()I");
    jint hashCode = (*env)->CallIntMethod(env, signature, methodID_hashcode);
    // 读signature的byte array
    jmethodID methodID_toByteArray = (*env)->GetMethodID(env, signature_clazz,
                                                         "toByteArray", "()[B");
    jbyteArray byteArray = (*env)->CallObjectMethod(env, signature, methodID_toByteArray);
    jsize len = (*env)->GetArrayLength(env, byteArray); // 获取长度
    jbyte* bap = (*env)->GetByteArrayElements(env, byteArray, JNI_FALSE); // jbyteArray转为jbyte*
    char* rtn = NULL;
    int i=0;
    int result = -1;
    if(len > 0)
    {
        rtn = (char*)malloc(len+1); // "\0"
        memcpy(rtn, bap, len);
        rtn[len]=0;
        // 注：这里也可以通过回调java的MessageDigest类方法获取MD5
        unsigned char decrypt[16];
        MD5_CTX mdContext;
        MD5Init(&mdContext); //初始化
        MD5Update(&mdContext, rtn, len);
        MD5Final(&mdContext, decrypt);
        //char* char_result = (char*) malloc(16*2+1);
        //ByteToHexStr(decrypt, char_result, 16);
        //*(char_result+16*2) = '\0';// 在末尾补\0
        //LOGI("result:%s\n", char_result);
        //result = strcasecmp("111", char_result);
        //free(char_result);
    }
    LOGI("result hashCode = %d", hashCode);
    (*env)->ReleaseByteArrayElements(env, byteArray, bap, 0);  //释放掉
    return result;

}




