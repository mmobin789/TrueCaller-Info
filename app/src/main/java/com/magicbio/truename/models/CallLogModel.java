package com.magicbio.truename.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Created by Ahmed Bilal on 12/14/2018.
 */
@Entity(tableName = "callLog")
public class CallLogModel {
    // @Column(name = "Name")
    String name;
    // @Column(name = "Number")
    String phNumber;
    //  @Column(name = "CallType")
    String callType;
    // @Column(name = "CallDate")
    String callDate;
    //  @Column(name = "CallDateTime")
    String callDayTime;
    // @Column(name = "CallDuration")
    String callDuration;
    //  @Column(name = "CallSim")
    String sim;

    // @Column(name = "image")
    String image;

    //@Column(name = "_id")
    @PrimaryKey
    public long id;  // this is call log id in the phone book.

    @Ignore
    public boolean areOptionsShown;
    @Ignore
    public boolean showAd;

    public boolean numberByTrueName;


    public CallLogModel() {
        super();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        return sim;
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

}
