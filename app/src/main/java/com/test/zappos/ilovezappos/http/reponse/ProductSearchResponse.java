package com.test.zappos.ilovezappos.http.reponse;


import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 6/3/16.
 */
public class ProductSearchResponse {

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

}
