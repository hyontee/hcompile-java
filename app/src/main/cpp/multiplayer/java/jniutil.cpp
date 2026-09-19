#include "jniutil.h"
#include "game/game.h"
#include "net/netgame.h"
#include <cmath>

extern CGame *pGame;
extern CNetGame* pNetGame;

JNIEnv *CJavaWrapper::GetEnv() {
    JNIEnv *env = nullptr;
    int getEnvStat = javaVM->GetEnv((void **) &env, JNI_VERSION_1_6);

    if (getEnvStat == JNI_EDETACHED) {
        FLog("GetEnv: not attached");
        if (javaVM->AttachCurrentThread(&env, NULL) != 0) {
            FLog("Failed to attach");
            return nullptr;
        }
    }
    if (getEnvStat == JNI_EVERSION) {
        FLog("GetEnv: version not supported");
        return nullptr;
    }

    if (getEnvStat == JNI_ERR) {
        FLog("GetEnv: JNI_ERR");
        return nullptr;
    }

    return env;
}

namespace {
    jstring MakeJString(JNIEnv* env, const char* text)
    {
        if (!env) {
            return nullptr;
        }

        const char* safeText = text ? text : "";

        jclass strClass = env->FindClass("java/lang/String");
        if (!strClass) {
            return nullptr;
        }
        jmethodID ctorID = env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
        if (!ctorID) {
            env->DeleteLocalRef(strClass);
            return nullptr;
        }

        jstring encoding = env->NewStringUTF("UTF-8");
        jbyteArray bytes = env->NewByteArray(strlen(safeText));
        env->SetByteArrayRegion(bytes, 0, strlen(safeText), (jbyte*)safeText);

        jstring result = (jstring)env->NewObject(strClass, ctorID, bytes, encoding);

        env->DeleteLocalRef(bytes);
        env->DeleteLocalRef(encoding);
        env->DeleteLocalRef(strClass);

        return result;
    }

    void NormalizeChatCoords(float& x, float& y, float& width, float& height)
    {
        if (x < 10.0f && y < 10.0f && width < 10.0f && height < 10.0f) {
            float screenWidth = 1920.0f;
            float screenHeight = 1080.0f;

            x = x * screenWidth;
            y = y * screenHeight;
            width = width * screenWidth;
            height = height * screenHeight;
        }

        if (width < 100.0f) width = 100.0f;
        if (height < 50.0f) height = 50.0f;

        if (x + width > 1920.0f) {
            x = 1920.0f - width - 10.0f;
        }
        if (y + height > 1080.0f) {
            y = 1080.0f - height - 10.0f;
        }

        if (x < 10.0f) x = 10.0f;
        if (y < 10.0f) y = 10.0f;
    }
} // namespace

CJavaWrapper::CJavaWrapper(JNIEnv *env, jobject activity)
{
    this->activity = env->NewGlobalRef(activity);

    jclass clas = env->GetObjectClass(activity);
    if(!clas)
    {
        FLog("no clas");
        return;
    }

    s_showTab = env->GetMethodID(clas, "showTab", "()V");
    s_hideTab = env->GetMethodID(clas, "hideTab", "()V");
    s_clearTab = env->GetMethodID(clas, "clearTab", "()V");
    s_commitTab = env->GetMethodID(clas, "commitTab", "()V");
    s_setTab = env->GetMethodID(clas, "setTab", "(ILjava/lang/String;III)V");

    s_showLoadingScreen = env->GetMethodID(clas, "showLoadingScreen", "()V");
    s_hideLoadingScreen = env->GetMethodID(clas, "hideLoadingScreen", "()V");

    s_setPauseState = env->GetMethodID(clas, "setPauseState", "(Z)V");

    s_ShowDialog = env->GetMethodID(clas, "showDialog", "(II[B[B[B[B)V");

    s_showInputLayout = env->GetMethodID(clas, "showKeyboard", "()V");
    s_hideInputLayout = env->GetMethodID(clas, "hideKeyboard", "()V");

    s_exitGame = env->GetMethodID(clas, "exitGame", "()V");

    s_showEditObject = env->GetMethodID(clas, "showEditObject", "()V");
    s_hideEditObject = env->GetMethodID(clas, "hideEditObject", "()V");

    s_showSpawn = env->GetMethodID(clas, "ShowSpawn", "(II)V");
    s_showAZS = env->GetMethodID(clas, "ShowAZS", "(IIFII)V");
    s_showGPS = env->GetMethodID(clas, "ShowGPS", "(I)V");
    s_showGreenZone = env->GetMethodID(clas, "ShowGreenZone", "(I)V");
    s_showNumbers = env->GetMethodID(clas, "ShowNumbers", "(I)V");
    s_buyNumberResult = env->GetMethodID(clas, "BuyNumberResult", "(II)V");

    s_showNotification = env->GetMethodID(clas, "showNotification", "(ILjava/lang/String;ILjava/lang/String;Ljava/lang/String;)V");

    env->DeleteLocalRef(clas);
}

