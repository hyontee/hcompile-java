#include "CVehicleGTA.h"
#include "patch.h"

#include "game/Enums/ePedState.h"
#include "game/RegisteredCorona.h"
#include "game/Coronas.h"
#include "game/Models/ModelInfo.h"
#include "game/Entity/CPedGTA.h"
#include "game/Entity/CEntityGTA.h"

void CVehicleGTA::RenderDriverAndPassengers() {
    if(IsRCVehicleModelID())
        return;

    if (pDriver && pDriver->m_nPedState == PEDSTATE_DRIVING) {
        CHook::CallFunction<void>(g_libGTASA + (VER_x32 ? 0x4A6964 + 1 : 0x59D3B8), pDriver);
        // pDriver->Render();
    }

    for (auto& passenger : m_apPassengers) {
        if (passenger && passenger->m_nPedState == PEDSTATE_DRIVING) {
            CHook::CallFunction<void>(g_libGTASA + (VER_x32 ? 0x4A6964 + 1 : 0x59D3B8), passenger);
            // passenger->Render();
        }
    }
}

void CVehicleGTA::SetDriver(CPedGTA* driver) {
    CEntityGTA::ChangeEntityReference(pDriver, driver);

    ApplyTurnForceToOccupantOnEntry(driver);
}

bool CVehicleGTA::AddPassenger(CPedGTA* passenger) {
    ApplyTurnForceToOccupantOnEntry(passenger);

    // Now, find a seat and place them into it
    const auto seats = GetMaxPassengerSeats();

    for(auto & emptySeat : m_apPassengers) {
        emptySeat = passenger;
        CEntityGTA::RegisterReference(emptySeat);
        m_nNumPassengers++;
        return false;
    }

    // No empty seats
    return false;
}

bool CVehicleGTA::AddPassenger(CPedGTA* passenger, uint8 seatIdx) {
    if (m_nVehicleFlags.bIsBus) {
        return AddPassenger(passenger);
    }

    // Check if seat is valid
    if (seatIdx >= m_nMaxPassengers) {
        return false;
    }

    // Check if anyone is already in that seat
    if (m_apPassengers[seatIdx]) {
        return false;
    }

    // Place passenger into seat, and add ref
    m_apPassengers[seatIdx] = passenger;
    CEntityGTA::RegisterReference(m_apPassengers[seatIdx]);
    m_nNumPassengers++;

    return true;
}

void CVehicleGTA::ApplyTurnForceToOccupantOnEntry(CPedGTA* passenger) {
    // Apply some turn force
    switch (m_nVehicleType) {
        case VEHICLE_TYPE_BIKE: {
            ApplyTurnForce(
                    GetUp() * passenger->m_fMass / -50.f,
                    GetForward() / -10.f // Behind the bike
            );
            break;
        }
        default: {
            ApplyTurnForce(
                    CVector{ .0f, .0f, passenger->m_fMass / -5.f },
                    CVector{ CVector2D{passenger->GetPosition() - GetPosition()}, 0.f }
            );
            break;
        }
    }
}

int CVehicleGTA::GetPassengerIndex(const CPedGTA* passenger) {
    for(int i = 0; i <  std::size(m_apPassengers); i++) {
        if(passenger == m_apPassengers[i])
            return i;
    }
    return -1;
}

void CVehicleGTA::AddVehicleUpgrade(int32 modelId) {
    CHook::CallFunction<void>(g_libGTASA + (VER_x32 ? 0x0058C66C + 1 : 0x6AFF4C), this, modelId);
}

void CVehicleGTA::RemoveVehicleUpgrade(int32 upgradeModelIndex) {
    CHook::CallFunction<void>(g_libGTASA + (VER_x32 ? 0x58CC2C + 1 : 0x6B0718), this, upgradeModelIndex);
}

// 0x6D3000
void CVehicleGTA::SetGettingInFlags(uint8 doorId) {
    m_nGettingInFlags |= doorId;
}

// 0x6D3020
void CVehicleGTA::SetGettingOutFlags(uint8 doorId) {
    m_nGettingOutFlags |= doorId;
}

// 0x6D3040
void CVehicleGTA::ClearGettingInFlags(uint8 doorId) {
    m_nGettingInFlags &= ~doorId;
}

// 0x6D3060
void CVehicleGTA::ClearGettingOutFlags(uint8 doorId) {
    m_nGettingOutFlags &= ~doorId;
}

// ----------------------------------- hooks

void RenderDriverAndPassengers_hook(CVehicleGTA *thiz)
{
    thiz->RenderDriverAndPassengers();
}

void SetDriver_hook(CVehicleGTA *thiz, CPedGTA *pPed)
{
    thiz->SetDriver(pPed);
}

bool CVehicle__GetVehicleLightsStatus_hook(CVehicleGTA *pVehicle)
{
    return pVehicle->GetLightsStatus();
}

