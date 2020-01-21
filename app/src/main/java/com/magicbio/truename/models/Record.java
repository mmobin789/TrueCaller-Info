
package com.magicbio.truename.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Record {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Address")
    @Expose
    private String address;
    @SerializedName("cellno")
    @Expose
    private String cellno;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("invitation")
    @Expose
    private String invitation;


    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCellno() {
        return cellno;
    }

    public void setCellno(String cellno) {
        this.cellno = cellno;
    }

    public String getSource() {
        if (source == null || source.equals("`users`")) {
            return getNtwork(cellno);
        } else {
            return source;
        }
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getInvitation() {
        return invitation;
    }

    public void setInvitation(String invitation) {
        this.invitation = invitation;
    }


    public String getNtwork(String number) {
        String network = "unknown";
        if (number.startsWith("9230")) {
            network = "jazz";
        } else if (number.startsWith("9232")) {
            network = "warid";
        } else if (number.startsWith("9233")) {
            network = "ufon";
        } else if (number.startsWith("9231")) {
            network = "zong";
        }
        return network;
    }

}
