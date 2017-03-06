package com.example.yjn.feedback;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.webkit.WebView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ch.swissms.nxdroid.lib.NxDroid;
import ch.swissms.nxdroid.lib.events.listener.JobExecutionListener;
import ch.swissms.nxdroid.lib.reports.TaskReport;
import ch.swissms.nxdroid.lib.test.RunningJob;
import ch.swissms.nxdroid.lib.test.RunningTask;

public class DataFragment extends Fragment {

    private boolean mInitialized = false;
    private StringBuffer mData;
    private JobExecutionListener mListener;
    private IJobOutput mDelegate;

    final private Object mLock = new Object();

    public interface IJobOutput {
        void onJobStarted();
        void onJobStep(float v, float v1);
        void onJobEnded();
    }

    public static DataFragment getInstance() {
        DataFragment fragment = new DataFragment();
        fragment.init();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        init();
    }

    private void init(){
        if(!mInitialized){
            mData = new StringBuffer();
            mInitialized = true;
        }
    }

    @Override
    public void onDestroy() {
        removeListener();
        super.onDestroy();
    }

    public void addListener(IJobOutput delegate) {
        synchronized (mLock) {
            mDelegate = delegate;
        }
        if (mListener == null) {
            mListener = new JobExecutionListener() {
                @Override
                public void onWebViewNeedRefresh(WebView webView) {

                }

                @Override
                public void onTaskStarted(RunningTask runningTask, float v, float v1) {

                }

                @Override
                public void onTaskEnded(RunningTask runningTask, float v, float v1) {

                }

                @Override
                public void onTaskTransferredData(RunningTask runningTask, int i) {

                }

                @Override
                public void onTaskRttData(RunningTask runningTask, int i) {

                }

                @Override
                public void onCancel(RunningTask runningTask) {

                }

                @Override
                public void onTaskResult(TaskReport taskReport) {

                }

                @Override
                public void onCycleStartedWithJobProgress(float v, float v1) {

                }

                @Override
                public void onCycleEndedWithJobProgress(float v, float v1) {

                }

                @Override
                public void onJobStarted(RunningJob runningJob, float v, float v1) {
                    synchronized (mLock) {
                        if (mDelegate != null) {
                            mDelegate.onJobStarted();
                        }
                    }
                }

                @Override
                public void onJobEndedWithJobProgress(float v, float v1) {
                    synchronized (mLock) {
                        if (mDelegate != null) {
                            mDelegate.onJobEnded();
                        }
                    }
                }

                @Override
                public void onUpdatedProgress(float v, float v1) {
                    synchronized (mLock) {
                        if (mDelegate != null) {
                            mDelegate.onJobStep(v, v1);
                        }
                    }
                }

                @Override
                public void onNewThroughputInfo(long l) {

                }

                @Override
                public void onYouTubePlayerRequested() {

                }

            };
            NxDroid.sharedInstance().addJobExecutionListener(mListener);
        }
    }

    public void removeListener() {
        if(mListener != null) {
            NxDroid.sharedInstance().removeJobExecutionListener(mListener);
            mDelegate = null;
        }
    }

    public void saveData(String newLine) {
        mData.append(String.format(Locale.US, "\n%s %s", new SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(new Date()), newLine));
    }

    public String getData() {
        return mData.toString();
    }
}