void (*CVehicle__DoVehicleLights)(CVehicleGTA* thiz, CMatrix *matVehicle, uint32 nLightFlags);
void CVehicle__DoVehicleLights_hook(CVehicleGTA* thiz, CMatrix *matVehicle, uint32 nLightFlags)
{
    uint8_t old = thiz->m_nVehicleFlags.bEngineOn;
    thiz->m_nVehicleFlags.bEngineOn = 1;
    CVehicle__DoVehicleLights(thiz, matVehicle, nLightFlags);
    thiz->m_nVehicleFlags.bEngineOn = old;
}

bool CVehicle__DoTailLightEffect(CVehicleGTA* thisVehicle, int32_t lightId, CMatrix* matVehicle, int isRight, int forcedOff, uint32_t nLightFlags, int lightsOn) {

    constexpr int REVERSE_LIGHT_OFFSET = 5;

    auto pModelInfoStart = CModelInfo::GetVehicleModelInfo(thisVehicle->m_nModelIndex);

    CVector* m_avDummyPos = pModelInfoStart->m_pVehicleStruct->m_avDummyPos;

    auto v = CVector(m_avDummyPos[1]);

    if (!isRight)
        v.x = -v.x;

    uint8_t alpha = (thisVehicle->m_fBreakPedal > 0) ? 100 : 56;
    if (thisVehicle->GetLightsStatus() || (thisVehicle->m_fBreakPedal > 0 && thisVehicle->pDriver)) {
        CCoronas::RegisterCorona(
                (uintptr) &thisVehicle->m_placement.m_vPosn.y + 2 * lightId + isRight,
                thisVehicle,
                180, 0, 0, alpha,
                &v,
                0.45f,
                40.f,
                eCoronaType::CORONATYPE_HEADLIGHT,
                eCoronaFlareType::FLARETYPE_NONE,
                false,
                false,
                0,
                0.0f,
                false,
                0,
                0,
                15.0f,
                false,
                false
        );
    }
    return true;
}

void CVehicleGTA::SetupRender() {
    RwRenderStateSet(rwRENDERSTATECULLMODE, RWRSTATE(TRUE));

    // CVehicleModelInfo::SetupLightFlags(this);
    //CVehicleModelInfo::SetEditableMaterials(m_pRwClump);
}

void SetupRender_hook(CVehicleGTA* thiz) {
    thiz->SetupRender();
}

void ProcessSirenAndHorn_hook(CVehicleGTA* thiz, bool bHornAvailable) {
    thiz->ProcessSirenAndHorn(bHornAvailable);
}
bool UsesSiren_hook(CVehicleGTA* thiz) {
    return thiz->UsesSiren();
}

#include "net/netgame.h"
extern CNetGame* pNetGame;

void DoHeadLightBeam(CVehicleGTA* thiz, eVehicleDummy dummyId, CMatrix* matrix, bool arg2) {
    uint8_t r = 0xFF, g = 0xFF, b = 0xFF, a;

    CVehiclePool* pVehiclePool = pNetGame->GetVehiclePool();
    VEHICLEID VehicleID = pVehiclePool->FindIDFromGtaPtr(thiz);
    auto veh = pVehiclePool->GetAt(VehicleID);

    if (veh)
        veh->processHeadlightsBeamColor(r, g, b, a);

    auto mi = CModelInfo::GetVehicleModelInfo(thiz->m_nModelIndex);
    CVector pointModelSpace = mi->GetModelDummyPosition(static_cast<eVehicleDummy>(2 * dummyId));
    if (dummyId == DUMMY_LIGHT_REAR_MAIN && pointModelSpace.IsZero())
        return;

    CVector point = matrix->GetPosition() + matrix->TransformVector(pointModelSpace);
    if (!arg2) {
        point -= 2 * pointModelSpace.x * matrix->GetRight();
    }
    const CVector pointToCamDir = Normalized(CCamera::Get().GetPosition() - point);
    const auto alpha = (uint8)((1.0f - std::fabs(DotProduct(pointToCamDir, matrix->GetForward()))) * 45.0f);

    RwRenderStateSet(rwRENDERSTATEZWRITEENABLE,         RWRSTATE(FALSE));
    RwRenderStateSet(rwRENDERSTATEZTESTENABLE,          RWRSTATE(TRUE));
    RwRenderStateSet(rwRENDERSTATEVERTEXALPHAENABLE,    RWRSTATE(TRUE));
    RwRenderStateSet(rwRENDERSTATESRCBLEND,             RWRSTATE(rwBLENDSRCALPHA));
    RwRenderStateSet(rwRENDERSTATEDESTBLEND,            RWRSTATE(rwBLENDINVSRCALPHA));
    RwRenderStateSet(rwRENDERSTATESHADEMODE,            RWRSTATE(rwSHADEMODEGOURAUD));
    RwRenderStateSet(rwRENDERSTATETEXTURERASTER,        RWRSTATE(NULL));
    RwRenderStateSet(rwRENDERSTATECULLMODE,             RWRSTATE(rwCULLMODECULLNONE));
    RwRenderStateSet(rwRENDERSTATEALPHATESTFUNCTION,    RWRSTATE(rwALPHATESTFUNCTIONGREATER));
    RwRenderStateSet(rwRENDERSTATEALPHATESTFUNCTIONREF, RWRSTATE(FALSE));

    const float   angleMult   = 0.15f;
    const CVector lightNormal = Normalized(matrix->GetForward() - matrix->GetUp() * angleMult);
    const CVector lightRight  = Normalized(CrossProduct(lightNormal, pointToCamDir));
    const CVector lightPos    = point - matrix->GetForward() * 0.1f;

    const CVector posn[] = {
            lightPos - lightRight * 0.05f,
            lightPos + lightRight * 0.05f,
            lightPos + lightNormal * 3.0f - lightRight * 0.5f,
            lightPos + lightNormal * 3.0f + lightRight * 0.5f,
            lightPos + lightNormal * 0.2f
    };
    const uint8 alphas[] = { alpha, alpha, 0, 0, alpha };

    RxObjSpace3DVertex vertices[5];

    for (auto i = 0u; i < std::size(vertices); i++) {
        const RwRGBA color = { r, g, b, alphas[i] };

        RxObjSpace3DVertexSetPreLitColor(&vertices[i], &color);
        RxObjSpace3DVertexSetPos(&vertices[i], &posn[i]);
    }

    if (RwIm3DTransform(vertices, std::size(vertices), nullptr, rwIM3D_VERTEXRGBA | rwIM3D_VERTEXXYZ))
    {
        RxVertexIndex indices[] = { 0, 1, 4, 1, 3, 4, 2, 3, 4, 0, 2, 4 };
        RwIm3DRenderIndexedPrimitive(rwPRIMTYPETRILIST, indices, std::size(indices));
        RwIm3DEnd();
    }

    RwRenderStateSet(rwRENDERSTATETEXTURERASTER,         RWRSTATE(FALSE));
    RwRenderStateSet(rwRENDERSTATEZWRITEENABLE,          RWRSTATE(TRUE));
    RwRenderStateSet(rwRENDERSTATEZTESTENABLE,           RWRSTATE(TRUE));
    RwRenderStateSet(rwRENDERSTATESRCBLEND,              RWRSTATE(rwBLENDSRCALPHA));
    RwRenderStateSet(rwRENDERSTATEDESTBLEND,             RWRSTATE(rwBLENDINVSRCALPHA));
    RwRenderStateSet(rwRENDERSTATEVERTEXALPHAENABLE,     RWRSTATE(FALSE));
    RwRenderStateSet(rwRENDERSTATECULLMODE,              RWRSTATE(rwCULLMODECULLNONE));
}

