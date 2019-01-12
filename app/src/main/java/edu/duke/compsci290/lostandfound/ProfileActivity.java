package edu.duke.compsci290.lostandfound;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

/**
 * Created by fleur on 4/3/18.
 */

public class ProfileActivity extends AppCompatActivity {
    private Button mChangeName;
    private Button mProfPic;
    private ImageView mPic;
    private EditText mEditUser;
    private TextView mMessageNoti;
    private Switch mMessageNotiToggle;
    private StorageReference mStorageRef;
    private FirebaseUser mUser;
    private boolean mCheck;
    private SharedPreferences sharedPref;
    public final static int mPICK_PHOTO_CODE = 1046;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("debug", "STARTED PROFILE ACTIVITY");
        setContentView(R.layout.activity_profile);

        sharedPref = getApplicationContext().getSharedPreferences("Notifications", 0);


        mStorageRef = FirebaseStorage.getInstance().getReference();
        mChangeName = findViewById(R.id.user_name);
        mProfPic = findViewById(R.id.profile_pic);
        mPic = findViewById(R.id.tempImage);
        mEditUser = findViewById(R.id.user_edit);
        mMessageNoti = findViewById(R.id.message_noti);
        mMessageNotiToggle = findViewById(R.id.messageNotiToggle);

        mCheck = sharedPref.getBoolean("Message_Toggle", false);
        if(sharedPref.contains("Message_Toggle")) {
            if (mCheck){mMessageNotiToggle.setChecked(true);}
            else{mMessageNotiToggle.setChecked(false);}
        }
        else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("Message_Toggle", false);
            mMessageNotiToggle.setChecked(false);
        }

            //Access current user data stored on firebase
            mUser = FirebaseAuth.getInstance().getCurrentUser();


            //Check if user has a profile picture. If not use default.
            if (mUser.getPhotoUrl() == null) {
                Drawable myDrawable = this.getDrawable(R.drawable.bird);
                mPic.setImageDrawable(myDrawable);
            } else {
                StorageReference pictureRef = mStorageRef.child("ProfilePictures").child(mUser.getUid());
                pictureRef.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String URL = uri.toString();
                                Log.d("DEBUG", "URL DOwnloaded: " + URL);
                                Glide.with(ProfileActivity.this).load(URL).into(mPic);
                            }
                        });

            }
            //sets a listener for the enter button to confirm username entry.
            mEditUser.setOnEditorActionListener(listener);

            //Button to change username
            mChangeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //launch username ChangeActivty here Gather data from database on username
                    mEditUser.setVisibility(View.VISIBLE);


                    //startActivity(new Intent(ProfileActivity.this, ));
                }
            });
            //button to change profile picture.
            mProfPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Launch Profile Pic ChangeActivity here Gather data from database on picture
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    //TEST IF STARTACTIVITY IS SAFE TO RUN
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, mPICK_PHOTO_CODE);
                    }
                }

            });

            mMessageNotiToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCheck){
                        mMessageNotiToggle.setChecked(false);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("messageNotification");
                    }
                    else{
                        mMessageNotiToggle.setChecked(true);
                        FirebaseMessaging.getInstance().subscribeToTopic("messageNotification");
                    }

                }
            });

        }

        //When coming back to this activty from the gallery.
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            ProfileActivity.super.onActivityResult(requestCode, resultCode, data);
            Log.d("DEBUG", "ONACTIVITY");
            if (requestCode == mPICK_PHOTO_CODE && resultCode == RESULT_OK) {
                Uri photoUri = data.getData();
                Log.d("DEBUG", "PHOTOURI: " + photoUri);
                assert mUser != null;
                //choose where to store new image
                StorageReference profPic = mStorageRef.child("ProfilePictures").child(mUser.getUid());

                //store image.
                UserProfileChangeRequest.Builder ProfileBuilder = new UserProfileChangeRequest.Builder();
                ProfileBuilder.setPhotoUri(photoUri);
                mUser.updateProfile(ProfileBuilder.build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProfileActivity.this, "UPDATED PROFILE PICTURE", Toast.LENGTH_SHORT).show();
                    }
                });

                //Set new image
                try {
                    Log.d("DEBUG", "ENTERING TRY STATEMENT");

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                    mPic.setImageBitmap(bitmap);
                    assert photoUri != null;
                    profPic.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ProfileActivity.this, "UPLOAD COMPLETE", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void onStop(){
            super.onStop();
            SharedPreferences.Editor editor = sharedPref.edit();
            if(mMessageNotiToggle.isChecked()){
                editor.putBoolean("Message_Toggle", true);
                editor.apply();
            }
            else {
                editor.putBoolean("Message_Toggle", false);
                editor.apply();
            }
        }

        @Override
        public void onPause(){
            super.onPause();
            SharedPreferences.Editor editor = sharedPref.edit();
            if(mMessageNotiToggle.isChecked()){
                editor.putBoolean("Message_Toggle", true);
                editor.apply();
            }
            else {
                editor.putBoolean("Message_Toggle", false);
                editor.apply();
            }
        }

        TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    final String displayName = mEditUser.getText().toString().trim();
                    Log.d("DEBUG", "DISPLAYNAME: " + displayName);
                    UserProfileChangeRequest.Builder ProfileBuilder = new UserProfileChangeRequest.Builder();
                    ProfileBuilder.setDisplayName(displayName);
                    mUser.updateProfile(ProfileBuilder.build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mEditUser.setVisibility(View.GONE);
                            Log.d("DEBUG", ("updated display name: " + mUser.getDisplayName()));
                        }
                    });
                }
                return true;
            }
        };
}
