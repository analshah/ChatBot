package com.example.analshah.chatbot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "imagess";
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
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String imageId;
    private FirebaseUser user;
    private  Uri fileuri;



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

        mFirebaseInstance=FirebaseDatabase.getInstance();
        mFirebaseDatabase=mFirebaseInstance.getReference("ImagesValues");

        mFirebaseInstance.getReference("app_name").setValue("ChatBot");


        mFirebaseInstance.getReference("app_name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.e(TAG, "App title updated");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "Failed to read app title value.",databaseError.toException());
            }
        });

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

    private void uploadimage(String imagename ,String imageid)
    {

        if (TextUtils.isEmpty(imageId)) {
            imageId = mFirebaseDatabase.push().getKey();

        }
        user=FirebaseAuth.getInstance().getCurrentUser();
        fileuri=Uri.fromFile(new File(pathfile));
        ImageDetail imagedetail=new ImageDetail(imagename,imageid,user.getUid(),fileuri.toString());
        mFirebaseDatabase.child(imageId).setValue(imagedetail);
        mFirebaseDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.e(TAG," "+s);
                Log.e("helloo"," "+dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        addUserChangeListener();

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

    private void addUserChangeListener() {

        mFirebaseDatabase.child(imageId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageDetail image = dataSnapshot.getValue(ImageDetail.class);

                // Check for null
                if (image == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    private void downloadimage()
    {
        //final StorageReference riversRef = mStorageRef.child("picsss/"+ filename);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://chatbot-8a0e4.appspot.com").child("picsss/");

        List<FileDownloadTask> data= storageRef.getActiveDownloadTasks();
        Log.e("asd",""+data);

//        if(filepath != null) {

//            try {
//                final File localFile = File.createTempFile("images", "jpg");
//                storageRef.getActiveDownloadTasks()getFile(localFile)
//                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        // Local temp file has been created
//                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                        imageView.setImageBitmap(bitmap);
//                        Toast.makeText(getApplicationContext(),"file downloaded",Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle any errors
//                        Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_SHORT).show();
//                    }
//                });

//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

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
            String imageid=mFirebaseDatabase.push().getKey();
            uploadimage(filename,imageid);
        }
        if(view == download)
        {
            downloadimage();
        }


    }
}
