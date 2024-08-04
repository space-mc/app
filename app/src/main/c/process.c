#include <sys/sysinfo.h>
#include <time.h>
#include <jni.h>
#include <bits/sysconf.h>
#include "process_info.h"

void ThrowException(JNIEnv *env, const char *message);

jint GetProcessPid(JNIEnv *env, jobject thiz);

jclass Process$class;
jfieldID Process$pid;

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *unused) {
    JNIEnv *env = NULL;

    (*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_6);

    Process$class = (*env)->FindClass(env, "org/space/pmmp/server/process/Process");

    if (!Process$class) {
        ThrowException(env, "Could not find org/space/pmmp/server/process/Process class!");

        return -1;
    }

    Process$pid = (*env)->GetFieldID(env, Process$class, "pid", "I");

    if (!Process$pid) {
        ThrowException(env, "Could not find int Process::pid!");

        return -1;
    }

    return JNI_VERSION_1_6;
}

JNIEXPORT jlong JNICALL
Java_org_space_pmmp_server_process_Process_cpuTime(JNIEnv *env, jobject thiz) {
    static long clk_tck = 0;

    clock_t cpuTimeInClock;
    struct procstat_t stat;
    jint pid = GetProcessPid(env, thiz);

    if (GetProcessStatus(pid, &stat) != 0) {
        return -1;
    }

    if (clk_tck == 0) {
        clk_tck = sysconf(_SC_CLK_TCK);
    }

    cpuTimeInClock =
            stat.user_time + stat.system_time + stat.child_user_time + stat.child_system_time;

    // Return in milliseconds, as we're using System.currentTimeMillis() in Kotlin side to determine
    // the elapsed time
    return (cpuTimeInClock * 1000) / clk_tck;
}

JNIEXPORT jlong JNICALL
Java_org_space_pmmp_server_process_Process_memoryUsage(JNIEnv *env, jobject thiz) {
    struct procstatm_t statm;
    jint pid = GetProcessPid(env, thiz);

    if (GetProcessMemory(pid, &statm) != 0) {
        return -1;
    }

    // Return in MiB
    return (statm.resident * PAGE_SIZE) / 1024 / 1024;
}

JNIEXPORT jint JNICALL
Java_org_space_pmmp_server_process_Process_threadCount(JNIEnv *env, jobject thiz) {
    struct procstat_t stat;
    jint pid = GetProcessPid(env, thiz);

    if (GetProcessStatus(pid, &stat) != 0) {
        return 0;
    }

    return stat.num_threads;
}

inline jint GetProcessPid(JNIEnv *env, jobject thiz) {
    return (*env)->GetIntField(env, thiz, Process$pid);
}

void ThrowException(JNIEnv *env, const char *message) {
    jclass Exception$class = (*env)->FindClass(env, "java/lang/Exception");

    (*env)->ThrowNew(env, Exception$class, message);
}