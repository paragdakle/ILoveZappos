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
 * Created by Parag Dakle on 7/3/16.
 *
 * Generic template class for performing all HTTP requests.
 *
 * @param T Response Class
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

    /* Function executes an HTTP request by calling the appropriate method.
     *
     * @param method HTTP API identifier
     * @param parameters Map of all the parameters needed to consume the HTTP API.
     *
     * @return T Response class object is successful else null.
     */
    public T execute(int method, Map<String, String> parameters) {
        switch (method) {
            case SEARCH_PRODUCT:
                return (T) this.search(parameters);

        }
        return null;
    }

    /* Function executes an HTTPS request to execute an product search using the given parameters.
     *
     * @param parameters Map of all the search parameters.
     *
     * @return response ProductSearchResponse object containing all the search results.
     */
    private ProductSearchResponse search(Map<String, String> parameters) {

        ProductSearchResponse response = null;

        String searchTerm;

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
                        //If request execution failed, retry once.
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
