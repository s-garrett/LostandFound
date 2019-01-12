package edu.duke.compsci290.lostandfound;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.File;

import static android.gesture.GestureLibraries.fromFile;

public class SignupActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button mLoginBtn;
    private Button mSignUpbtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase mAuth instance
        mAuth = FirebaseAuth.getInstance();

        mLoginBtn = findViewById(R.id.log_in_button);
        mSignUpbtn = findViewById(R.id.register_button);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            //go to the login page if the user says they have an account
            public void onClick(View v) {
                Intent myIntent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(myIntent);
                finish();
            }
        });

        mSignUpbtn.setOnClickListener(new View.OnClickListener() {
            //register the user in firebase mAuth
            @Override
            public void onClick(View v) {

                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                //make sure email and password are not blank
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Email can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Password can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 4) {
                    Toast.makeText(getApplicationContext(), "Password should be at least 4 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                //create a new user in firebase auth
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "You have been registered!", Toast.LENGTH_SHORT).show();
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Registration failed :/" + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    //set email to default displayname;
                                    UserProfileChangeRequest.Builder ProfileBuilder = new UserProfileChangeRequest.Builder();
                                    ProfileBuilder.setDisplayName(email);
                                    mAuth.getCurrentUser().updateProfile(ProfileBuilder.build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("DEBUG", ("updated display name: " +mAuth.getCurrentUser().getDisplayName()));
                                        }
                                    });
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
