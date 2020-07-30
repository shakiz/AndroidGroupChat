package com.sciecwlms.firebasechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {
    // Firebase instance variables
    private Button btnSend;
    private EditText edtMessage;
    private RecyclerView rvMessage;

    private AppPreference mAppPreference;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<Message, ChatViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSend = (Button) findViewById(R.id.sendButton);
        edtMessage = (EditText) findViewById(R.id.messageEditText);
        rvMessage = (RecyclerView) findViewById(R.id.messageRecyclerView);
        rvMessage.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvMessage.setLayoutManager(layoutManager);

        mAppPreference = new AppPreference(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtMessage.getText().toString().trim();
                Message object = new Message(message,mAppPreference.getEmail());
                mDatabaseReference.child("chat")
                        .push()
                        .setValue(object)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                edtMessage.setText("");
                                if(task.isSuccessful()){
                                    Log.v("SendMessage", "Success");
                                }else{
                                    Log.v("SendMessage", "Failed ");
                                }
                            }
                        });
            }
        });

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("chat");

        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(query, new SnapshotParser<Message>() {
                            @NonNull
                            @Override
                            public Message parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Message(snapshot.child("message").getValue().toString(),snapshot.child("sender").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Message, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i, @NonNull Message message) {
                chatViewHolder.tvMessage.setText(message.message);
                chatViewHolder.tvEmail.setText(message.sender);
            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_row_chat,parent,false);
                return new ChatViewHolder(view);
            }
        };


        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition =
                        layoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rvMessage.scrollToPosition(positionStart);
                }
            }
        });

        rvMessage.setAdapter(adapter);

    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail, tvMessage;
        ImageView icon;

        public ChatViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            tvEmail = (TextView) itemView.findViewById(R.id.tv_sender);
            tvMessage = (TextView) itemView.findViewById(R.id.tv_message);
        }
    }

    @Override
    public void onPause() {
        adapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}