
package com.rezito.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class Order {

    @SerializedName("Data")
    private List<OrderDatum> mData;
    @SerializedName("ResponseCode")
    private String mResponseCode;
    @SerializedName("ResponseMsg")
    private String mResponseMsg;
    @SerializedName("Result")
    private String mResult;

    public List<OrderDatum> getData() {
        return mData;
    }

    public void setData(List<OrderDatum> data) {
        mData = data;
    }

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public String getResponseMsg() {
        return mResponseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        mResponseMsg = responseMsg;
    }

    public String getResult() {
        return mResult;
    }

    public void setResult(String result) {
        mResult = result;
    }

}
