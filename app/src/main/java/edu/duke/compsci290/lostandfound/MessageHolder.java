package edu.duke.compsci290.lostandfound;

import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MessageHolder extends RecyclerView.ViewHolder{
    private TextView mMessage, mDate, mUsername;

    public MessageHolder(View itemView) {
        super(itemView);
        //populate fields in the message_template
        mMessage = itemView.findViewById(R.id.message_body);
        mUsername = itemView.findViewById(R.id.user);
        mDate = itemView.findViewById(R.id.message_time);
    }

    public void setMessage(String message){ mMessage.setText(message); }
    public void setUsername(String user) {mUsername.setText(user);}
    public void setDate(String date){mDate.setText(date);
    }

}
