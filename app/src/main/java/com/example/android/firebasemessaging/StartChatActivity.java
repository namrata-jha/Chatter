package com.example.android.firebasemessaging;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class StartChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_chat);
    }

    public void emailEntered(View view) {
        final EditText codeEditText = findViewById(R.id.email);
        String email = codeEditText.getText().toString();
        codeEditText.setText("");

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userRef.orderByChild("emailID").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot activitySnapShot: dataSnapshot.getChildren()){
                                MessageUser receivedData = activitySnapShot.getValue(MessageUser.class);
                                String uid =  Objects.requireNonNull(receivedData).getUserID();
                                Intent data = new Intent();
                                data.putExtra("uid", uid);
                                data.putExtra("user_name", receivedData.getUserName());
                                setResult(RESULT_OK, data);
                                finish();
                            }
                        }
                        else{
                            Toast.makeText(StartChatActivity.this, "Email doesn't exist.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(StartChatActivity.this, "Database error has occurred.", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
    }

    public void shareEmail(View view) {
        String message = "Hey, my email is " + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()
                + ". Start a conversation right away!";

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
        finish();
    }
}
