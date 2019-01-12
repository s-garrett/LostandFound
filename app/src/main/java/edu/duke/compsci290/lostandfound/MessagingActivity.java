package edu.duke.compsci290.lostandfound;

import android.app.Activity;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MessagingActivity extends Activity {
    private Button mNewMessage;
    private FirebaseRecyclerAdapter<ChatMessage, MessageHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging_inbox);
        RecyclerView listOfMessages = findViewById(R.id.list_messages);
        listOfMessages.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("messages");

        Query query = Ref.orderByKey();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<ChatMessage>().setQuery(query, ChatMessage.class).build();

        mAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageHolder>(options){

            @Override
            protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull ChatMessage model) {
                Log.d("DEBUG", "Entering setting up messages details.");
                holder.setMessage(model.getMessageText());
                holder.setDate(model.getMessageTime());
                if(model.getMessageUser() == null){ holder.setUsername("Anon-USER");}
                else{ holder.setUsername(model.getMessageUser());}
            }

            @Override
            public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message, parent, false);

                return new MessageHolder(view);
            }
        };
        listOfMessages.setAdapter(mAdapter);

        mNewMessage = findViewById(R.id.new_message);
        mNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) findViewById(R.id.input);
                String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                //send input to the database as a new message.
                FirebaseDatabase.getInstance()
                        .getReference().child("messages")
                        .push()
                        .setValue(new ChatMessage(input.getText().toString().trim(), username));
                input.setText("");
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop(){
        super.onStop();
        mAdapter.stopListening();
    }


}
