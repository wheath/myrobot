adb shell ps | grep com.myrobot| awk '{print }' | xargs adb shell kill
