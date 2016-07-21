/*************************************************************************
	> File Name: jnitest.c
	> Author: 
	> Mail: 
	> Created Time: 2016年07月13日 星期三 18时07分44秒
 ************************************************************************/

#include "com_ran_ben_androidcomponentdemo_utils_NdkJniUtils.h"

#define TAG "application"
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__))
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__))


JNIEXPORT jint JNICALL Java_com_ran_ben_androidcomponentdemo_utils_NdkJniUtils_checkDexMD5
(JNIEnv* env, jclass tis, jstring japkpath, jstring jfilename, jstring jcachedir)
{
   int i = 0;
   jboolean iscopy;
   const char *apkpath = (*env)->GetStringUTFChars(env, japkpath, &iscopy);
   struct zip* apkArchive = zip_open(apkpath, 0, NULL);;
   (*env)->ReleaseStringUTFChars(env, japkpath, apkpath);

   struct zip_stat fstat;
   zip_stat_init(&fstat);

   const char *filename = (*env)->GetStringUTFChars(env, jfilename, &iscopy);
   struct zip_file* file = zip_fopen(apkArchive, filename, 0);

   if (!file) {
    LOGE("Error opening %s from APK", filename);
    return -1;
   }

   zip_stat(apkArchive, filename, 0, &fstat);
   (*env)->ReleaseStringUTFChars(env, jfilename, filename);

   char *cachefile = (*env)->GetStringUTFChars(env, jcachedir, &iscopy);
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
           LOGE("read file error/n");
           exit(102);
       }
       write(fd, buf, len);
       sum += len;
   }
   close(fd);
   zip_fclose(file);
   zip_close(apkArchive);
   LOGD("%s md5 = %s \n", cachefile, MDFile(cachefile));
   int result = -1;
   result = strcasecmp("3a3173c51c90b4dc53710405c23d254e", MDFile(cachefile));
   (*env)->ReleaseStringUTFChars(env, jcachedir, cachefile);
   return result;
}


JNIEXPORT jint JNICALL Java_com_ran_ben_androidcomponentdemo_utils_NdkJniUtils_checkSign
  (JNIEnv *env, jclass clz, jobject obj){
      if(obj == NULL){
         return -1;
      }

      jmethodID packageManagerMethodID = (*env)->GetMethodID(env, (*env)->GetObjectClass(env, obj),
                                                          "getPackageManager", "()Landroid/content/pm/PackageManager;");
      if(packageManagerMethodID == NULL){
         return -2;
      }
      jobject packageManager = (*env)->CallObjectMethod(env, obj, packageManagerMethodID);

      jmethodID packageNameMethodID = (*env)->GetMethodID(env, (*env)->GetObjectClass(env, obj),
                                                       "getPackageName", "()Ljava/lang/String;");
      if(packageNameMethodID == NULL){
         return -3;
      }
      jobject packageName = (*env)->CallObjectMethod(env, obj, packageNameMethodID);

      if(packageManager == NULL){
         return -4;
      }
      jmethodID packageInfoMethodID = (*env)->GetMethodID(env, (*env)->GetObjectClass(env, packageManager),
                                                       "getPackageInfo", "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
      if(packageName == NULL || packageInfoMethodID == NULL){
         return -5;
      }
      jobject packageInfo = (*env)->CallObjectMethod(env, packageManager, packageInfoMethodID, packageName, 64);

      if(packageInfo == NULL){
         return -6;
      }
      jfieldID sigMethodID = (*env)->GetFieldID(env, (*env)->GetObjectClass(env, packageInfo),
                                             "signatures", "[Landroid/content/pm/Signature;");
      if(sigMethodID == NULL){
         return -6;
      }
      jobjectArray sig = (jobjectArray)((*env)->GetObjectField(env, packageInfo, sigMethodID));
      if(sig == NULL){
         return -7;
      }

      int len = (*env)->GetArrayLength(env, sig);
      if(len <= 0){
         return -8;
      }

      jobject firstSig = (*env)->GetObjectArrayElement(env, sig, 0);

      if(firstSig == NULL){
         return -9;
      }

      jmethodID toCharStringMethodID = (*env)->GetMethodID(env, (*env)->GetObjectClass(env, firstSig),
                                                        "toCharsString", "()Ljava/lang/String;");
      jstring sigString = (jstring)(*env)->CallObjectMethod(env, firstSig, toCharStringMethodID);

      if(sigString == NULL){
         return -10;
      }

      const char rightSig[14] = {'3','0','8','2','0','2','1','d','3','0','8','2','0','1'};
      const char *data = (*env)->GetStringUTFChars(env, sigString, 0);
      if(data == NULL){
         return -11;
      }
      LOGD("String: %s", data);

      int index = 0;
      for(;index < 14;index++){
        if(data[index] != rightSig[index]){
         return -12;
        }
      };

      return 0;
  }




