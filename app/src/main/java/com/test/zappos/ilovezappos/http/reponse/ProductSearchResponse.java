package com.test.zappos.ilovezappos.http.reponse;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 6/3/16.
 */
public class ProductSearchResponse implements Parcelable {

    @SerializedName("statusCode")
    public String statusCode;

    @SerializedName("originalTerm")
    public String searchTerm;

    @SerializedName("currentResultCount")
    public String currentResultCount;

    @SerializedName("totalResultCount")
    public String totalResultCount;

    @SerializedName("term")
    public String term;

    @SerializedName("results")
    public JsonArray results;

    public ProductSearchResponse() {}

    protected ProductSearchResponse(Parcel in) {
        statusCode = in.readString();
        searchTerm = in.readString();
        currentResultCount = in.readString();
        totalResultCount = in.readString();
        term = in.readString();
        JsonParser parser = new JsonParser();
        results = parser.parse(in.readString()).getAsJsonArray();
    }

    public static final Creator<ProductSearchResponse> CREATOR = new Creator<ProductSearchResponse>() {
        @Override
        public ProductSearchResponse createFromParcel(Parcel in) {
            return new ProductSearchResponse(in);
        }

        @Override
        public ProductSearchResponse[] newArray(int size) {
            return new ProductSearchResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(statusCode);
        dest.writeString(searchTerm);
        dest.writeString(currentResultCount);
        dest.writeString(totalResultCount);
        dest.writeString(term);
        dest.writeString(results.toString());
    }

    /*@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Search Term: ")
                .append(searchTerm)
                .append(", Current Result Count: ")
                .append(currentResultCount)
                .append(", Total Result Count: ")
                .append(totalResultCount)
                .append(", Term: ")
                .append(term)
                .append(", Results: ")
                .append(results.toString());
        return builder.toString();
    }*/

}
