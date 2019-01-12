package edu.duke.compsci290.lostandfound;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private TextView mText;
    private FloatingActionButton mNewBtn;
    private RecyclerView mItemsList;
    FirebaseRecyclerAdapter<LostItem, ItemHolder> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("DEBUG", "MainActivity.onCreate: " + FirebaseInstanceId.getInstance().getToken());
        mText = findViewById(R.id.mainText);
        mNewBtn = findViewById(R.id.new_item);
        mItemsList = findViewById(R.id.items_recycler_view);

        mItemsList.hasFixedSize();
        mItemsList.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("posts");  //ref is the parent of relevant data (root in this case)
        Log.d("ref", "hello" + ref.toString());
        Query query = ref.orderByKey();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<LostItem>().setQuery(query, LostItem.class).build();
        mAdapter = new FirebaseRecyclerAdapter<LostItem, ItemHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemHolder holder, final int position, @NonNull final LostItem model) {
                holder.setTitle(model.getTitle());

                holder.setDesc(model.getDate());
                holder.setContact(model.getContactInfo());
                holder.setPlace(model.getPlace());
                holder.setType(model.getType());
                holder.setImage(model.getImage(), getApplicationContext());

                holder.itemView.setOnClickListener(new View.OnClickListener() {//open individual item view on click
                    @Override
                    public void onClick(View view) {
                        Log.d("position", ""+position);
                        Intent intent = new Intent(getApplicationContext(), ItemViewActivity.class);
                        //pass necessary information to populate activity layout
                        intent.putExtra("title", model.getTitle());
                        intent.putExtra("type", model.getType());
                        intent.putExtra("place", model.getPlace());
                        intent.putExtra("contact", model.getContactInfo());
                        intent.putExtra("desc", model.getDescription());
                        intent.putExtra("image", model.getImage());
                        intent.putExtra("date", model.getDate());
                        intent.putExtra("status", model.getIsLost());
                        Log.d("image", "putting image string: " + model.getImage());
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
        mItemsList.setAdapter(mAdapter);
        //this itemtouchhelper used to remove items from firebase and recyclerview if swiped away
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {//user swipes left on viewholder in recyclerview
                    //use alert dialog to ensure deletion is not accidental
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Has this item been returned?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() { //make sure swipe isn't an accident
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAdapter.notifyItemRemoved(position);    //item removed from recylcerview
                            //DELETE FROM FIREBASE HERE
                            mAdapter.getRef(position).setValue(null);

                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {  //give them option to not remove the item
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAdapter.notifyItemRemoved(position + 1);    //still need to notify of change; removes then returns item, otherwise we get a blank viewholder
                            mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                        }
                    }).show();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mItemsList); //attach it to the recyclerview

        if (user != null) {
            //display user info
            String name = user.getDisplayName();
            String email = user.getEmail();

            mText.setText(email + " is logged in.");
        }

        mNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, SubmitLostActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {mAdapter.notifyDataSetChanged();}//redraw adapter so images are bound correctly
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {mAdapter.notifyDataSetChanged();}
        mAdapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//create option to sign out via three dots
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        initSearchWidget(menu);
        return true;
    }

    private void initSearchWidget(Menu menu) {
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // start the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
        searchView.setIconifiedByDefault(false);// Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("query to submit", query);
                Intent myIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(myIntent);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        //check what menu item was selected
        switch (item.getItemId()) {
            case R.id.sign_out:
                Log.d("debug", "signing out");
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                //launch the login page
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;

            case R.id.clear_search_history:
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                        SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
                suggestions.clearHistory();
                return true;

            case R.id.user_profile:
                Log.d("debug", "launching profile menu");
                //launch intent to profile page where we access profile information from database?
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;

            case R.id.open_messages:
                Log.d("debug", "Opening Messages Screen");
                Intent intent2 = new Intent(this, MessagingActivity.class);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //When Map Button is clicked on, switch to map view
    public void viewMap(View view) {
        startActivity(new Intent(MainActivity.this, MapsActivity.class));
    }
}
