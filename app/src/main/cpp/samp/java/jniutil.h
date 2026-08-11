#pragma once
#include <jni.h>
#include "game/common.h"
#include "main.h"

#define EXCEPTION_CHECK(env) \
	if ((env)->ExceptionCheck()) \
	{ \
		(env)->ExceptionDescribe(); \
		(env)->ExceptionClear(); \
		return; \
	}
class CJavaWrapper
{
public:
	static JNIEnv* GetEnv();
	static void* NVThreadSpawnProc(void* arg);

	CJavaWrapper(JNIEnv *env, jobject activity);
	~CJavaWrapper() {};

    void SetPauseState(bool pause);
    void ShowLoadingScreen();
	void HideLoadingScreen();
	void ShowTab();
	void HideTab();
	void SetTab(int id, char* names, int score, int pings);
	void ClearTab();

	void exitGame();
	jbyteArray as_byte_array(unsigned char* buf, int len) {
		JNIEnv* p;
		javaVM->GetEnv((void**)&p, JNI_VERSION_1_6);
		jbyteArray array = p->NewByteArray (len);
		p->SetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
		return array;
	}
	void ShowDialog(int dialogStyle, int dialogID, char* title, char* text, char* button1, char* button2);

	void ShowEditObject();
	void HideEditObject();


	jobject activity;
    jmethodID s_setPauseState;
	jmethodID s_loadingScreenCopyright;
	jmethodID s_showLoadingScreen;
	jmethodID s_hideLoadingScreen;
	jmethodID s_ShowDialog;
	jmethodID s_showTab;
	jmethodID s_setTab;
	jmethodID s_clearTab;
	jmethodID s_hideTab;
	jmethodID s_exitGame;
	jmethodID s_showEditObject;
	jmethodID s_hideEditObject;

	jmethodID s_toggleCustomButtons;
	void toggleCustomButtons(bool state);

	jmethodID s_SetVisibleKeyboardStandard;
	void SetVisibleKeyboard(bool active, int type);
};