LOCAL_PATH := $(call my-dir)
#########################################
include $(CLEAR_VARS)
LOCAL_MODULE := application
LOCAL_SRC_FILES := jnitest.c md5.c
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
include $(BUILD_SHARED_LIBRARY)
