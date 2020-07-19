#include <jni.h>
#include <string>

std::string hello = "Hello from C++";

extern "C" JNIEXPORT jstring JNICALL
Java_br_net_easify_tracker_MainActivity_stringFromJNI(JNIEnv* env, jobject) {

    return env->NewStringUTF(hello.c_str());
}
