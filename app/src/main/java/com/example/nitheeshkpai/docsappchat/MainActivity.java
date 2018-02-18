package com.example.nitheeshkpai.docsappchat;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nitheeshkpai.docsappchat.adapter.MessageAdapter;
import com.example.nitheeshkpai.docsappchat.model.Message;
import com.example.nitheeshkpai.docsappchat.utils.Constants;
import com.example.nitheeshkpai.docsappchat.utils.DatabaseHelper;
import com.example.nitheeshkpai.docsappchat.utils.NetworkChangeReceiver;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();

    private DatabaseHelper dBHelper;
    private Message receivedMessage;

    private EditText messageToBeSentEditText;
    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;

    private Gson gson;
    private RequestQueue queue;

    private final ArrayList<Message> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dBHelper = new DatabaseHelper(getApplicationContext());

        setContentView(R.layout.activity_main);

        initRecyclerView();

        messageToBeSentEditText = findViewById(R.id.edit_text_chat_box);
        ImageButton sendButton = findViewById(R.id.button_chat_box_send);

        //Set up Volley to send API request on clicking send
        queue = Volley.newRequestQueue(this);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Message sendingMessage = new Message(Constants.currentUserID, Constants.currentUserName, messageToBeSentEditText.getText());
                updateUI(sendingMessage);

                //Build URL for request
                String url = Constants.urlPrefix + messageToBeSentEditText.getText() + Constants.urlSuffix;

                //Re-adjust text in EditText
                messageToBeSentEditText.setText("");
                messageToBeSentEditText.setHint("Type your message here");

                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getInt("success") == 1) {
                                        gson = new Gson();
                                        receivedMessage = gson.fromJson(String.valueOf(response.getJSONObject("message")), Message.class);
                                        updateUI(receivedMessage);
                                        dBHelper.saveMessage(sendingMessage); //Add sent message to DB if response is successful
                                        dBHelper.saveMessage(receivedMessage); //Add received message to DB
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dBHelper.addPending(sendingMessage); //Add unsent message to pending list
                                Toast.makeText(getApplicationContext(), "No network Connection", Toast.LENGTH_LONG).show();
                            }
                        }
                );
                queue.add(getRequest);
            }
        });
    }

    private void initRecyclerView() {
        //Set up Recycler View and Adapter
        messageRecyclerView = findViewById(R.id.recycler_view_message_list);

        //Code to scroll up when keyboard appears
        messageRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                messageRecyclerView.smoothScrollToPosition(messageList.size());
            }
        });

        messageList.addAll(dBHelper.getAllItems());

        messageAdapter = new MessageAdapter(messageList);
        messageRecyclerView.setAdapter(messageAdapter);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateUI(Message message) { //Method to add messages to view, update scroll
        messageList.add(message);
        messageAdapter.notifyDataSetChanged();
        messageRecyclerView.post(new Runnable() {
            public void run() {
                messageRecyclerView.smoothScrollToPosition(messageRecyclerView.getBottom());
            }
        });
    }

    public void checkForPendingMessages() {
        ArrayList<Message> pendingMessages = dBHelper.getPendingMessages();
        for (Message m : pendingMessages) {
            Toast.makeText(getApplicationContext(), "Sending Pending Messages", Toast.LENGTH_SHORT).show();
            makeNetworkRequestForPendingMessages(m);
        }
    }

    private void makeNetworkRequestForPendingMessages(final Message pendingMessage) {
        //Build URL for request to send unsent messages
        String url = Constants.urlPrefix + pendingMessage.getMessage() + Constants.urlSuffix;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt("success") == 1) {
                                gson = new Gson();
                                receivedMessage = gson.fromJson(String.valueOf(response.getJSONObject("message")), Message.class);
                                updateUI(receivedMessage);
                                dBHelper.removePendingMessage(pendingMessage); //Update dirty bit of previously unsent message
                                dBHelper.saveMessage(receivedMessage); //Add received message to DB
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "No network Connection again", Toast.LENGTH_LONG).show();
                    }
                }
        );
        queue.add(getRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }
}
