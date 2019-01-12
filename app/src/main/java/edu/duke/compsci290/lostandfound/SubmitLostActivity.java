package edu.duke.compsci290.lostandfound;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.location.places.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class SubmitLostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener, DatePickerDialog.OnDateSetListener{
    private Button mSubmitLostBtn, mGetLocationBtn, mGetDateBtn, mCameraBtn, mAlbumBtn, mLostBtn, mFoundBtn;
    private EditText mItemName, mDescription, mContactInfo;
    private TextView mConfirmLocationText, mConfirmDateText;
    private ImageView mImageView;
    private Spinner mTypeOptions;
    private String mType;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mPostsRef = mDatabase.child("posts");
    private String mPlaceName;
    private double mLng;
    private double mLat;
    private Boolean mStatus;
    private String mCurrentDate;
    private FirebaseUser mUser;
    private StorageReference mStorageRef;
    private Uri mImageURI;

    private final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
    private final int OPEN_CAMERA=0;
    private final int PICK_GALLERY=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_lost);
        mItemName = findViewById(R.id.enter_item_title);
        mContactInfo = findViewById(R.id.enter_contact_email);
        mDescription = findViewById(R.id.enter_item_description);
        mSubmitLostBtn = findViewById(R.id.submit_item_btn);
        mGetLocationBtn = findViewById(R.id.get_location_btn);
        mConfirmLocationText = findViewById(R.id.confirm_location_text);
        mGetDateBtn = findViewById(R.id.get_date_btn);
        mConfirmDateText = findViewById(R.id.confirm_date_text);
        mImageView=findViewById(R.id.image_view);
        mCameraBtn=findViewById(R.id.camera_btn);
        mAlbumBtn=findViewById(R.id.album_btn);
        mLostBtn= findViewById(R.id.lost_btn);
        mFoundBtn= findViewById(R.id.found_btn);


        //set the dropdown spinner for selecting types of items
        mTypeOptions = findViewById(R.id.item_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lost_item_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mTypeOptions.setAdapter(adapter);
        mTypeOptions.setOnItemSelectedListener(this);

        //set the onclick listeners for the buttons
        mGetLocationBtn.setOnClickListener(this);
        mGetDateBtn.setOnClickListener(this);
        mSubmitLostBtn.setOnClickListener(this);
        mCameraBtn.setOnClickListener(this);
        mAlbumBtn.setOnClickListener(this);


        mLostBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mLostBtn.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.colorAccent)));
                    mFoundBtn.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.input_register_hint)));
                    mStatus = TRUE;
                }
                return false;
            }

        });

        mFoundBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {

                    mFoundBtn.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.colorAccent)));
                    mLostBtn.setBackgroundTintList(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.input_register_hint)));
                    mStatus = FALSE;
                }
                return false;
            }

        });

        //set ConfirmDateText to current date
        Calendar c = Calendar.getInstance();
        mCurrentDate = formatter.format(c.getTime());
        mConfirmDateText.setText(mCurrentDate);

        //Access user Information and set storage reference for images.
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //TODO: set ConfirmLocationText to current location

    }

    @Override
    public void onClick(View view){
        switch(view.getId()) {
            case R.id.submit_item_btn: //get the Strings/values to push to firebase
                String name = mItemName.getText().toString().trim();
                String nameToLower = name.toLowerCase();
                String desc = mDescription.getText().toString().trim();
                String contact = mContactInfo.getText().toString().trim();
                if (name.equals("") || desc.equals("") || contact.equals("") || mStatus==null) {
                    //make sure none of the inputs are blank
                    Toast toast = Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                //create a new item from the user inputs
                DatabaseReference newItemRef = mPostsRef.push();
                String imageKey = newItemRef.getKey();//the string to associate the image with the database key
                LostItem item = new LostItem(mStatus, name, nameToLower, contact, desc, mPlaceName, mType, mLat, mLng, mCurrentDate.toString(), imageKey);
                Log.d("item created", item.toString() );
                //push to firebase by creating a new node under the "posts" section and then pushing this item to it.
                newItemRef.setValue(item);
                //Push image matching to this post to Firebase Storage
                StorageReference uploadLostImage = mStorageRef.child(newItemRef.getKey());
                uploadLostImage.putFile(mImageURI)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("DEBUG", "UPLOADED TO STORAGE");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("DEBUG", "FAILED TO PUSH IMAGE TO FIREBASE");
                            }
                        });
                //go back to main activity after submission
                Intent myIntent = new Intent(SubmitLostActivity.this, MainActivity.class);
                startActivity(myIntent);
                finish();
                break;
            case R.id.get_location_btn:
                launchPlacePicker();
                break;
            case R.id.get_date_btn:
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                break;
            case R.id.camera_btn:
                File image = null;
                try{
                    image = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (image != null){
                    mImageURI = FileProvider.getUriForFile(this, "edu.duke.compsci290.lostandfound.provider", image);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageURI);
                    startActivityForResult(intent, OPEN_CAMERA);
                }
                break;
            case R.id.album_btn:
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), PICK_GALLERY);
                break;
        }

    }

    public void launchPlacePicker() {
        Log.d("debug", "LAUNCH PLACE PICKER BEGIN");
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            Log.d("DEBUG", "TRY TO START ACTIVITY");
            startActivityForResult(builder.build(SubmitLostActivity.this), 1);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //callback for placepicker after user has selected a place
        SubmitLostActivity.super.onActivityResult(requestCode, resultCode, data);
        Log.d("DEBUG", "ONRESULT RETURN");
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place selectedPlace = PlacePicker.getPlace(data, this);
                Log.d("place", selectedPlace.getName().toString());
                mPlaceName = selectedPlace.getName().toString();
                mLat = selectedPlace.getLatLng().latitude;
                mLng = selectedPlace.getLatLng().longitude;
                Log.d("coordinate", String.valueOf(mLat)+" "+ String.valueOf(mLng));
                mConfirmLocationText.setText(""+mPlaceName);
            }
        }else if(requestCode == OPEN_CAMERA && resultCode == RESULT_OK){
            Log.d("DEBUG", "IMAGE URI ON RESULT: " + mImageURI);
            mImageView.setImageURI(mImageURI);
        }else if(requestCode == PICK_GALLERY && resultCode == RESULT_OK){
            mImageURI = data.getData();
            Log.d("DEBUG", "IMAGEURI: " + mImageURI);
            //choose where to store new image
            mImageView.setImageURI(mImageURI);
        }
    }


    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        String selectedType = parent.getItemAtPosition(position).toString();
        Log.d("selected type is ", selectedType);
        mType = selectedType;
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        Toast.makeText(getApplicationContext(), "Empty selection.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month+1;
        mCurrentDate = month+"/"+dayOfMonth+"/"+year;
        mConfirmDateText.setText(mCurrentDate);

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }
}
