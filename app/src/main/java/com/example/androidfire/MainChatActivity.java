package com.example.androidfire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainChatActivity extends AppCompatActivity {

    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference mDatabaseReference;
    private ChatListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        setUpDisplayName();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);

        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage();
                return true;
            }
        });

        // TODO: Add an OnClickListener to the sendButton to send a message

    }

    private void setUpDisplayName() {
        SharedPreferences pref = getSharedPreferences(RegisterActivity.CHAT_PREFS, 0);
        mDisplayName = pref.getString(RegisterActivity.DISPLAY_NAME_KEY, null);
        if(mDisplayName == null) mDisplayName = "Anonymous";
    }


    private void sendMessage() {
        String input = mInputText.getText().toString();
        if(!input.equals("")) {
            Message chat = new Message(input, mDisplayName);
            mDatabaseReference.child("message").push().setValue(chat)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Chat", "sendMessage" + task.isSuccessful());
                            } else  {
                                Log.d("Chat", "sendMessage failed" + task.getException());
                            }
                        }
                    });

            mInputText.setText("");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter = new ChatListAdapter(MainChatActivity.this, mDatabaseReference, mDisplayName);
        Log.d("Chat", "onStart: " + mAdapter);
        mChatListView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.cleanup();
    }

}