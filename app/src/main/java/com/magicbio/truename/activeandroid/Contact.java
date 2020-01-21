package com.magicbio.truename.activeandroid;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Ahmed Bilal on 12/20/2018.
 */

@Table(name = "Contacts")
public class Contact extends Model {
    @Column(name = "Name")
    public String name;

    @Column(name = "Number", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String number;


    @Column(name = "Image")
    public String Image;

    public Contact() {
        super();
    }

    public static List<Contact> getAll() {
        return new Select()
                .all()
                .from(Contact.class)
                .execute();
    }

    public static Contact getRandom(String Phone) {
        if (Phone == null) {
            Phone = "";
        }
        return new Select()
                .from(Contact.class)
                .where("Number = ?", Phone)
                .executeSingle();
    }

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
