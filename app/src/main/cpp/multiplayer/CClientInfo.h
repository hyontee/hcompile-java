#pragma once

#include <cstdint>
#include "main.h"
#include "game/common.h"
#include "game/game.h"
#include "net/netgame.h"

#ifndef SAMP_MAJOR_VERSION
#define SAMP_MAJOR_VERSION 2
#endif

#ifndef SAMP_MINOR_VERSION
#define SAMP_MINOR_VERSION 0
#endif

class CClientInfo
{
public:
    static uint16_t usSAMPMajorVersion;
    static uint16_t usSAMPMinorVersion;
    static bool     bSAMPModified;

    static uint16_t usLauncherVersion;
    static uint16_t usModpackVersion;

    static char szSerial[0x7F];

    static void WriteClientInfoToBitStream(RakNet::BitStream& bs);
};
