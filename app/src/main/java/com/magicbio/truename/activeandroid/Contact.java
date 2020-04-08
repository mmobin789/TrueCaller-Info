package com.magicbio.truename.activeandroid;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.magicbio.truename.adapters.DynamicSearchAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Ahmed Bilal on 12/20/2018.
 */

@Table(name = "Contacts")
public class Contact extends Model implements DynamicSearchAdapter.Searchable {
    @Column(name = "Name")
    @Expose
    public String name;

    @Column(name = "Number", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    @Expose
    public String number;


    @Column(name = "Image")
    public String Image;

    @Column(name = "email")
    public String email;

    @Column(name = "contactId")
    @Expose
    public String userid;


    public boolean showAd;

    public boolean areOptionsShown;

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

    private static boolean searchByName = true;

    public static void setSearchByNumber() {
        searchByName = false;
    }

    public static void setSearchByName() {
        searchByName = true;
    }

    @NotNull
    @Override
    public String getSearchCriteria() {
        if (searchByName && name != null)
            return name.toLowerCase();

        return number;
    }
}
