package com.example.nitheeshkpai.docsappchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText messageToBeSent;
    private Button sendButton;

    private RecyclerView messageRecyclerView;
    private MessageListAdapter messageAdapter;

    private ArrayList<Message> messageList = new ArrayList<>();

    private Gson gson;

    private Message receivedMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageRecyclerView = findViewById(R.id.reyclerview_message_list);
        messageAdapter = new MessageListAdapter(this, messageList);
        messageRecyclerView.setAdapter(messageAdapter);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        final RequestQueue queue = Volley.newRequestQueue(this);

        messageToBeSent = findViewById(R.id.edittext_chatbox);

        sendButton = findViewById(R.id.button_chatbox_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                messageList.add(new Message(Constants.currentUserID, Constants.currentUserName,messageToBeSent.getText()));
                messageAdapter.notifyDataSetChanged();
                String url = "https://www.personalityforge.com/api/chat/?apiKey=6nt5d1nJHkqbkphe&message="+messageToBeSent.getText()+"&chatBotID=63906&externalID=chirag1";
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
                                        messageList.add(receivedMessage);
                                        messageAdapter.notifyDataSetChanged();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                );
                queue.add(getRequest);
            }
        });

    }
}
