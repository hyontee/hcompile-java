#include <jni.h>
#include <pthread.h>
#include <syscall.h>

#include "main.h"
#include "game/game.h"
#include "net/netgame.h"
#include "gui/gui.h"
#include "playertags.h"
#include "audiostream.h"
#include "java/jniutil.h"
#include <dlfcn.h>
#include "StackTrace.h"

// voice
#include "voice_new/Plugin.h"

#include "vendor/armhook/patch.h"
#include "vendor/str_obfuscator/str_obfuscator.hpp"

#include "settings.h"

#include "crashlytics.h"

JavaVM* javaVM;

char* g_pszStorage = nullptr;

UI* pUI = nullptr;
CGame *pGame = nullptr;

CNetGame *pNetGame = nullptr;
CPlayerTags* pPlayerTags = nullptr;
CSnapShotHelper* pSnapShotHelper = nullptr;
CAudioStream* pAudioStream = nullptr;
CJavaWrapper* pJavaWrapper = nullptr;
CSettings* pSettings = nullptr;

MaterialTextGenerator* pMaterialTextGenerator = nullptr;

bool bDebug = false;
bool bGameInited = false;
bool bNetworkInited = false;

uintptr_t g_libGTASA = 0x00;
uintptr_t g_libSAMP = 0x00;

int g_maxFPS = 60;
void ApplyFPSPatch(uint8_t fps);

void ApplyGlobalPatches();
void ApplyPatches_level0();
void InstallSpecialHooks();
void InitRenderWareFunctions();
void Log(const char* fmt, ...);

int work = 0;

void ReadSettingFile()
{
	pSettings = new CSettings();

	firebase::crashlytics::SetUserId(pSettings->Get().szNickName);
}

struct sigaction act_old;
struct sigaction act1_old;
struct sigaction act2_old;
struct sigaction act3_old;

void handler(int signum, siginfo_t *info, void* contextPtr)
{
	ucontext* context = (ucontext_t*)contextPtr;

	if (act_old.sa_sigaction)
	{
		act_old.sa_sigaction(signum, info, contextPtr);
	}

	if(info->si_signo == SIGSEGV)
	{
		Log("SIGSEGV | Fault address: 0x%x", info->si_addr);

		PRINT_CRASH_STATES(context);

		CStackTrace::printBacktrace();
	}

	return;
}

void handler1(int signum, siginfo_t *info, void* contextPtr)
{
	ucontext* context = (ucontext_t*)contextPtr;

	if (act1_old.sa_sigaction)
	{
		act1_old.sa_sigaction(signum, info, contextPtr);
	}

	if(info->si_signo == SIGABRT)
	{
		Log("SIGABRT | Fault address: 0x%x", info->si_addr);

		PRINT_CRASH_STATES(context);

		CStackTrace::printBacktrace();
	}

	return;
}

void handler2(int signum, siginfo_t *info, void* contextPtr)
{
	ucontext* context = (ucontext_t*)contextPtr;

	if (act2_old.sa_sigaction)
	{
		act2_old.sa_sigaction(signum, info, contextPtr);
	}

	if(info->si_signo == SIGFPE)
	{
		Log("SIGFPE | Fault address: 0x%x", info->si_addr);

		PRINT_CRASH_STATES(context);

		CStackTrace::printBacktrace();
	}

	return;
}

void handler3(int signum, siginfo_t *info, void* contextPtr)
{
	ucontext* context = (ucontext_t*)contextPtr;

	if (act3_old.sa_sigaction)
	{
		act3_old.sa_sigaction(signum, info, contextPtr);
	}

	if(info->si_signo == SIGBUS)
	{
		Log("SIGBUS | Fault address: 0x%x", info->si_addr);

		PRINT_CRASH_STATES(context);

		CStackTrace::printBacktrace();
	}

	return;
}

void DoInitStuff()
{
	if (bGameInited == false)
	{
		pPlayerTags = new CPlayerTags();
		pSnapShotHelper = new CSnapShotHelper();
		pMaterialTextGenerator = new MaterialTextGenerator();
		pAudioStream = new CAudioStream();
		pAudioStream->Initialize();

		pUI->splashscreen()->setVisible(false);
		pUI->chat()->setVisible(true);
		pJavaWrapper->toggleCustomButtons(false);

		pGame->Initialize();
		pGame->SetMaxStats();
		pGame->ToggleThePassingOfTime(false);

		LogVoice("[dbg:samp:load] : module loaded");
		ApplyFPSPatch(120);
		bGameInited = true;
	}

	if (!bNetworkInited && !bDebug)
	{
		pNetGame = new CNetGame("80.242.59.112", 1448, pSettings->Get().szNickName, pSettings->Get().szPassword);
		bNetworkInited = true;

        Log("DoInitStuff end");
	}
}

