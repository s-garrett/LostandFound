package edu.duke.compsci290.lostandfound;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {
    private String mMessageText;
    private String mMessageUser;
    private String mMessageTime;

    public ChatMessage(String messageText, String messageUser){
        this.mMessageText = messageText;
        this.mMessageUser = messageUser;
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyy hh:mm:ss", Locale.US);
        Date time = Calendar.getInstance().getTime();
        this.mMessageTime = "Posted: " + formatter.format(time);
    }

    public ChatMessage(){

    }
    //Use these to acquire the information needed to fill the messageActivity recycler.

    public String getMessageText(){
        return mMessageText;
    }
    public void setMessageText(String message){ this.mMessageText = message; }

    public String getMessageUser(){ return mMessageUser;}
    public void setMessageUser(String user){ this.mMessageUser = user;}

    public String getMessageTime(){ return mMessageTime;}
    public void setMessageTime(String messageTime){ this.mMessageTime = messageTime;}

}
