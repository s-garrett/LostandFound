package edu.duke.compsci290.lostandfound;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



public class ItemViewActivity extends AppCompatActivity {

    private TextView mDesc, mTitle, mContact, mType, mPlace, mStatus, mDate;
    private ImageView mImage;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_view);
        mDesc = findViewById(R.id.item_view_desc);
        mTitle = findViewById(R.id.item_view_title);
        mContact = findViewById(R.id.item_view_contact);
        mType = findViewById(R.id.item_view_category);
        mPlace = findViewById(R.id.item_view_place);
        mImage = findViewById(R.id.item_view_image);
        mDate = findViewById(R.id.item_view_time);
        mStatus = findViewById(R.id.item_view_status);

        Intent intent = getIntent();//get the intent passed from mainactivity

        mDesc.setText(intent.getStringExtra("desc"));
        mTitle.setText(intent.getStringExtra("title"));
        mContact.setText(intent.getStringExtra("contact"));
        mPlace.setText(intent.getStringExtra("place"));
        mType.setText(intent.getStringExtra("type"));
        mDate.setText(intent.getStringExtra("date"));
        Boolean isLost = intent.getBooleanExtra("status", false);
        if (isLost) {
            mStatus.setText("Lost");
        }
        else {
            mStatus.setText("Found");
        }
//        String img = intent.getStringExtra("image");
//        Glide.with(this).load(img).into(mImage);

        String img = intent.getStringExtra("image");//img is the key in storage that ties the image to a post in database
        Log.d("image", "image string: " + img);
        if ( img != null) {

            StorageReference ref = FirebaseStorage.getInstance().getReference().child(img);
            //loadimageusing glide
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageURL = uri.toString();
                    Glide.with(getApplicationContext()).load(imageURL).into(mImage);//load image usingglide
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("rv image error", exception.toString());
                }
            });
            Glide.with(getApplicationContext()).load(img).into(mImage);
        }
    }
}
