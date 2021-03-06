package com.magicbio.truename.models;

import com.google.gson.annotations.Expose;
import com.magicbio.truename.adapters.DynamicSearchAdapter;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Ahmed Bilal on 12/27/2018.
 */

public class Sms implements DynamicSearchAdapter.Searchable {
    @Expose
    private String userid;

    private String address;

    public String getName() {
        return name;
    }

    @Expose
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @Expose
    private String txtmessage;

    private String _readState; //"0" for have not read sms and "1" for have read sms
    private String _time;
    private String _folderName;
    private String _sim;
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
        //   if (!searchByName || contact == null)
            return name;
        // sms sender name not available for now
        // return contact.getName().toLowerCase();
    }

    public String getId() {
        return userid;
    }

    public void setId(String id) {
        userid = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMsg() {
        return txtmessage;
    }

    public void setMsg(String msg) {
        txtmessage = msg;
    }

    public String getReadState() {
        return _readState;
    }

    public void setReadState(String readState) {
        _readState = readState;
    }

    public String getTime() {
        return _time;
    }

    public void setTime(String time) {
        _time = time;
    }

    public String getFolderName() {
        return _folderName;
    }

    public void setFolderName(String folderName) {
        _folderName = folderName;
    }

    public String getSim() {
        if (_sim == null) {
            return "";
        } else {
            return _sim;
        }
    }

    public void setSim(String _sim) {
        this._sim = _sim;
    }
}
