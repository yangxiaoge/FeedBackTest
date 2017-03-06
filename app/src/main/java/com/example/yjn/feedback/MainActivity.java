package com.example.yjn.feedback;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import ch.swissms.nxdroid.lib.NxDroid;
import ch.swissms.nxdroid.lib.Settings;
import ch.swissms.nxdroid.lib.Types;
import ch.swissms.nxdroid.lib.UserFeedback;

public class MainActivity extends NxDroidActivity {
    private TextView mConsole;
    private ScrollView mScroller;
    private EditText mFeedbackText;
    private Button mSendFeedback;

    private DataFragment mDataFragment;

    private static final String TAG_FRAGMENT = "DataFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();
        mDataFragment = (DataFragment) fm.findFragmentByTag(TAG_FRAGMENT);
        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mDataFragment == null) {
            mDataFragment = DataFragment.getInstance();
            fm.beginTransaction().add(mDataFragment, TAG_FRAGMENT).commit();
        }

        setContentView(R.layout.activity_main);

        mConsole = (TextView) findViewById(R.id.console);
        mScroller = (ScrollView) findViewById(R.id.scroller);
        mFeedbackText = (EditText) findViewById(R.id.text_feedback);
        mSendFeedback = (Button) findViewById(R.id.send_feedback_button);

        Settings.setBackgroundMonitoring(true, this);
    }

    /*
    * NxDroidActivity
    * */
    @Override
    public void onNxDroidSuccess() {
        final NxDroid nxDroid = NxDroid.sharedInstance();

        Settings.setBackgroundMonitoring(true, this);

        log("NxDroid is ready");
        if(nxDroid.isImsiAvailable()) {
            log("IMSI detected");

            mSendFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = mFeedbackText.getText().toString();

                    // You can choose about detail for a custom feedback with the constructors
                    UserFeedback userFeedback = new UserFeedback(Types.UserFeedbackType.OtherEvent, message);
                    nxDroid.notifyUserEvent(userFeedback);

                    log("UserFeedback sent. Message: " + message);
                }
            });
        } else {
            log("No IMSI detected");
        }
    }

    @Override
    public void onNxDroidFailed() {
        log("Failed to initialize NxDroid");
    }

    /*
    * Helpers
    * */
    private void log(String message) {
        mDataFragment.saveData(message);
        print();
    }

    private void print() {
        mConsole.post(new Runnable() {
            public void run() {
                mConsole.setText(mDataFragment.getData());
                mScroller.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
}
