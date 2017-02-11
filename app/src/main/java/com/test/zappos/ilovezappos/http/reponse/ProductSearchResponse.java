package com.test.zappos.ilovezappos.http.reponse;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Parag Dakle on 6/3/16.
 *
 * Class is the Model class for Product Search Results response.
 */
public class ProductSearchResponse implements Parcelable {

    /*
     * Class default constructor.
     *
     * Initialize the search result entities.
     */
    public ProductSearchResponse() {
        statusCode = "";
        searchTerm = "";
        currentResultCount = "";
        totalResultCount = "";
        term = "";
        results = new JsonArray();
    }

    //Search result response status code.
    @SerializedName("statusCode")
    public String statusCode;

    //Original search term
    @SerializedName("originalTerm")
    public String searchTerm;

    //Count of items returned in the search response.
    @SerializedName("currentResultCount")
    public String currentResultCount;

    //Total number of result items for the search term.
    @SerializedName("totalResultCount")
    public String totalResultCount;

    //Secondary search term used, if any.
    @SerializedName("term")
    public String term;

    //Search results.
    @SerializedName("results")
    public JsonArray results;

    /*
     * Parceleable parameterized constructor.
     */
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

}
