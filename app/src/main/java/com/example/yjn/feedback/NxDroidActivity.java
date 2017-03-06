package com.example.yjn.feedback;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import ch.swissms.nxdroid.lib.NxDroid;

public abstract class NxDroidActivity extends AppCompatActivity implements Application.OnInitializationLister {
    private static final int PERMISSIONS_REQUEST = 0;

    private NxDroid mNxDroid = null;

    private Boolean mGranted = null;
    private String[] mGrantedPermissions = null;
    private Boolean mInitialized = null;
    private final Object mLock = new Object();

    @Override
    protected void onStart() {
        Application.getApplication().setOnInitializationListener(this);
        requestPermissions();

        super.onStart();
    }

    @Override
    public void onSuccessfulInitialization(NxDroid nxDroid) {
        mNxDroid = nxDroid;

        synchronized (mLock) {
            mInitialized = true;
        }
        checkNxDroidStatus();
    }

    @Override
    public void onFailedInitialization() {
        synchronized (mLock) {
            mInitialized = false;
        }
        checkNxDroidStatus();
    }

    private void requestPermissions() {
        // Ask for permissions
        ArrayList<String> permissions = new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), PERMISSIONS_REQUEST);
        } else {
            // already granted
            synchronized (mLock) {
                mGranted = true;
            }
            checkNxDroidStatus();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        boolean granted = false;

        ArrayList<String> grantedPermissions = new ArrayList<String>();
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                for (int i = 0; i < grantResults.length; ++i) {
                    int grantResult = grantResults[i];
                    String permission = permissions[i];
                    if (grantResult == PackageManager.PERMISSION_GRANTED && permission.equals(Manifest.permission.READ_PHONE_STATE)) {
                        granted = true;
                    }
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        grantedPermissions.add(permission);
                    }
                }
            }
        }

        synchronized (mLock) {
            mGranted = granted;
            mGrantedPermissions = grantedPermissions.toArray(new String[grantedPermissions.size()]);
        }
        checkNxDroidStatus();
    }

    private void checkNxDroidStatus() {
        synchronized (mLock) {
            if (mGranted != null && mInitialized != null) {
                if (mInitialized && mGranted) {
                    mNxDroid.setGrantedPermissions(mGrantedPermissions);
                    onNxDroidSuccess();
                } else {
                    onNxDroidFailed();
                }
                mGranted = null;
            }
        }
    }

    public abstract void onNxDroidSuccess();
    public abstract void onNxDroidFailed();
}
