package com.example.analshah.chatbot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;

    //view objects
    private Button buttonLogout;
    private static final int PICK_IMAGE_REQUEST =123 ;
    private Button choose,upload,download;
    private ImageView imageView;
    private Uri filepath;
    private StorageReference mStorageRef;
    private File file;
    private String filename;
    private Uri filedata;
    private String pathfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        choose=(Button)findViewById(R.id.choose);
        upload=(Button)findViewById(R.id.upload);
        download=(Button)findViewById(R.id.download);
        imageView=(ImageView)findViewById(R.id.imageview);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        choose.setOnClickListener(this);
        upload.setOnClickListener(this);
        download.setOnClickListener(this);
        firebaseAuth =FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //initializing views
        buttonLogout = (Button) findViewById(R.id.buttonLogout);

        //adding listener to button
        buttonLogout.setOnClickListener(this);
    }

    private void Showfilechooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select an image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            filedata=data.getData();
            pathfile=filedata.getPath();
            file=new File(pathfile);

            filename =file.getName();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadimage()
    {
        StorageReference riversRef = mStorageRef.child("picsss/"+ filename);

        if(filepath != null) {

            final ProgressDialog progressdialog=new ProgressDialog(this);
            progressdialog.setTitle("uploading....");
            progressdialog.show();
            riversRef.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressdialog.dismiss();
                            Toast.makeText(getApplicationContext(),"file uploaded",Toast.LENGTH_SHORT).show();
                            imageView.setImageResource(0);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressdialog.dismiss();
                            Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                {
                   // double progress=(100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    //progressdialog.setMessage(((int)progress)+" % uploading...");


                }
            });
        }

    }

    private void downloadimage()
    {
        StorageReference riversRef = mStorageRef.child("picsss/"+ filename);

        if(filepath != null) {

            try {
                File localFile = File.createTempFile("images", "jpg");
                riversRef.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created

                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        imageView.setImageBitmap(bitmap);
                        Toast.makeText(getApplicationContext(),"file downloaded",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    @Override
    public void onClick(View view) {
        //if logout is pressed
        if (view == buttonLogout) {
            //logging out the user
            firebaseAuth.signOut();
            //closing activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
        if(view == choose)
        {
            Showfilechooser();

        }
         if(view == upload)
        {
            uploadimage();
        }
        if(view == download)
        {
            downloadimage();
        }


    }
}
