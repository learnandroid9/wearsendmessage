package me.xbt.wearsendmessage;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class WatchActivity extends Activity {

    /**
     * parameter name for msg.
     * used in intent.putExtra() to pass in a parameter.
     */
    public static final String PARAM_MSG = "param_msg";
    /** # of msg received */
    public static final String PARAM_NUM_MSG = "param_num_msg";

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                TextView textView2 = (TextView) stub.findViewById(R.id.text2);

                // get params passed to this intent.
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    String msg = extras.getString(PARAM_MSG);
                    int numMsg = extras.getInt(PARAM_NUM_MSG);
                    if (msg != null) {
                        mTextView.setText("msg received=" + msg);
                    }
                    textView2.setText("# of msgs received: " + numMsg);
                }
            }
        });
    }

    /** display the text to user */
    public void setText(String msg) {
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        TextView textView = (TextView) stub.findViewById(R.id.text);
        textView.setText(msg);
    }
}
