
package com.magicbio.truename.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    public static class Data {
        @Expose
        public String number, name, image, type, link;
        @SerializedName("is_spam")
        @Expose
        public String spamCount;
    }

}
