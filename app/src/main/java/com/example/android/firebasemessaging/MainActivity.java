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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private final int SIGN_IN_REQUEST_CODE = 1;
    private final int START_CHAT_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
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
            displayUserList();
        }



    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
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

        if(requestCode == START_CHAT_REQUEST_CODE) {
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
        if(item.getItemId() == R.id.menu_sign_out) {
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
        return true;
    }



    public void startChat(View view) {
        Intent intent = new Intent(getApplicationContext(), StartChatActivity.class);
        startActivityForResult(intent, START_CHAT_REQUEST_CODE);
    }

    private void beginChat(String userUid, String userName){
        Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
        intent.putExtra("user_uid", userUid);
        intent.putExtra("user_name", userName);
        startActivity(intent);
    }

    private void displayUserList(){
        final RecyclerView listOfUsers = findViewById(R.id.list_of_users);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("chats")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .child("chat_list")
                .orderByChild("lastMessageTime")
                .limitToLast(50);

        UserListAdapter adapter = new UserListAdapter(R.layout.user_layout, query);
        listOfUsers.setAdapter(adapter);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listOfUsers.setLayoutManager(layoutManager);
    }
}