#define SPCALL_BUTTON_ALT 0
#define SPCALL_BUTTON_TAB 1
#define SPCALL_BUTTON_Y 2
#define SPCALL_BUTTON_N 3
#define SPCALL_BUTTON_F 4
#define SPCALL_HUD_FIST 5

extern "C" {
	JNIEXPORT void JNICALL Java_com_samp_mobile_game_SAMP_initializeSAMP(JNIEnv *pEnv, jobject thiz)
	{
		pJavaWrapper = new CJavaWrapper(pEnv, thiz);
	}
	JNIEXPORT void JNICALL Java_com_samp_mobile_game_SAMP_onInputEnd(JNIEnv *pEnv, jobject thiz, jbyteArray str)
	{
		if(pUI)
		{
			pUI->keyboard()->sendForGB(pEnv, thiz, str);
		}
	}
	JNIEXPORT void JNICALL Java_com_samp_mobile_game_SAMP_onEventBackPressed(JNIEnv *pEnv, jobject thiz)
	{

	}
	JNIEXPORT void JNICALL Java_com_samp_mobile_game_SAMP_sendDialogResponse(JNIEnv* pEnv, jobject thiz, jint i3, jint i, jint i2, jbyteArray str)
	{
		jboolean isCopy = true;

		jbyte* pMsg = pEnv->GetByteArrayElements(str, &isCopy);
		jsize length = pEnv->GetArrayLength(str);

		std::string szStr((char*)pMsg, length);

		if(pNetGame) {
			pNetGame->SendDialogResponse(i, i3, i2, (char*)szStr.c_str());
		}

		pEnv->ReleaseByteArrayElements(str, pMsg, JNI_ABORT);
	}
	JNIEXPORT void JNICALL Java_com_samp_mobile_game_ui_dialog_DialogManager_sendDialogResponse(JNIEnv* pEnv, jobject thiz, jint i3, jint i, jint i2, jbyteArray str)
	{
		jboolean isCopy = true;

		jbyte* pMsg = pEnv->GetByteArrayElements(str, &isCopy);
		jsize length = pEnv->GetArrayLength(str);

		std::string szStr((char*)pMsg, length);

		if(pNetGame) {
			pNetGame->SendDialogResponse(i, i3, i2, (char*)szStr.c_str());
		}

		pEnv->ReleaseByteArrayElements(str, pMsg, JNI_ABORT);
	}
JNIEXPORT void JNICALL Java_com_samp_mobile_game_SAMP_specialCall(JNIEnv *pEnv, jobject thiz, jint id)
{
	switch(id)
	{
		case SPCALL_BUTTON_ALT:
		{
			CPlayerPool* pPlayerPool = pNetGame->GetPlayerPool();
			if (pPlayerPool)
			{
				CLocalPlayer* pLocalPlayer;
				if (!pPlayerPool->GetLocalPlayer()->GetPlayerPed()->IsInVehicle() && !pPlayerPool->GetLocalPlayer()->GetPlayerPed()->IsAPassenger())
					LocalPlayerKeys.bKeys[ePadKeys::KEY_WALK] = true;
				else
					LocalPlayerKeys.bKeys[ePadKeys::KEY_FIRE] = true;
			}

			break;
		}
		case SPCALL_BUTTON_TAB:
		{
			if(pUI->playertablist()->visible())
				pUI->playertablist()->hide();
			else
				pUI->playertablist()->show();

			break;
		}
		case SPCALL_BUTTON_Y: LocalPlayerKeys.bKeys[ePadKeys::KEY_YES] = true; break;
		case SPCALL_BUTTON_N: LocalPlayerKeys.bKeys[ePadKeys::KEY_NO] = true; break;
		case SPCALL_BUTTON_F: LocalPlayerKeys.bKeys[ePadKeys::KEY_SECONDARY_ATTACK] = true; break;
	}
}
}

void MainLoop()
{
	if (pGame->bIsGameExiting) return;

	DoInitStuff();

	if (pNetGame) {
		pNetGame->Process();
	}

	if (pAudioStream) {
		pAudioStream->Process();
	}

}

void InitGui()
{
	// new voice
	Plugin::OnPluginLoad();
	Plugin::OnSampLoad();

	std::string font_path = string_format("%sSAMP/fonts/%s", g_pszStorage, FONT_NAME);
	pUI = new UI(ImVec2(RsGlobal->maximumWidth, RsGlobal->maximumHeight), font_path.c_str());
	pUI->initialize();
	pUI->performLayout();
}

