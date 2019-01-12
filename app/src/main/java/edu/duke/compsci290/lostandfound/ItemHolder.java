package edu.duke.compsci290.lostandfound;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * Created by alexyang on 4/19/18.
 */

public class ItemHolder extends RecyclerView.ViewHolder {
    private TextView mDesc, mTitle, mContact, mType, mLocation;
    private ImageView mImage;


    public ItemHolder(View itemView) {//view is lost_item.xml
        super(itemView);
        mDesc = itemView.findViewById(R.id.item_desc);
        mTitle = itemView.findViewById(R.id.item_title);
        mContact = itemView.findViewById(R.id.item_contact_info);
        mType = itemView.findViewById(R.id.item_category);
        mImage = itemView.findViewById(R.id.item_image);
        mLocation = itemView.findViewById(R.id.item_location);
    }

    //setter methods for firebase UI
    public void setTitle(String t) {
        mTitle.setText(t);
    }
    public void setDesc(String d) { mDesc.setText(d); }
    public void setContact(String c) { mContact.setText(c); }
    public void setPlace(String p) { mLocation.setText(p);}
    public void setType(String c) { mType.setText(c); }
    public void setImage(String i, final Context context) {
        //firebase ui allows us to not download image to load; just need storage reference to image
        //i is the key to the image in firebase storage
        if (i == null) {//no image to display, prevent recyclerview from using wrong image on returning from itemview
            mImage.setImageDrawable(null);
            return;
        }
        //Log.d("image", "image key: " + i);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(i);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide
                        .with(context)
                        .load(imageURL)
                        .into(mImage);//load image using glide
            }
        })
            .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("rv image error", exception.toString());
            }
        });

        //GlideApp.with(context).load(ref).into(mImage);
        //Glide.with(context).using(new FirebaseImageLoader()).load(ref).into(mImage);//using won't work with glide 4

    }

}
