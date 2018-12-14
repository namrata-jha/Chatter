package com.example.android.firebasemessaging;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private final int SIGN_IN_REQUEST_CODE = 1;
    private final int START_CHAT_REQUEST_CODE = 2;

    @Override
    protected void onResume() {
        super.onResume();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            setUserOnlineState(true);
            displayUserList();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            setUserOnlineState(false);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            setUserOnlineState(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();
            setUserOnlineState(true);
            displayUserList();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                MessageUser user = new MessageUser(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName(),
                        FirebaseAuth.getInstance().getUid(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail());
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("users")
                        .child(user.getUserID())
                        .setValue(user);
                displayUserList();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }

        if (requestCode == START_CHAT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String userUid = data.getStringExtra("uid");
                String userName = data.getStringExtra("user_name");
                beginChat(userUid, userName);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            setUserOnlineState(false);
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();

                            // Close activity
                            finish();
                        }
                    });
        }

        if(item.getItemId()==R.id.menu_settings){
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }
        return true;
    }


    public void startChat(View view) {
        Intent intent = new Intent(getApplicationContext(), StartChatActivity.class);
        startActivityForResult(intent, START_CHAT_REQUEST_CODE);
    }

    private void beginChat(String userUid, String userName) {
        if (!userUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            final Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
            intent.putExtra("user_uid", userUid);
            intent.putExtra("user_name", userName);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Cannot initiate chat with self.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayUserList() {
        final RecyclerView listOfUsers = findViewById(R.id.list_of_users);

        Query query;
        if(getApplicationContext().getSharedPreferences
                ("settings", MODE_PRIVATE).getBoolean("VIEW_FAVOURITE", false)) {
            query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("chats")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .child("chat_list")
                    .orderByChild("favourite")
                    .limitToLast(50);
        }

        else {
            query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("chats")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .child("chat_list")
                    .orderByChild("lastMessageTime")
                    .limitToLast(50);
        }

        UserListAdapter adapter = new UserListAdapter(R.layout.user_layout, query, getApplicationContext());
        listOfUsers.setAdapter(adapter);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listOfUsers.setLayoutManager(layoutManager);
    }

    public void setUserOnlineState(final boolean state) {
        Boolean bool = state;
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("online").setValue(bool);

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference chatRef = dbRef
                .child("chats")
                .child(uid)
                .child("chat_list");

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> chatList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    chatList.add(snapshot.getKey());
                }
                for (String userUid : chatList) {
                    dbRef
                            .child("chats")
                            .child(userUid)
                            .child("chat_list")
                            .child(uid)
                            .child("online")
                            .setValue(state);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
