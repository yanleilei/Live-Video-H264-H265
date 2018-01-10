package com.example.hy.liveexampleandroid.LiveSend;

import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.hy.liveexampleandroid.Push.PusherImp;
import com.example.hy.liveexampleandroid.R;
import com.example.hy.liveexampleandroid.Util.IpChecker;
import com.example.hy.liveexampleandroid.Util.ToastUtil;
import com.example.hy.liveexampleandroid.View.SettingPopupWindow;

/**
 * Created by Hamik Young on 2017/12/29.
 */

public class SendActivity extends AppCompatActivity implements
        SendView, View.OnClickListener, PopupWindow.OnDismissListener {

    private Button mSendBtn;
    private TextureView mTextureView;
    private EditText mEditText;

    private Size mPreviewSize=null;
    private Size mPushSize=null;
    private String mPushType="";

    private SendPresenter presenter;

    private SettingPopupWindow mPopupWindow = null;
    private static final String TAG = "SendActivity";

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        mEditText = findViewById(R.id.push_IP);
        mSendBtn = findViewById(R.id.send_live_btn);
        mTextureView = findViewById(R.id.video_texture);
        mSendBtn.setOnClickListener(this);
        findViewById(R.id.take_pic_btn).setOnClickListener(this);
        mEditText.setText("192.168.1.1:8080");
        presenter = new SendPresenterImp(this, new SendInteractorImp());
        presenter.initialPusher();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting_item:
                showSettingPopWindow();
                break;
            case R.id.switch_camera:
                presenter.switchCamera();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void toastMessage(String message) {
        ToastUtil.toast(this, message, Toast.LENGTH_SHORT);
    }

    @Override
    public void showSettingPopWindow() {
        if (mSendBtn.getText().toString().equals(getResources().getString(R.string.startLive))) {
            if (mPopupWindow == null) {
                mPopupWindow = new SettingPopupWindow(this, PusherImp.supportSize);
                mPopupWindow.setOnDismissListener(this);
                mPreviewSize=mPopupWindow.getPreviewSize();
                mPushSize=mPopupWindow.getPushSize();
                mPushType=mPopupWindow.getPushType();
            }
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        } else {
            ToastUtil.toast(this, getResources().getString(R.string.please_stop_push), Toast.LENGTH_SHORT);
        }
        Log.i(TAG, "showSettingPopWindow: previewsize==="+String.valueOf(mPopupWindow.getPreviewSize()));
        Log.i(TAG, "showSettingPopWindow: pushSize==="+String.valueOf(mPopupWindow.getPushSize()));
        Log.i(TAG, "showSettingPopWindow: pushType===="+String.valueOf(mPopupWindow.getPushType()));

    }

    @Override
    public void IpEmptyError() {
        mEditText.setError(getString(R.string.Ip_Empty_Error));
    }

    @Override
    public void IpInvalidError() {
        mEditText.setError(getString(R.string.Ip_Error));
    }

    @Override
    public void btnTextChangeToStart() {
        mSendBtn.setText(R.string.startLive);
        mEditText.setEnabled(true);
    }

    @Override
    public void btnTextChangeToStop() {
        mSendBtn.setText(R.string.stopLive);
        mEditText.setEnabled(false);
    }

    @Override
    public TextureView supplyTextureView() {
        return mTextureView;
    }

    @Override
    public CameraManager supplyCameraManager() {
        return (CameraManager) getSystemService(CAMERA_SERVICE);
    }

    @Override
    public String getPushIp() {
        return mEditText.getText().toString();
    }

    @Override
    public void resetPopupWindow() {
        mPopupWindow = new SettingPopupWindow(this, PusherImp.supportSize);
        mPopupWindow.setOnDismissListener(this);
        mPreviewSize=mPopupWindow.getPreviewSize();
        mPushSize=mPopupWindow.getPushSize();
        mPushType=mPopupWindow.getPushType();
    }


    @Override
    public boolean IpIsValid() {
        return IpChecker.IsIpValid(mEditText.getText().toString());
    }

    @Override
    public boolean IpIsEmpty() {
        return IpChecker.IsIpEmpty(mEditText.getText().toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_live_btn:
                if (mSendBtn.getText().toString().equals(getResources().getString(R.string.startLive))) {
                    checkPushResolution();
                    presenter.startPushVideo();
                } else {
                    presenter.stopPushVideo();
                }
                break;
            case R.id.take_pic_btn:
                presenter.takePic();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDismiss() {
        if (mPreviewSize != mPopupWindow.getPreviewSize()) {
            mPreviewSize = mPopupWindow.getPreviewSize();
            presenter.setPreviewSize(mPreviewSize);
        }
    }
    private void checkPushResolution(){
        if (mPopupWindow == null) {
            mPopupWindow = new SettingPopupWindow(this, PusherImp.supportSize);
            mPopupWindow.setOnDismissListener(this);
        }
        if (mPushSize != mPopupWindow.getPushSize()) {
            mPushSize = mPopupWindow.getPushSize();
            presenter.setPushSize(mPushSize);
        }
        if (!mPushType.equals( mPopupWindow.getPushType())) {
            mPushType = mPopupWindow.getPushType();
            presenter.setPushType(mPushType);
        }
    }
}
