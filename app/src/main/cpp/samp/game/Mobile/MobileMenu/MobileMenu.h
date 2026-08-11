#pragma once

#include "../../common.h"
#include "../../RW/rwcore.h"
#include "../../Core/Vector2D.h"
#include "../../Radar.h"

enum eMobileSettings : int32 {
    MS_Visuals = 0x0,
    MS_Resolution = 0x1,
    MS_DrawDistance = 0x2,
    MS_SteerType = 0x3,
    MS_Traffic = 0x4,
    MS_Shadows = 0x5,
    MS_Targeting = 0x6,
    MS_CarReflections = 0x7,
    MS_Accelerometer = 0x8,
    MS_TouchLayout = 0x9,
    MS_CamHeight = 0xA,
    MS_SFXVolume = 0xB,
    MS_MusicVolume = 0xC,
    MS_Autotune = 0xD,
    MS_DefaultStation = 0xE,
    MS_InvertLook = 0xF,
    MS_Brightness = 0x10,
    MS_Language = 0x11,
    MS_BikeControl = 0x12,
    MS_HUD = 0x13,
    MS_SimulationSpeed = 0x14,
    MS_CinematicCam = 0x15,
    MS_DebugCam = 0x16,
    MS_SteerAnalogScale = 0x17,
    MS_Subtitles = 0x18,
    MS_JoypadLookX = 0x19,
    MS_JoypadLookY = 0x1A,
    MS_TweakAnalogLow = 0x1B,
    MS_TweakAnalogLowMax = 0x1C,
    MS_VibrationFeedback = 0x1D,
    MS_FrameLimiter = 0x1E,
    MS_MouseSensitivityX = 0x1F,
    MS_MouseSensitivityY = 0x20,
    MS_HRTF = 0x21,
    MS_XBOXControlScheme = 0x22,
    MS_AutoClimb = 0x23,
    MS_ReplayKit = 0x24,
    MS_MAX = 0x25
};

struct MobileSettings
{
    char            *name;
    unsigned int**  values;
    int32           value;
    int32           defaultValue;
    int32           min;
    int32           max;
    bool            visible;
    eMobileSettings type;
};

enum OSPointerState : int32_t
{
    OSPS_ButtonReleased = 0x0,
    OSPS_ButtonUp = 0x1,
    OSPS_ButtonPressed = 0x2,
    OSPS_ButtonDown = 0x3,
    OSPS_ButtonInvalid = 0xFF
};

struct MenuScreen
{
    int (**_vptr$MenuScreen)(void);
    RwTexture *arrowTex;
    float opacity;
    bool hasBack;
};

template <typename T> struct OSArray
{
    uint32_t numAlloced;
    uint32_t numEntries;
    T *dataPtr;
};

class CMobileMenu {
public:
    CVector2D               bgUVSize;
    CVector2D               bgTargetCoords;
    CVector2D               bgCurCoords;
    CVector2D               bgStartCoords;
    OSArray<MenuScreen*>    screenStack;
    MenuScreen*             pendingScreen;
    RwTexture*              bgTex;
    RwTexture*              sliderEmpty;
    RwTexture*              sliderFull;
    RwTexture*              sliderNub;
    RwTexture*              controlsBack;
    RwTexture*              controlsBack2;
    tBlipHandle             waypoint_blip;
    bool                    m_WantsToRestartGame;
    bool                    WantsToLoad;
    int                     SelectedSlot;
    bool                    CurrentGameNotResumable;
    bool                    InitializedForSignOut;
    float                   NEW_MAP_SCALE;
    float                   MAP_OFFSET_X;
    float                   MAP_OFFSET_Y;
    float                   MAP_AREA_X;
    float                   MAP_AREA_Y;
    bool                    DisplayingMap;
    bool                    isMapMode;
    bool                    pointerMode;
    bool                    isMouse;
    CVector2D               pointerCoords[4];
    OSPointerState          pointerState[4];
    uint32_t                pointerPress[4];

public:
    static CMobileMenu& Get() {
        static CMobileMenu* gMobileMenu = nullptr;
        if (!gMobileMenu) {
            gMobileMenu = reinterpret_cast<CMobileMenu*>(g_libGTASA + (VER_x32 ? 0x006E0074 : 0x8BE780));
        }
        return *gMobileMenu;
    }

    static inline MobileSettings ms_MobileSettings[eMobileSettings::MS_MAX];
};
VALIDATE_SIZE(OSArray<MenuScreen*>, (VER_x32 ? 0xC : 0x10));
VALIDATE_SIZE(CMobileMenu, (VER_x32 ? 0xB0 : 0xD0));
