package edu.duke.compsci290.lostandfound;

import android.app.SearchManager;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mPostsRef = mDatabase.child("posts");
    FirebaseRecyclerAdapter<LostItem, ItemHolder> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("entering search act");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        RecyclerView rv = findViewById(R.id.activity_search_rv);
        rv.hasFixedSize();
        rv.setLayoutManager(new LinearLayoutManager(this));
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY).toLowerCase();
            // save the query to sugguestion
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            Query result = mPostsRef.orderByChild("titleToLower").equalTo(query); //find the titleToLower matching to the query
            FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<LostItem>().setQuery(result, LostItem.class).build();
            mAdapter = new FirebaseRecyclerAdapter<LostItem, ItemHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ItemHolder holder, final int position, @NonNull final LostItem model) {
                    holder.setTitle(model.getTitle());
                    holder.setDesc(model.getDescription());
                    holder.setContact(model.getContactInfo());
                    holder.setPlace(model.getPlace());
                    holder.setType(model.getType());
                    holder.setImage(model.getImage(), getApplicationContext());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {//open individual item view on click
                        @Override
                        public void onClick(View view) {
                            Log.d("position", ""+ position);
                            Intent intent = new Intent(SearchActivity.this, ItemViewActivity.class);
                            //pass necessary information to populate activity layout
                            intent.putExtra("title", model.getTitle());
                            intent.putExtra("type", model.getType());
                            intent.putExtra("place", model.getPlace());
                            intent.putExtra("contact", model.getContactInfo());
                            intent.putExtra("desc", model.getDescription());
                            intent.putExtra("image", model.getImage());
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.lost_item, parent, false);

                    return new ItemHolder(view);
                }
            };
        }
        rv.setAdapter(mAdapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();

        } else {
            //nothing matches 
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
    @Override
    public void onBackPressed(){
        //go back to main activity
        Intent myIntent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }
}

