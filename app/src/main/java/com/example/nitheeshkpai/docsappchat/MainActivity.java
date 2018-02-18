package com.example.nitheeshkpai.docsappchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.nitheeshkpai.docsappchat.utils.Constants;
import com.example.nitheeshkpai.docsappchat.utils.DatabaseHelper;
import com.example.nitheeshkpai.docsappchat.adapter.MessageAdapter;
import com.example.nitheeshkpai.docsappchat.model.Message;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dBHelper;

    private EditText messageToBeSent;
    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;

    private final ArrayList<Message> messageList = new ArrayList<>();

    private Gson gson;

    private Message receivedMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dBHelper = new DatabaseHelper(getApplicationContext());

        setContentView(R.layout.activity_main);

        initRecyclerView();

        messageToBeSent = findViewById(R.id.edit_text_chat_box);
        ImageButton sendButton = findViewById(R.id.button_chat_box_send);

        //Set up Volley to send API request on clicking send
        final RequestQueue queue = Volley.newRequestQueue(this);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateUIandDB(new Message(Constants.currentUserID, Constants.currentUserName,messageToBeSent.getText()));

                //Build URL for request
                String url = "https://www.personalityforge.com/api/chat/?apiKey=6nt5d1nJHkqbkphe&message="+messageToBeSent.getText()+"&chatBotID=63906&externalID=chirag1";

                //Re-adjust text in EditText
                messageToBeSent.setText("");
                messageToBeSent.setHint("Type your message here");

                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if(response.getInt("success") == 1) {
                                        gson = new Gson();
                                        receivedMessage = gson.fromJson(String.valueOf(response.getJSONObject("message")),Message.class);
                                        updateUIandDB(receivedMessage);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),"No network Connection",Toast.LENGTH_LONG).show();
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
            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {
                messageRecyclerView.smoothScrollToPosition(messageList.size());
            }
        });

        messageList.addAll(dBHelper.getAllItems());

        messageAdapter = new MessageAdapter(messageList);
        messageRecyclerView.setAdapter(messageAdapter);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void updateUIandDB(Message message) { //Method to add messages to view, update scroll
        messageList.add(message);
        messageAdapter.notifyDataSetChanged();
        messageRecyclerView.post(new Runnable() {
            public void run() {
                messageRecyclerView.smoothScrollToPosition(messageRecyclerView.getBottom());
            }
        });
        dBHelper.addItem(message);
    }
}