void CVehicleGTA::InjectHooks() {
    // var
    CHook::Write(g_libGTASA + (VER_x32 ? 0x675F10 : 0x849EA8), &CVehicleGTA::m_aSpecialColModel);

    // CHook::Redirect("_ZN8CVehicle11SetupRenderEv", &SetupRender_hook);

    CHook::Redirect("_ZN8CVehicle25RenderDriverAndPassengersEv", &RenderDriverAndPassengers_hook);
    CHook::Redirect("_ZN8CVehicle9SetDriverEP4CPed", &SetDriver_hook);

    CHook::Redirect("_ZN8CVehicle17DoTailLightEffectEiR7CMatrixhhjh", &CVehicle__DoTailLightEffect);
    // CHook::Redirect("_ZN8CVehicle15DoHeadLightBeamEiR7CMatrixh", &DoHeadLightBeam);
    CHook::Redirect("_ZN8CVehicle22GetVehicleLightsStatusEv", &CVehicle__GetVehicleLightsStatus_hook);

    CHook::Redirect("_ZN8CVehicle19ProcessSirenAndHornEb", &ProcessSirenAndHorn_hook);
    CHook::Redirect("_ZNK8CVehicle9UsesSirenEv", &UsesSiren_hook);
}

bool CVehicleGTA::IsRCVehicleModelID() {
    switch (m_nModelIndex) {
        case 441:
        case 464:
        case 465:
        case 594:
        case 501:
        case 564:
            return true;

        default:
            break;
    }
    return false;
}

#include "game/Widgets/TouchInterface.h"
bool CVehicleGTA::UsesSiren() const {
    return m_nVehicleFlags.bHasSiren;
}

void CVehicleGTA::ProcessSirenAndHorn(bool bHornAvailable) {
    auto localPed = pNetGame->GetPlayerPool()->GetLocalPlayer()->GetPlayerPed()->m_pPed;
    m_cHorn = 0;

    if (!localPed->IsInVehicle() || pDriver != localPed) {
        auto byteCurDriver = FindPlayerNumFromPedPtr(reinterpret_cast<CPedGTA*>(pDriver));
        m_cHorn = RemotePlayerKeys[byteCurDriver].bKeys[ePadKeys::KEY_CROUCH];
        return;
    }

    if (UsesSiren() && CTouchInterface::IsDoubleTapped(CTouchInterface::WIDGET_HORN, false, 1)) {
        m_nVehicleFlags.bSirenOrAlarm = !m_nVehicleFlags.bSirenOrAlarm;
    }

    uint32 hornActivate = CTouchInterface::IsTouched(CTouchInterface::WIDGET_HORN, nullptr, 1);
    LocalPlayerKeys.bKeys[ePadKeys::KEY_CROUCH] = hornActivate;

    if(bHornAvailable)
        m_cHorn = hornActivate;
}
