package edu.duke.compsci290.lostandfound;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by yuqiyun on 4/23/18.
 */

public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;
    private ImageView mImage;
    private TextView mTitle, mDate,mStatus, mContact;


    public MapInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.map_info_window, null);
    }

    public void setImage(String i, final Context context) {
        //i is the key to the image in firebase storage
        if (i == null) {
            mImage.setImageDrawable(null);
            return;
        }
        Log.d("load image", "image key: " + i);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(i);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(context).load(imageURL).into(mImage);//load image using glide
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("rv image error", exception.toString());
            }
        });
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker){
        mTitle = mWindow.findViewById(R.id.info_title);
        mDate = mWindow.findViewById(R.id.info_additional);
        mImage = mWindow.findViewById(R.id.info_image);
        mContact = mWindow.findViewById(R.id.info_contact);
        mStatus = mWindow.findViewById(R.id.info_status);

        mTitle.setText(marker.getTitle());


        LostItem info = (LostItem) marker.getTag();

        setImage(info.getImage(), mContext);
        mContact.setText(info.getContactInfo());
        mDate.setText(info.getDate());

        Boolean status = info.getIsLost();
        if (status){
            mStatus.setText("LOST");
        }else{
            mStatus.setText("FOUND");
        }

        return mWindow;
    }
}
