package com.example.yjn.feedback;

import ch.swissms.nxdroid.lib.NxDroid;
import ch.swissms.nxdroid.lib.events.listener.InitializationListener;

public class Application extends android.app.Application implements InitializationListener{

    private OnInitializationLister mOnInitializationLister = null;

    private static Application application = null;

    private Boolean mSuccessfulInitialization = null;

    public interface OnInitializationLister {
        void onSuccessfulInitialization(NxDroid nxDroid);
        void onFailedInitialization();
    }

    public static Application getApplication() {
        return application;
    }

    public void setOnInitializationListener(OnInitializationLister onInitializationListener) {
        mOnInitializationLister = onInitializationListener;
        if (mSuccessfulInitialization != null) {
            if (mSuccessfulInitialization) {
                mOnInitializationLister.onSuccessfulInitialization(NxDroid.sharedInstance());
            }
            else {
                mOnInitializationLister.onFailedInitialization();
            }
        }
    }

    @Override
    public void onCreate() {
        application = this;
        // Initialize NxDroid
        NxDroid.initialize(this, this);

        super.onCreate();
    }

    @Override
    public void onFailedInitialization() {
        mSuccessfulInitialization = false;
        if (mOnInitializationLister != null) {
            mOnInitializationLister.onFailedInitialization();
        }
    }

    @Override
    public void onSuccessfulInitialization(NxDroid nxDroid) {
        mSuccessfulInitialization = true;
        if (mOnInitializationLister != null) {
            mOnInitializationLister.onSuccessfulInitialization(nxDroid);
        }
    }
}
