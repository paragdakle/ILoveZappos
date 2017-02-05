package com.test.zappos.ilovezappos.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.test.zappos.ilovezappos.R;
import com.test.zappos.ilovezappos.common.Constants;
import com.test.zappos.ilovezappos.common.DownloadCallback;
import com.test.zappos.ilovezappos.http.BaseHttpRequestAsyncTask;
import com.test.zappos.ilovezappos.http.HttpRequests;
import com.test.zappos.ilovezappos.http.reponse.ProductSearchResponse;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DownloadCallback, TextView.OnEditorActionListener {

    Button btnSearch;
    EditText searchTextEditText;
    FloatingActionButton btnViewCart;
    Snackbar errorSnackbar;
    CoordinatorLayout parentLayout;
    private ProductSearchResponse response;

    private final int SEARCH_COMPLETE = 0;
    private final int SEARCH_FAILED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSearch = (Button) findViewById(R.id.btnSearch);
        searchTextEditText = (EditText) findViewById(R.id.searchKeywordEditText);
        btnViewCart = (FloatingActionButton) findViewById(R.id.btnViewCart);
        parentLayout = (CoordinatorLayout) findViewById(R.id.parentCoordinatorLayout);

        btnSearch.setOnClickListener(this);
        btnViewCart.setOnClickListener(this);
        searchTextEditText.setOnEditorActionListener(this);

        response = new ProductSearchResponse();
    }

    /* Function validates the tag and searches for the image tag by starting the SearchAsyncTask.
     * @param tag The image tag to search
     *
     * @return void
     */
    private void search(String term) {
        if(validateSearchTerm()) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put(Constants.SEARCH_KEY_TERM, term);
            ProductSearchAsyncTask task = new ProductSearchAsyncTask(MainActivity.this, HttpRequests.SEARCH_PRODUCT, parameters, "Searching...");
            task.execute((Void) null);
        }
        else {
            showError(getResources().getString(R.string.error_no_search_term_message));
        }
    }

    /* Function validates the term to be searched.
     *
     * @return boolean Validates the search tag and returns true if successful else false.
     *
     * Note: Currently only basic validation is been done and it is assumed that any special
     * character can be part of the term being searched and thus not checked for.
     */
    private boolean validateSearchTerm() {
        return searchTextEditText != null
                && searchTextEditText.getText() != null
                && !searchTextEditText.getText().toString().isEmpty();
    }

    /*
     * Listener handles the Send/Go key press event for searching an image tag.
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            hideSoftKeyboard();
            search(searchTextEditText.getText().toString());
            handled = true;
        }
        return handled;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                search(searchTextEditText.getText().toString());
                break;
            case R.id.btnViewCart:
                break;
        }
    }

    @Override
    public void finishedDownloading(int operation) {
        switch (operation)  {
            case SEARCH_COMPLETE:
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                intent.putExtra("searchResults", response);
                startActivity(intent);
                break;
            case SEARCH_FAILED:
                showError(getResources().getString(R.string.error_no_products_found_message));
                break;
        }
    }

    @Override
    public boolean checkNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /* Function hides the soft keyboard.
     *
     * @return void
     */
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /* Function displays an error message in a Snackbar.
     * @param errorMessage The error message to display
     *
     * @return void
     */
    private void showError(String errorMessage) {
        if(errorMessage != null && !errorMessage.isEmpty()) {
            errorSnackbar = Snackbar.make(parentLayout, errorMessage, BaseTransientBottomBar.LENGTH_LONG);
            errorSnackbar.show();
        }
    }

    private class ProductSearchAsyncTask extends BaseHttpRequestAsyncTask<ProductSearchResponse> {

        DownloadCallback mCallback;

        public ProductSearchAsyncTask(Context context, int apiMethod, Map<String, String> parameters, String displayMessage) {
            super(context, apiMethod, parameters, displayMessage);
            this.mCallback = (DownloadCallback) context;
        }

        @Override
        protected void onPostExecute(ProductSearchResponse searchResponse) {
            super.onPostExecute(searchResponse);
            if(searchResponse != null) {
                response = searchResponse;
                mCallback.finishedDownloading(SEARCH_COMPLETE);
            }
            else {
                mCallback.finishedDownloading(SEARCH_FAILED);
            }
        }
    }
}
