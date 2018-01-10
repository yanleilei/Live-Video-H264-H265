package com.example.hy.liveexampleandroid.Push.Camera;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.os.Handler;
import android.util.Size;

/**
 * Created by Hamik Young on 2018/1/4.
 */

public interface Camera {

 void initial();
 void setIsProcessImage(boolean isProcessImage);
 void switchCamera();
 void setPreviewSize(Size previewSize);
 CameraDevice getCameraDevice();
 void closeCamera();

}
