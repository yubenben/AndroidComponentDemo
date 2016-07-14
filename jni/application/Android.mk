LOCAL_PATH := $(call my-dir)
#########################################
include $(CLEAR_VARS)
LOCAL_MODULE := application
LOCAL_SRC_FILES := application.c md5.c
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog


LOCAL_C_INCLUDES := $(LOCAL_PATH)/../libzip/
LOCAL_STATIC_LIBRARIES := libzip

#-Wno-psabi to remove warning about GCC 4.4 va_list warning
LOCAL_CFLAGS := -DANDROID_NDK -Wno-psabi

LOCAL_DEFAULT_CPP_EXTENSION := c

LOCAL_LDLIBS := -lm -llog

LOCAL_LDLIBS+=-L$(SYSROOT)/usr/lib -ldl
LOCAL_LDLIBS+=-L$(SYSROOT)/usr/lib -lz
include $(BUILD_SHARED_LIBRARY)
