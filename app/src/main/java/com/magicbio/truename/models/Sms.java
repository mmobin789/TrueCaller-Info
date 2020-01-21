package com.magicbio.truename.models;

/**
 * Created by Ahmed Bilal on 12/27/2018.
 */

public class Sms {
    private String _id;
    private String _address;
    private String _msg;
    private String _readState; //"0" for have not read sms and "1" for have read sms
    private String _time;
    private String _folderName;
    private String _sim;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

    public String getAddress() {
        return _address;
    }

    public void setAddress(String address) {
        _address = address;
    }

    public String getMsg() {
        return _msg;
    }

    public void setMsg(String msg) {
        _msg = msg;
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
