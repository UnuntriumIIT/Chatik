package com.example.ununtrium.chatik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;

public class imageViewAc extends AppCompatActivity {

    private ArrayList<ImgItem> mList = new ArrayList<>();
    private ImageAdapter adp;
    private static final int GALLERY_REQUEST = 1;
    private String login = "";
    private StorageReference stRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login = (String)getIntent().getSerializableExtra("LOGIN");
        stRef = FirebaseStorage.getInstance().getReference();
        setContentView(R.layout.activity_image_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        ListView imgList = findViewById(R.id.list_of_img);
        FloatingActionButton btnAdd = findViewById(R.id.add_btn);
        adp = new ImageAdapter(this, R.layout.img_view, mList);
        imgList.setAdapter(adp);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        String FileName = "images/"+ UUID.randomUUID().toString()+".jpg";
        StorageReference ref = stRef.child(FileName);

        switch(requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        mList.add(new ImgItem(login, bitmap,FileName));
                        UploadTask uploadTask = ref.putFile(selectedImage, metadata);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
        adp.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "array.bin");
            StorageReference ref = stRef.child("array/");
            ref.getFile(f);
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            mList = (ArrayList<ImgItem>) ois.readObject();
            adp.notifyDataSetChanged();
        }
        catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File f = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "array.bin");
            try {
                FileOutputStream fos = new FileOutputStream(f);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(mList);
                os.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("file/bin")
                    .build();
            StorageReference ref = stRef.child("array/");
            Uri uri = Uri.fromFile(f);
            UploadTask uploadTask = ref.putFile(uri, metadata);
            if (uploadTask.isComplete()) f.delete();
        }

    }
}
