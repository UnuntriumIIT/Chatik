package com.example.ununtrium.chatik;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Chat extends AppCompatActivity {

    FirebaseListAdapter adapter;
    private DatabaseReference mDataBase;
    private String login = "";
    final ArrayList<ChatMessage> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ActivityCompat.requestPermissions(Chat.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(Chat.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        mDataBase = FirebaseDatabase.getInstance().getReference();
        login = (String)getIntent().getSerializableExtra("LOGIN");


        final ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
                .setLayout(R.layout.message)
                .setQuery(mDataBase, ChatMessage.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Object model, int position) {
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);
                ChatMessage msg = (ChatMessage) model;
                mList.add((ChatMessage) model);
                messageText.setText(msg.getMessageText());
                messageUser.setText(msg.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", msg.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        final EditText input = (EditText)findViewById(R.id.input);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDataBase.push().setValue(new ChatMessage(input.getText().toString(), login));
                adapter.notifyDataSetChanged();
                input.setText("");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);
        switch (id) {
            case R.id.action_img:
                Intent img_act = new Intent(this, imageViewAc.class);
                img_act.putExtra("LOGIN", login);
                startActivity(img_act);
                break;
            case R.id.download:
                Downloading d = new Downloading();
                d.execute();
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private class Downloading extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                try {
                    FileOutputStream fos =
                            new FileOutputStream(
                                    new File(Environment.getExternalStoragePublicDirectory(
                                            Environment.DIRECTORY_DOWNLOADS), UUID.randomUUID().toString()+".bin")
                            );
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(mList);
                    os.close();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }
    }
}
