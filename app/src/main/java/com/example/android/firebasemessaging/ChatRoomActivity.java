package com.example.android.firebasemessaging;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Objects;

public class ChatRoomActivity extends AppCompatActivity {

    private String myUid;
    private String userUid;
    private RecyclerView listOfMessages;
    private MessageListAdapter adapter;
    private MessageUser userData;
    private MessageUser myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        myUid = FirebaseAuth.getInstance().getUid();
        userUid = getIntent().getStringExtra("user_uid");
        String userName = getIntent().getStringExtra("user_name");
        Objects.requireNonNull(getSupportActionBar()).setTitle(userName);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");

        ref.orderByChild("userID").equalTo(userUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot activitySnapShot : dataSnapshot.getChildren()) {
                                userData = activitySnapShot.getValue(MessageUser.class);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        ref.orderByChild("userID").equalTo(myUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot activitySnapShot : dataSnapshot.getChildren()) {
                                myData = activitySnapShot.getValue(MessageUser.class);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = findViewById(R.id.input);
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                // Read the input field and push a new instance of ChatMessage to the Firebase database
                String message = input.getText().toString();
                if (!message.equals("")) {
                    dbRef.child("chats")
                            .child(myUid)
                            .child(userUid)
                            .push()
                            .setValue(new ChatMessage(message,myData));

                    dbRef.child("chats")
                            .child(userUid)
                            .child(myUid)
                            .push()
                            .setValue(new ChatMessage(message,myData));


                    dbRef.child("chats")
                            .child(myUid)
                            .child("chat_list")
                            .child(userData.getUserID())
                            .setValue(new ChatListUser(userData, -1* new Date().getTime(), message));

                    dbRef.child("chats")
                            .child(userUid)
                            .child("chat_list")
                            .child(myData.getUserID())
                            .setValue(new ChatListUser(myData, -1* new Date().getTime(), message));

                    // Clear the input
                    input.setText("");

                }
            }
        });

        displayChatMessages();
    }

    private void displayChatMessages(){

        listOfMessages = findViewById(R.id.list_of_messages);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("chats")
                .child(myUid)
                .child(userUid)
                .limitToLast(50);

        adapter = new MessageListAdapter(R.layout.my_message, query);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
        });
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition =
                        layoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    listOfMessages.scrollToPosition(positionStart);
                }
            }
        });
        listOfMessages.setAdapter(adapter);

        listOfMessages.setLayoutManager(layoutManager);
    }
}
