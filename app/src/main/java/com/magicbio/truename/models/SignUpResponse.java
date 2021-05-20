
package com.magicbio.truename.models;

import com.google.gson.annotations.Expose;

public class SignUpResponse {
    @Expose
    private boolean status;
    @Expose
    private Integer id;

    public boolean getStatus() {
        return status;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