void CJavaWrapper::ShowKeyboard()
{
    JNIEnv* p;
    javaVM->GetEnv((void**)&p, JNI_VERSION_1_6);
    p->CallVoidMethod(activity, s_showInputLayout);
    EXCEPTION_CHECK(p);
}

void CJavaWrapper::HideKeyboard()
{
    JNIEnv* p;
    javaVM->GetEnv((void**)&p, JNI_VERSION_1_6);
    p->CallVoidMethod(activity, s_hideInputLayout);
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

void CJavaWrapper::SetPauseState(bool pause)
{
    JNIEnv* p;
    javaVM->GetEnv((void**)&p, JNI_VERSION_1_6);
    p->CallVoidMethod(activity, s_setPauseState, pause);
    EXCEPTION_CHECK(p);
}

void CJavaWrapper::SetTab(int id, const char* name, int level, int ping, uint32_t color)
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

    jbyteArray bytes = global_env->NewByteArray(strlen(name));
    global_env->SetByteArrayRegion(bytes, 0, strlen(name), (jbyte*)name);
    jstring str1 = (jstring)global_env->NewObject(strClass, ctorID, bytes, encoding);

    global_env->CallVoidMethod(activity, s_setTab, id, str1, level, ping, static_cast<jint>(color));

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

void CJavaWrapper::CommitTab()
{
    JNIEnv* p;
    javaVM->GetEnv((void**)&p, JNI_VERSION_1_6);
    p->CallVoidMethod(activity, s_commitTab);
    EXCEPTION_CHECK(p);
}

void CJavaWrapper::ShowDialog(int dialogStyle, int dialogID, char* title, char* text, char* button1, char* button2)
{
    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        FLog("No env");
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

void CJavaWrapper::exitGame() {
    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        FLog("No env");
        return;
    }

    env->CallVoidMethod(this->activity, this->s_exitGame);
}

void CJavaWrapper::ShowEditObject() {
    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        FLog("No env");
        return;
    }

    env->CallVoidMethod(this->activity, this->s_showEditObject);
}

void CJavaWrapper::HideEditObject() {
    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        FLog("No env");
        return;
    }

    env->CallVoidMethod(this->activity, this->s_hideEditObject);
}

extern "C" JNIEXPORT void JNICALL Java_com_nvidia_devtech_NvEventQueueActivity_sendCommand(JNIEnv* pEnv, jobject thiz, jbyteArray str)
{
    jboolean isCopy = true;

    jbyte* pMsg = pEnv->GetByteArrayElements(str, &isCopy);
    jsize length = pEnv->GetArrayLength(str);

    std::string szStr((char*)pMsg, length);

    if(pNetGame) {
        pNetGame->SendChatCommand((char*)szStr.c_str());
    }

    pEnv->ReleaseByteArrayElements(str, pMsg, JNI_ABORT);
}

void CJavaWrapper::ShowNotification(int type, char* text, int duration, char* actionforBtn, char* textBtn)
{
    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        FLog("No env");
        return;
    }

    jclass strClass = env->FindClass("java/lang/String");
    jmethodID ctorID = env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    jstring encoding = env->NewStringUTF("UTF-8");

    jbyteArray bytes = env->NewByteArray(strlen(text));
    env->SetByteArrayRegion(bytes, 0, strlen(text), (jbyte*)text);
    jstring jtext = (jstring) env->NewObject(strClass, ctorID, bytes, encoding);

    bytes = env->NewByteArray(strlen(actionforBtn));
    env->SetByteArrayRegion(bytes, 0, strlen(actionforBtn), (jbyte*)actionforBtn);
    jstring jactionforBtn = (jstring) env->NewObject(strClass, ctorID, bytes, encoding);

    bytes = env->NewByteArray(strlen(textBtn));
    env->SetByteArrayRegion(bytes, 0, strlen(textBtn), (jbyte*)textBtn);
    jstring jtextBtn = (jstring) env->NewObject(strClass, ctorID, bytes, encoding);

    env->CallVoidMethod(this->activity, this->s_showNotification, type, jtext, duration, jactionforBtn, jtextBtn);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_nvidia_devtech_NvEventQueueActivity_sendClick(JNIEnv *env, jobject thiz, jbyteArray str) {
    jboolean isCopy = true;

    jbyte* pMsg = env->GetByteArrayElements(str, &isCopy);
    jsize length = env->GetArrayLength(str);

    std::string szStr((char*)pMsg, length);

    if(pNetGame) {
        pNetGame->SendChatCommand((char*)szStr.c_str());
    }

    env->ReleaseByteArrayElements(str, pMsg, JNI_ABORT);
}

