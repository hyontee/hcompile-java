#include "jniutil.h"

#include "game/game.h"
extern CGame *pGame;

#include "net/netgame.h"
extern CNetGame *pNetGame;

typedef void* (*OSThreadFunction)(void*);
struct ThreadLaunchData {
    void* thread_struct;
    OSThreadFunction func;
    char thread_name[32];
};

void* CJavaWrapper::NVThreadSpawnProc(void* arg) {
    std::unique_ptr<ThreadLaunchData> data(static_cast<ThreadLaunchData*>(arg));

    bool attached = false;
    JNIEnv* env = nullptr;
    jint status = javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (status == JNI_EDETACHED) {
        if (javaVM->AttachCurrentThread(&env, nullptr) == 0) {
            attached = true;
        }
    }

    if (env && data->thread_name[0] != '\0') {
        jclass threadClass = env->FindClass("java/lang/Thread");
        if (threadClass) {
            jmethodID currentThread = env->GetStaticMethodID(threadClass, "currentThread", "()Ljava/lang/Thread;");
            jmethodID setName = env->GetMethodID(threadClass, "setName", "(Ljava/lang/String;)V");

            if (currentThread && setName) {
                jobject threadObj = env->CallStaticObjectMethod(threadClass, currentThread);
                jstring nameStr = env->NewStringUTF(data->thread_name);
                env->CallVoidMethod(threadObj, setName, nameStr);

                env->DeleteLocalRef(nameStr);
                env->DeleteLocalRef(threadObj);
            }
            env->DeleteLocalRef(threadClass);
        }
    }

    void* result = data->func(data->thread_struct);
    if (attached) {
        javaVM->DetachCurrentThread();
    }
    return result;
}

JNIEnv *CJavaWrapper::GetEnv() {
    JNIEnv *env = nullptr;
    int getEnvStat = javaVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    if (getEnvStat == JNI_EDETACHED) {
        Log("GetEnv: not attached");
        if (javaVM->AttachCurrentThread(&env, NULL) != 0) {
            Log("Failed to attach");
            return nullptr;
        }
    }
    if (getEnvStat == JNI_EVERSION) {
        Log("GetEnv: version not supported");
        return nullptr;
    }

    if (getEnvStat == JNI_ERR) {
        Log("GetEnv: JNI_ERR");
        return nullptr;
    }

    return env;
}

CJavaWrapper::CJavaWrapper(JNIEnv *env, jobject activity)
{
    this->activity = env->NewGlobalRef(activity);

    jclass clas = env->GetObjectClass(activity);
    if(!clas)
    {
        Log("no clas");
        return;
    }

    s_showTab = env->GetMethodID(clas, "showTab", "()V");
    s_hideTab = env->GetMethodID(clas, "hideTab", "()V");
    s_clearTab = env->GetMethodID(clas, "clearTab", "()V");
    s_setTab = env->GetMethodID(clas, "setTab", "(ILjava/lang/String;II)V");

    s_showLoadingScreen = env->GetMethodID(clas, "showLoadingScreen", "()V");
    s_hideLoadingScreen = env->GetMethodID(clas, "hideLoadingScreen", "()V");

    s_setPauseState = env->GetMethodID(clas, "setPauseState", "(Z)V");

    s_ShowDialog = env->GetMethodID(clas, "showDialog", "(II[B[B[B[B)V");

    s_exitGame = env->GetMethodID(clas, "exitGame", "()V");

    s_showEditObject = env->GetMethodID(clas, "showEditObject", "()V");
    s_hideEditObject = env->GetMethodID(clas, "hideEditObject", "()V");
    s_toggleCustomButtons = env->GetMethodID(clas, "toggleCustomButtons", "(Z)V");

    s_SetVisibleKeyboardStandard = env->GetMethodID(clas, "SetVisibleKeyboardStandard", "(II)V");

    env->DeleteLocalRef(clas);
}

