package com.magicbio.truename.db.contacts;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Ahmed Bilal on 12/20/2018.
 */

@Entity(tableName = "contacts")
public class Contact {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @Expose
    public String name;


    @Ignore
    public String number;
    @Expose
    public ArrayList<String> numbers;

    public ArrayList<String> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<String> numbers) {
        HashSet<String> hashSet = new HashSet<>(numbers);
        this.numbers = new ArrayList<>(hashSet);
    }


    //  @Column(name = "Image")
    public String Image;

    //@Column(name = "email")
    public String email;

    //@Column(name = "contactId")
    public String userid;


    public boolean showAd;

    public boolean areOptionsShown;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
