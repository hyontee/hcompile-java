#include "CClientInfo.h"


uint16_t CClientInfo::usSAMPMajorVersion = SAMP_MAJOR_VERSION;
uint16_t CClientInfo::usSAMPMinorVersion = SAMP_MINOR_VERSION;
bool     CClientInfo::bSAMPModified = false;

uint16_t CClientInfo::usLauncherVersion = 0;
uint16_t CClientInfo::usModpackVersion = 0;

char CClientInfo::szSerial[0x7F] = {0};

void CClientInfo::WriteClientInfoToBitStream(RakNet::BitStream& bs)
{
    const uint16_t usChecksum = 0x94D5;
    bs.Write(usChecksum);
    bs.Write(usSAMPMajorVersion);
    bs.Write(usSAMPMinorVersion);
    bs.Write(bSAMPModified);

    bs.Write(usLauncherVersion);
    bs.Write(usModpackVersion);

    bs.Write((uint16_t)63);
    bs.Write(szSerial, 63);
}