#include "game/multitouch.h"
#include "armhook/patch.h"
#include "util/CUtil.h"
int androidSdkVer = 0;
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	javaVM = vm;
	LOGI("SA-MP library loaded! Build time: " __DATE__ " " __TIME__);

	g_libGTASA = CUtil::FindLib("libGTASA.so");
	if (g_libGTASA == 0x00) {
		LOGE("libGTASA.so address was not found! ");
		return JNI_VERSION_1_6;
	}

	g_libSAMP = CUtil::FindLib("libsamp.so");
	if (g_libSAMP == 0x00) {
		LOGE("libsamp.so address was not found! ");
		return JNI_VERSION_1_6;
	}

	firebase::crashlytics::Initialize();

	uintptr_t libgtasa = CUtil::FindLib("libGTASA.so");
	uintptr_t libsamp = CUtil::FindLib("libsamp.so");
	uintptr_t libc = CUtil::FindLib("libc.so");

	Log("libGTASA.so: 0x%x", libgtasa);
	Log("libsamp.so: 0x%x", libsamp);
	Log("libc.so: 0x%x", libc);

	char sdk_ver_str[92];
	if(__system_property_get("ro.build.version.sdk", sdk_ver_str)) {
		androidSdkVer = atoi(sdk_ver_str);
	}
	char str[100];

	sprintf(str, "0x%x", libgtasa);
	firebase::crashlytics::SetCustomKey("libGTASA.so", str);
	
	sprintf(str, "0x%x", libsamp);
	firebase::crashlytics::SetCustomKey("libsamp.so", str);

	sprintf(str, "0x%x", libc);
	firebase::crashlytics::SetCustomKey("libc.so", str);

	CHook::InitHookStuff();
	InstallSpecialHooks();
	ApplyPatches_level0();
    InitRenderWareFunctions();
    MultiTouch::initialize();

	pGame = new CGame();

	struct sigaction act;
	act.sa_sigaction = handler;
	sigemptyset(&act.sa_mask);
	act.sa_flags = SA_SIGINFO;
	sigaction(SIGSEGV, &act, &act_old);

	struct sigaction act1;
	act1.sa_sigaction = handler1;
	sigemptyset(&act1.sa_mask);
	act1.sa_flags = SA_SIGINFO;
	sigaction(SIGABRT, &act1, &act1_old);

	struct sigaction act2;
	act2.sa_sigaction = handler2;
	sigemptyset(&act2.sa_mask);
	act2.sa_flags = SA_SIGINFO;
	sigaction(SIGFPE, &act2, &act2_old);

	struct sigaction act3;
	act3.sa_sigaction = handler3;
	sigemptyset(&act3.sa_mask);
	act3.sa_flags = SA_SIGINFO;
	sigaction(SIGBUS, &act3, &act3_old);
		
	return JNI_VERSION_1_6;
}

uint32_t GetTickCount()
{
    return CTimer::m_snTimeInMillisecondsNonClipped;
}	

void Log(const char* fmt, ...)
{
	char buffer[0xFF];
	static FILE* flLog = nullptr;
	const char* pszStorage = g_pszStorage;


	if (flLog == nullptr && pszStorage != nullptr)
	{
		sprintf(buffer, "%s/samp_log.txt", pszStorage);
		flLog = fopen(buffer, "a");
	}

	memset(buffer, 0, sizeof(buffer));

	va_list arg;
	va_start(arg, fmt);
	vsnprintf(buffer, sizeof(buffer), fmt, arg);
	va_end(arg);

	LOGI("%s", buffer);
	firebase::crashlytics::Log(buffer);

	if (flLog == nullptr) return;
	fprintf(flLog, "%s\n", buffer);
	fflush(flLog);

	return;
}

void LogVoice(const char* fmt, ...)
{
	char buffer[0xFF];
	static FILE* flLog = nullptr;
	const char* pszStorage = g_pszStorage;

	if (flLog == nullptr && pszStorage != nullptr)
	{
		sprintf(buffer, "%sSAMP/%s", pszStorage, SV::kLogFileName);
		flLog = fopen(buffer, "w");
	}

	memset(buffer, 0, sizeof(buffer));

	va_list arg;
	va_start(arg, fmt);
	vsnprintf(buffer, sizeof(buffer), fmt, arg);
	va_end(arg);

	__android_log_write(ANDROID_LOG_INFO, "AXL", buffer);

	if (flLog == nullptr) return;
	fprintf(flLog, "%s\n", buffer);
	fflush(flLog);

	return;
}