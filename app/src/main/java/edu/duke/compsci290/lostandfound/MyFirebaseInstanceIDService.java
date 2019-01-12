package edu.duke.compsci290.lostandfound;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{
    public void onTokenRefresh(){
        //get updated instanceID token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("DEBUG", "Refreshed token: " + refreshedToken);
        SendRegistrationToServer(refreshedToken);

        //to send messages to this app instance or manage app's subscriptions on server
        //send ID token to app server
        //sendRegistrationToServer(tokenUpdate);

        //calling getToken in context ensures accessing a current available registration token.
        //returns null if token not generated
    }

    private void SendRegistrationToServer(String token){
        //add custom info.
    }
}
