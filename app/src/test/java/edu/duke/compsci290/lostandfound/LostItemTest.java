package edu.duke.compsci290.lostandfound;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alexyang on 4/28/18.
 */

public class LostItemTest {
    private final LostItem mLost = new LostItem("thing", "thing", "a@b.com", "ugly thing", "duke", "Personal Belonging", 36.00, 36.00, "4/2/18", "url");

    @Test
    public void getContactTest() throws Exception {
        assertEquals(mLost.getContactInfo(), "a@b.com");
    }
    @Test
    public void setContactTest() throws Exception {
        mLost.setContactInfo("b@c.com");
        assertEquals(mLost.getContactInfo(), "b@c.com");
    }
    @Test
    public void getTitleTest() throws Exception {
        assertEquals(mLost.getTitle(), "thing");
    }
    @Test
    public void setTitleTest() throws Exception {
        mLost.setTitle("new thing");
        assertEquals(mLost.getTitle(), "new thing");
    }
    @Test
    public void getDescTest() throws Exception {
        assertEquals(mLost.getDescription(), "ugly thing");
    }
    @Test
    public void setDescTest() throws Exception {
        mLost.setDescription("super ugly");
        assertEquals(mLost.getDescription(), "super ugly");
    }
    @Test
    public void getPlaceTest() throws Exception {
        assertEquals(mLost.getPlace(), "duke");
    }
    @Test
    public void setPlaceTest() throws Exception {
        mLost.setPlace("unc");
        assertEquals(mLost.getPlace(), "unc");
    }
    @Test
    public void getTypeTest() throws Exception {
        assertEquals(mLost.getType(), "Personal Belonging");
    }
    @Test
    public void setTypeTest() throws Exception {
        mLost.setType("hurr");
        assertEquals(mLost.getType(), "hurr");
    }
    @Test
    public void getLongTest() throws Exception {
        assertTrue(mLost.getLng()==36.00);
    }
    @Test
    public void setLongTest() throws Exception {
        mLost.setLng(37.00);
        assertTrue(mLost.getLng()==37.00);
    }
    @Test
    public void getLatTest() throws Exception {
        assertTrue(mLost.getLat()==36.00);
    }
    @Test
    public void setLatTest() throws Exception {
        mLost.setLat(37.00);
        assertTrue(mLost.getLat()==37.00);
    }
    @Test
    public void getDateTest() throws Exception {
        assertEquals(mLost.getDate(), "4/2/18");
    }
    @Test
    public void setDateTest() throws Exception {
        mLost.setDate("4/4/18");
        assertEquals(mLost.getDate(), "4/4/18");
    }
    @Test
    public void getImgTest() throws Exception {
        assertEquals(mLost.getImage(), "url");
    }
    @Test
    public void setImgTest() throws Exception {
        mLost.setImage("www");
        assertEquals(mLost.getImage(), "www");
    }
    @Test
    public void getTitleLowerTest() throws Exception {
        assertEquals(mLost.getTitleToLower(), "thing");
    }
    @Test
    public void setTitleLowerTest() throws Exception {
        mLost.setTitleToLower("super ugly");
        assertEquals(mLost.getTitleToLower(), "super ugly");
    }
}