void CJavaWrapper::ShowSpawn(int home, int fraction)
{
    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        FLog("No env");
        return;
    }

    env->CallVoidMethod(this->activity, this->s_showSpawn, (jint)home, (jint)fraction);
}

extern "C"
JNIEXPORT void JNICALL Java_com_brilliant_game_core_Samp_sendSpawnClick(JNIEnv* pEnv, jobject thiz, jint id)
{
    if (!pNetGame) return;

    FLog("sendSpawnClick CALLED: id=%d", id);

    RakNet::BitStream bsSend;
    bsSend.Write((uint8_t)242);
    bsSend.Write((uint32_t)0x42);
    bsSend.Write((uint8_t)0);
    bsSend.Write((uint8_t)id);

    pNetGame->GetRakClient()->Send(&bsSend, SYSTEM_PRIORITY, RELIABLE_SEQUENCED, 0);
}

void CJavaWrapper::ShowAZS(int fuelId, int maxFuel, float currentFuel, int price, int balance)
{
    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        FLog("No env");
        return;
    }

    env->CallVoidMethod(this->activity, this->s_showAZS, (jint)fuelId, (jint)maxFuel, (jfloat)currentFuel, (jint)price, (jint)balance);
}

extern "C"
JNIEXPORT void JNICALL Java_com_brilliant_game_core_Samp_refuelTheCar(JNIEnv* pEnv, jobject thiz, jint fuel, jint price)
{
    if (!pNetGame)
    {
        FLog("refuelTheCar: pNetGame == null, packet NOT sent");
        return;
    }

    FLog("refuelTheCar CALLED: fuel=%d price=%d", fuel, price);

    RakNet::BitStream bsSend;
    bsSend.Write((uint8_t)242);
    bsSend.Write((uint32_t)0x41);
    bsSend.Write((uint16_t)fuel);
    bsSend.Write((uint16_t)price);

    pNetGame->GetRakClient()->Send(&bsSend, SYSTEM_PRIORITY, RELIABLE_SEQUENCED, 0);
}
void CJavaWrapper::ShowNumbers(int balance)
{
    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        FLog("No env");
        return;
    }

    env->CallVoidMethod(this->activity, this->s_showNumbers, (jint)balance);
}

void CJavaWrapper::BuyNumberResult(int status, int reason)
{
    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        FLog("No env");
        return;
    }

    env->CallVoidMethod(this->activity, this->s_buyNumberResult, (jint)status, (jint)reason);
}

extern "C"
JNIEXPORT void JNICALL Java_com_brilliant_game_core_Samp_sendBuyNumber(JNIEnv* env, jobject thiz, jint country, jint isCustom, jstring number, jstring region)
{
    if (!pNetGame) return;

    const char* uNumber = env->GetStringUTFChars(number, nullptr);
    const char* uRegion = env->GetStringUTFChars(region, nullptr);

    FLog("sendBuyNumber CALLED: country=%d isCustom=%d number=%s region=%s", country, isCustom, uNumber, uRegion);

    RakNet::BitStream bsSend;
    bsSend.Write((uint8_t)242);
    bsSend.Write((uint32_t)0x47);
    bsSend.Write((uint32_t)country);
    bsSend.Write((uint32_t)isCustom);
    bsSend.Write((uint8_t)strlen(uNumber));
    bsSend.Write(uNumber, strlen(uNumber));
    bsSend.Write((uint8_t)strlen(uRegion));
    bsSend.Write(uRegion, strlen(uRegion));

    pNetGame->GetRakClient()->Send(&bsSend, HIGH_PRIORITY, RELIABLE, 0);

    env->ReleaseStringUTFChars(number, uNumber);
    env->ReleaseStringUTFChars(region, uRegion);
}

void CJavaWrapper::ShowGPS(int value)
{
    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        FLog("No env");
        return;
    }

    env->CallVoidMethod(this->activity, this->s_showGPS, (jint)value);
}

void CJavaWrapper::ShowGreenZone(int value)
{
    JNIEnv* env;
    javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);

    if (!env)
    {
        FLog("No env");
        return;
    }

    env->CallVoidMethod(this->activity, this->s_showGreenZone, (jint)value);
}