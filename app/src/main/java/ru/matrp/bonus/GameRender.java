package ru.matrp.bonus;

import android.graphics.Bitmap;
import com.nvidia.devtech.NvEventQueueActivity;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class GameRender {
    public static final int LISTENER_TYPE_RENDER = 0;
    public static final int LISTENER_TYPE_TEXTURE = 1;
    private static float OffX = 0.0f;
    private static float OffY = 0.0f;
    private static float OffZ = 0.0f;
    public static final int RENDER_TYPE_CAR = 1;
    public static final int RENDER_TYPE_OBJECT = 0;
    public static final int RENDER_TYPE_SKIN = 2;
    private NvEventQueueActivity mActivity = null;
    private ArrayList<GameRenderInstance> mQueue;

    public interface GameRenderListener {
        void OnRenderComplete(int i, byte[] bArr, long j);
    }

    public interface GameTextureListener {
        void OnTextureGet(int i, Bitmap bitmap);
    }

    private native void initJni();

    public native int[] createVehicleSnapshotTex(int iModel, int dwColor, float[] vecRot, float fZoom, int dwColor1, int dwColor2);

    private native void nativeRequestRender(int i, int i2, int i3, int i4, int i5, float f, float f2, float f3, float f4, float f5, float f6, float f7, Object obj);

    private native void nativeRequestRenderTexture(byte[] bArr, int i);

    private native void nativeRequestRenderTexturePlate(int i, byte[] bArr, byte[] bArr2, int i2, float f, float f2, float f3, float f4);

    private class GameRenderInstance {
        int id;
        GameRenderListener listener;
        GameTextureListener listenerTexture;
        int type;

        private GameRenderInstance() {
        }
    }

    public void SetOffsets(float f, float f2, float f3) {
        OffX = f;
        OffY = f2;
        OffZ = f3;
    }

    public GameRender(NvEventQueueActivity nvEventQueueActivity) {
        this.mActivity = nvEventQueueActivity;
        this.mQueue = new ArrayList<>();
        //initJni();
    }

    public void onNativeRendered(int i, byte[] bArr, long j) {
        for (int i2 = 0; i2 < this.mQueue.size(); i2++) {
            GameRenderInstance gameRenderInstance = this.mQueue.get(i2);
            if (gameRenderInstance.id == i) {
                gameRenderInstance.listener.OnRenderComplete(i, bArr, j);
                this.mQueue.remove(i2);
                return;
            }
        }
    }

    public void onNativeTextureSend(int i, byte[] bArr, long j, int i2, int i3) {
        for (int i4 = 0; i4 < this.mQueue.size(); i4++) {
            GameRenderInstance gameRenderInstance = this.mQueue.get(i4);
            if (gameRenderInstance.id == i) {
                Bitmap createBitmap = Bitmap.createBitmap(i2, i3, Bitmap.Config.ARGB_8888);
                createBitmap.setHasAlpha(true);
                IntBuffer asIntBuffer = ByteBuffer.wrap(bArr).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
                int[] iArr = new int[asIntBuffer.remaining()];
                asIntBuffer.get(iArr);
                createBitmap.setPixels(iArr, 0, i2, 0, 0, i2, i3);
                gameRenderInstance.listenerTexture.OnTextureGet(i, createBitmap);
                this.mQueue.remove(i4);
                return;
            }
        }
    }

    public void RequestRender(int i, int i2, int i3, int i4, int i5, float f, float f2, float f3, float f4, GameRenderListener gameRenderListener) {
        GameRenderInstance gameRenderInstance = new GameRenderInstance();
        int i6 = i2;
        gameRenderInstance.id = i6;
        GameRenderListener gameRenderListener2 = gameRenderListener;
        gameRenderInstance.listener = gameRenderListener2;
        gameRenderInstance.type = 0;
        this.mQueue.add(gameRenderInstance);
        nativeRequestRender(i, i6, i3, i4, i5, f, f2, f3, f4, OffX, OffY, OffZ, gameRenderListener2);
        OffZ = 0.0f;
        OffY = 0.0f;
        OffX = 0.0f;
    }

    public void RequestTexture(String str, int i, GameTextureListener gameTextureListener) {
        GameRenderInstance gameRenderInstance = new GameRenderInstance();
        gameRenderInstance.id = i;
        gameRenderInstance.type = 0;
        gameRenderInstance.listenerTexture = gameTextureListener;
        this.mQueue.add(gameRenderInstance);
        nativeRequestRenderTexture(str.getBytes(), i);
    }

    public void RequestTexturePlate(int i, int i2, String str, String str2, float f, float f2, float f3, float f4, GameTextureListener gameTextureListener) {
        GameRenderInstance gameRenderInstance = new GameRenderInstance();
        int i3 = i2;
        gameRenderInstance.id = i3;
        gameRenderInstance.type = 1;
        gameRenderInstance.listenerTexture = gameTextureListener;
        this.mQueue.add(gameRenderInstance);
        nativeRequestRenderTexturePlate(i, str.getBytes(), str2.getBytes(), i3, f, f2, f3, f4);
    }
}
