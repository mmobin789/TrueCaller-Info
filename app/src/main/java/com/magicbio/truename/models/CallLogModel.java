package com.magicbio.truename.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.magicbio.truename.adapters.DynamicSearchAdapter;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Ahmed Bilal on 12/14/2018.
 */
@Table(name = "CallLog")
public class CallLogModel extends Model implements DynamicSearchAdapter.Searchable {

    @Column(name = "Name")
    String name;
    @Column(name = "Number")
    String phNumber;
    @Column(name = "CallType")
    String callType;
    @Column(name = "CallDate")
    String callDate;
    @Column(name = "CallDateTime")
    String callDayTime;
    @Column(name = "CallDuration")
    String callDuration;
    @Column(name = "CallSim")
    String sim;

    @Column(name = "image")
    String image;

    @Column(name = "_id")
    String _id;

    public boolean areOptionsShown;

    public boolean showAd;


    public CallLogModel() {
        super();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String get_Id() {
        return _id;
    }

    public void set_Id(String id) {
        this._id = id;
    }

    public String getName() {
        if (name == null) {
            return phNumber;
        } else {
            return name;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSim() {
        if (sim == null) {
            return "";
        } else {
            return sim;
        }
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getPhNumber() {
        return phNumber;
    }

    public void setPhNumber(String phNumber) {
        this.phNumber = phNumber;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallDate() {
        if (callDate == null)
            return "";
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public String getCallDayTime() {
        return callDayTime;
    }

    public void setCallDayTime(String callDayTime) {
        this.callDayTime = callDayTime;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
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

        return phNumber;
    }
}