void CJavaWrapper::ShowDialog(int dialogStyle, int dialogID, char* title, char* text, char* button1, char* button2)
{
    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        Log("No env");
        return;
    }

    std::string sTitle(title);
    std::string sText(text);
    std::string sButton1(button1);
    std::string sButton2(button2);

    jbyteArray jstrTitle = as_byte_array((unsigned char*)sTitle.c_str(), sTitle.length());
    jbyteArray jstrText = as_byte_array((unsigned char*)sText.c_str(), sText.length());
    jbyteArray jstrButton1 = as_byte_array((unsigned char*)sButton1.c_str(), sButton1.length());
    jbyteArray jstrButton2 = as_byte_array((unsigned char*)sButton2.c_str(), sButton2.length());

    env->CallVoidMethod(activity, s_ShowDialog, dialogID, dialogStyle, jstrTitle, jstrText, jstrButton1, jstrButton2);

    EXCEPTION_CHECK(env);
}
void CJavaWrapper::toggleCustomButtons(bool status)
{
    JNIEnv* p;
    javaVM->GetEnv((void**)&p, JNI_VERSION_1_6);
    p->CallVoidMethod(activity, s_toggleCustomButtons, status);
    EXCEPTION_CHECK(p);
}

void CJavaWrapper::ShowLoadingScreen()
{
    JNIEnv* p;
    javaVM->GetEnv((void**)&p, JNI_VERSION_1_6);
    p->CallVoidMethod(activity, s_showLoadingScreen);
    EXCEPTION_CHECK(p);
}

void CJavaWrapper::HideLoadingScreen()
{
    JNIEnv* p;
    javaVM->GetEnv((void**)&p, JNI_VERSION_1_6);
    p->CallVoidMethod(activity, s_hideLoadingScreen);
    EXCEPTION_CHECK(p);
}


void CJavaWrapper::SetPauseState(bool pause) {
    JNIEnv *env = GetEnv();

    if (!env) {
        Log("No env");
        return;
    }
    env->CallVoidMethod(this->activity, this->s_setPauseState, pause);
}
void CJavaWrapper::SetTab(int id, char* names, int score, int pings)
{
    JNIEnv* global_env;
    javaVM->GetEnv((void**)&global_env, JNI_VERSION_1_6);

    if (!global_env)
    {
        LOGI("No env");
        return;
    }

    jclass strClass = global_env->FindClass("java/lang/String");
    jmethodID ctorID = global_env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    jstring encoding = global_env->NewStringUTF("UTF-8");

    jbyteArray bytes = global_env->NewByteArray(strlen(names));
    global_env->SetByteArrayRegion(bytes, 0, strlen(names), (jbyte*)names);
    jstring str1 = (jstring)global_env->NewObject(strClass, ctorID, bytes, encoding);

    global_env->CallVoidMethod(activity, s_setTab, id, str1, score, pings);

    EXCEPTION_CHECK(global_env);
}

void CJavaWrapper::ShowTab()
{
    JNIEnv* p;
    javaVM->GetEnv((void**)&p, JNI_VERSION_1_6);
    p->CallVoidMethod(activity, s_showTab);
    EXCEPTION_CHECK(p);
}

void CJavaWrapper::HideTab()
{
    JNIEnv* p;
    javaVM->GetEnv((void**)&p, JNI_VERSION_1_6);
    p->CallVoidMethod(activity, s_hideTab);
    EXCEPTION_CHECK(p);
}

void CJavaWrapper::ClearTab()
{
    JNIEnv* p;
    javaVM->GetEnv((void**)&p, JNI_VERSION_1_6);
    p->CallVoidMethod(activity, s_clearTab);
    EXCEPTION_CHECK(p);
}

void CJavaWrapper::exitGame() {

    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        Log("No env");
        return;
    }

    env->CallVoidMethod(this->activity, this->s_exitGame);
}

void CJavaWrapper::ShowEditObject() {

    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        Log("No env");
        return;
    }

    env->CallVoidMethod(this->activity, this->s_showEditObject);
}

void CJavaWrapper::HideEditObject() {

    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        Log("No env");
        return;
    }

    env->CallVoidMethod(this->activity, this->s_hideEditObject);
}

void CJavaWrapper::SetVisibleKeyboard(bool active, int type)
{
    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        Log("No env");
        return;
    }

    env->CallVoidMethod(this->activity, this->s_SetVisibleKeyboardStandard, active? 1 : 0, type);
    EXCEPTION_CHECK(env);
}