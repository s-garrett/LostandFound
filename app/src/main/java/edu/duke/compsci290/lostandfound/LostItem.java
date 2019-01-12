package edu.duke.compsci290.lostandfound;

import static java.lang.Boolean.TRUE;

/**
 * Created by alexyang on 4/15/18.
 */

public class LostItem {
    private String mContactEmail;
    private String mTitle;
    private String mTitleToLower;
    private String mDescription;
    private String mPlaceName;
    private String mType;
    private String mImage;
    private double mLat;
    private double mLng;
    private String mDate;
    private Boolean mIsLost;

    public LostItem() {
        //default constructor required for firebase calls to snapshot.getvalue()
    }

    public LostItem(Boolean lost, String title, String titleToLower, String email,  String description, String place, String type, double latitude, double longitude, String date, String imageString) {
//        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        mContactEmail = email;
        mTitle = title;
        mTitleToLower = titleToLower;
        mDescription = description;
        mPlaceName = place;
        mType = type;
        mLat = latitude;
        mLng = longitude;
        mImage = imageString;
        mDate = date;
        mIsLost = lost;
    }


    public LostItem(String title, String titleToLower, String email,  String description, String place, String type, double latitude, double longitude, String date, String imageString) {
        this(TRUE, title, titleToLower, email, description, place, type, latitude, longitude, date, imageString);
    }

    //getters and setters needed for firebase
    public String getContactInfo() {return mContactEmail;}
    public void setContactInfo(String email) {mContactEmail = email;}

    public String getTitle() {return mTitle;}
    public void setTitle(String title) {mTitle = title;}

    public String getTitleToLower(){return mTitleToLower;}
    public void setTitleToLower(String titleToLower) {mTitleToLower = titleToLower;}

    public String getDescription() {return mDescription;}
    public void setDescription(String description){mDescription = description;}

    public String getPlace() {return mPlaceName;}
    public void setPlace(String place){mPlaceName = place;}

    public String getType() {return mType;}
    public void setType(String type) {mType = type;}
    public double getLat(){return mLat;}
    public void setLat(double latitude){mLat = latitude;}

    public double getLng(){return mLng;}
    public void setLng(double longitude){mLng = longitude;}


    public String getImage() { return mImage; }
    public void setImage(String image) { mImage = image; }

    public String getDate(){return mDate;}
    public void setDate(String date){mDate = date;}

    public Boolean getIsLost(){return mIsLost;}
    public void setIsLost(Boolean lost){mIsLost = lost;}

    public String toString(){
        return mContactEmail + mTitle + mDescription;
    }

}
