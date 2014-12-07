package me.xbt.wearsendmessage;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WatchActivity extends Activity {

    /** msg path */
    public static final String MSG_FROM_WEAR = "/msg_from_wear";


    /**
     * parameter name for msg.
     * used in intent.putExtra() to pass in a parameter.
     */
    public static final String PARAM_MSG = "param_msg";
    /** # of msg received */
    public static final String PARAM_NUM_MSG = "param_num_msg";

    private TextView mTextView;

    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "WatchActivity";

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

                // show information received from msg.
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


                Button button = (Button)stub.findViewById(R.id.button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMessage(MSG_FROM_WEAR, "a msg from wear to mobile: " + new Date().toString());
                    }
                });
            }
        });

        // connect to data layer to send msg to mobile.
        connect();
    }

    /**
     * connect to wearable data layer
     */
    private void connect() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        //  "onConnected: null" is normal.
                        //  There's nothing in our bundle.
                        //sendMessage(MSG_FROM_WEAR, "a msg from wear to mobile: " + new Date().toString());
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                        // Request access only to the Wearable API
                .addApi(Wearable.API)
                .build();

        // need to add <meta-data> com.google.android.gms.version tag into androidmanifest.xml
        mGoogleApiClient.connect();
    }

    /**
     * send message to wearable data layer
     * @param path - msg path, used to identify what the msg is about
     * @param data - data inside msg
     */
    private void sendMessage(final String path, final String data){

        // it is necessary to put the following code in asynctask
        // getNodes() uses await(), which cannot be used on ui thread.
        // we will see an error about await cannot be called on ui thread if we do not use asynctask.
        new AsyncTask<Void, Void, List<Node>>(){

            @Override
            protected List<Node> doInBackground(Void... params) {
                return getNodes();
            }

            @Override
            protected void onPostExecute(List<Node> nodeList) {
                for(Node node : nodeList) {
                    Log.v(TAG, "sending msg.  data=" + data);

                    PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient,
                            node.getId(),
                            path,
                            data.getBytes()
                    );

                    result.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Log.v(TAG, "Phone: " + sendMessageResult.getStatus().getStatusMessage());
                        }
                    });
                }
            }
        }.execute();

    }


    /**
     * get a list of connected nodes that you can potentially send messages to
     */
    private List<Node> getNodes() {
        ArrayList<Node> results = new ArrayList<Node>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node);
        }
        return results;
    }

}
