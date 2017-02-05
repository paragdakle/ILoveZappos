package com.test.zappos.ilovezappos.http;

import android.text.TextUtils;

import com.test.zappos.ilovezappos.common.Constants;
import com.test.zappos.ilovezappos.http.reponse.ProductSearchResponse;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by root on 7/3/16.
 */
public class HttpRequests<T> {

    /*
     * API Name Identifiers
     */
    public static final int SEARCH_PRODUCT = 0;

    /*
     * HTTP Status Codes
     */

    private int REQUEST_EXECUTED_SUCCESSFULLY = 200;

    private boolean isRetryRequired = true;

    private interface SearchProductsService {
        @GET("/Search")
        Call<ProductSearchResponse> search(@Query("term") String term, @Query("key") String key);
    }

    public T execute(int method, Map<String, String> parameters) {
        switch (method) {
            case SEARCH_PRODUCT:
                return (T) this.search(parameters);

        }
        return null;
    }

    private ProductSearchResponse search(Map<String, String> parameters) {

        ProductSearchResponse response = null;

        String searchTerm = "";

        if(parameters != null && parameters.size() > 0 && parameters.containsKey(Constants.SEARCH_KEY_TERM)) {

            searchTerm = parameters.get(Constants.SEARCH_KEY_TERM);

            if (!TextUtils.isEmpty(searchTerm)) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                SearchProductsService getRhymesService = retrofit.create(SearchProductsService.class);

                Call<ProductSearchResponse> call = getRhymesService.search(searchTerm, Constants.API_KEY);

                try {
                    response = call.execute().body();
                    if (Integer.parseInt(response.statusCode) != REQUEST_EXECUTED_SUCCESSFULLY && this.isRetryRequired) {
                        this.isRetryRequired = false;
                        response = call.clone().execute().body();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }
}
