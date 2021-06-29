
package com.magicbio.truename.models;

import com.google.gson.annotations.Expose;

public class GetNumberResponse {


    @Expose
    private boolean status;

    @Expose
    private Data data;

    public boolean getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public static class Data {
        @Expose
        public String number, name, image,type,link;
    }

}
