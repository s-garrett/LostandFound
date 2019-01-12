package edu.duke.compsci290.lostandfound;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mPostsRef = mDatabase.child("posts");
    private DataSnapshot mDataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Attach a listener to read the data at our posts reference
        mPostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        //TODO: move camera to current location
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(36.0026, -78.9216))
                .zoom(15)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setOnInfoWindowClickListener(this);


    }

    private void setMarker(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds:dataSnapshot.getChildren()){
            try{
                mDataSnapshot = ds;
                double lat = ds.getValue(LostItem.class).getLat();
                double lng = ds.getValue(LostItem.class).getLng();
                String title = ds.getValue(LostItem.class).getTitle();
                String nameToLower = ds.getValue(LostItem.class).getTitleToLower();
                String desc = ds.getValue(LostItem.class).getDescription();
                String imageId = ds.getValue(LostItem.class).getImage();
                String date = ds.getValue(LostItem.class).getDate();
                String contact = ds.getValue(LostItem.class).getContactInfo();
                String placeName = ds.getValue(LostItem.class).getPlace();
                String type = ds.getValue(LostItem.class).getType();
                Boolean status = ds.getValue(LostItem.class).getIsLost();


                LostItem lostitem = new LostItem(status, title, nameToLower, contact, desc, placeName, type, lat, lng, date, imageId);

                MapInfoWindowAdapter infoWindowAdapter = new MapInfoWindowAdapter(this);
                mMap.setInfoWindowAdapter(infoWindowAdapter);

                LatLng loc = new LatLng(lat, lng);
                Marker marker = mMap.addMarker(new MarkerOptions().position(loc)
                        .title(title)
                        .snippet(imageId));
                marker.setTag(lostitem);
                if (status){
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                }

            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked", Toast.LENGTH_SHORT).show();

        DatabaseReference ref = mPostsRef.child(marker.getSnippet());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Intent intent = new Intent(MapsActivity.this, ItemViewActivity.class);
                    LostItem item = dataSnapshot.getValue(LostItem.class);
                    intent.putExtra("title", item.getTitle());
                    intent.putExtra("image", item.getImage());
                    intent.putExtra("contact", item.getContactInfo());
                    intent.putExtra("type", item.getType());
                    intent.putExtra("place", item.getPlace());
                    intent.putExtra("desc", item.getDescription());
                    intent.putExtra("date", item.getDate());
                    intent.putExtra("status", item.getIsLost());

                    startActivity(intent);
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }
}
