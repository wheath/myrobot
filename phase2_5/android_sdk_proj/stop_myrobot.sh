#!/bin/sh
adb shell ps | grep com.myrobot| awk '{print $2}' | xargs adb shell kill
