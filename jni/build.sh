#!/bin/bash
ndk-build && rm -rvf ../app/jniLibs/  && mv -v ../libs  ../app/jniLibs/ 
