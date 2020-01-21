package com.magicbio.truename.models;

/**
 * Created by Ahmed Bilal on 12/17/2018.
 */

public class ContactModel {
    String Name;
    String Number;
    String Image;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }
}
