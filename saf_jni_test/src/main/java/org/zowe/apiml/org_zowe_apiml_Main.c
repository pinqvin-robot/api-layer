#include "org_zowe_apiml_Main.h"
#include <stdio.h>

JNIEXPORT void JNICALL Java_org_zowe_apiml_Main_sayHello
  (JNIEnv* env, jobject thisObject) {
     printf("Hello, World!");
}

JNIEXPORT jstring JNICALL Java_org_zowe_apiml_Main_getUserId
  (JNIEnv *env, jobject thisObject, jstring certificate) {
  char msg[60] = "userID";
 jstring userId = (*env)->NewStringUTF(env,msg);
 return userId;
  }
