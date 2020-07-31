###Set up

#### Generate header
cd src/main/java/org/zowe/apiml
javac -h . Main.java

#### Implement C function
create file org_zowe_apiml_Main.c and implement method from header
JNIEXPORT void JNICALL Java_org_zowe_apiml_Main_sayHello
    (JNIEnv* env, jobject thisObject) {
    // impl
    }
    
#### Compile 

MacOS:
 cc -c -fPIC -I/Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/include -I/Library/Java/JavaVirtualMachines/jdk1.8.0_241.jdk/Contents/Home/include/darwin org_zowe_apiml_Main.c -o  org_zowe_apiml_Main.o 
Windows:
gcc -c -I%JAVA_HOME%\include -I%JAVA_HOME%\include\win32 org_zowe_apiml_Main.c -o  org_zowe_apiml_Main.o 
Ubuntu:
gcc -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux org_zowe_apiml_Main.c -o org_zowe_apiml_Main.o
#### Link

MacOS:
 cc -dynamiclib -o libnative.dylib org_zowe_apiml_Main.o -lc

Windows:
gcc -shared -o native.dll org_zowe_apiml_Main.o -Wl,--add-stdcall-alias

Ubuntu:
gcc -shared -fPIC -o libnative.so org_zowe_apiml_Main.o -lc

#### Run
cd ../../..

java -cp . -Djava.library.path=./org/zowe/apiml org.zowe.apiml.Main

#### Tips

After changing java code, regenerate headers.
