package com.magicbio.truename.db.contacts;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.magicbio.truename.adapters.DynamicSearchAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Ahmed Bilal on 12/20/2018.
 */

@Entity(tableName = "contacts")
public class Contact implements DynamicSearchAdapter.Searchable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @Expose
    public String name;

    @Expose
    @Ignore
    public String number;

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
    @Expose
    public String userid;


    public boolean showAd;

    public boolean areOptionsShown;

    public Contact() {
        super();
    }

    /*public static List<Contact> getAll() {
        return new Select()
                .all()
                .from(Contact.class)
                .execute();
    } */


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