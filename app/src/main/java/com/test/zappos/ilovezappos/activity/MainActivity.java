package com.test.zappos.ilovezappos.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Parag Dakle.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DownloadCallback, TextView.OnEditorActionListener, RecognitionListener {

    Button btnSearch, btnCrazySearch;
    AVLoadingIndicatorView indicatorView;
    EditText searchTextEditText;
    TextView txtVoiceSearchInfo;
    Snackbar errorSnackbar;
    CoordinatorLayout parentLayout;
    FloatingActionButton btnTalk;
    private ProductSearchResponse response;

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    private final int SEARCH_COMPLETE = 0;
    private final int SEARCH_FAILED = 1;

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize UI component variables
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnCrazySearch = (Button) findViewById(R.id.btnCrazySearch);
        btnTalk = (FloatingActionButton) findViewById(R.id.startVoiceSearch);
        searchTextEditText = (EditText) findViewById(R.id.searchKeywordEditText);
        parentLayout = (CoordinatorLayout) findViewById(R.id.parentCoordinatorLayout);
        indicatorView = (AVLoadingIndicatorView) findViewById(R.id.voiceInputIndicator);
        txtVoiceSearchInfo = (TextView) findViewById(R.id.voiceSearchInfo);

        //Attach the listeners
        btnSearch.setOnClickListener(this);
        btnCrazySearch.setOnClickListener(this);
        btnTalk.setOnClickListener(this);
        searchTextEditText.setOnEditorActionListener(this);

        //Initialize remaining variables
        response = new ProductSearchResponse();

        //If savedInstanceState available retrieve search text from it.
        if(savedInstanceState != null && savedInstanceState.containsKey(Constants.SEARCH_KEY_TERM)) {
            searchTextEditText.setText(savedInstanceState.getString(Constants.SEARCH_KEY_TERM, ""));
        }

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        initializeRecognizerIntent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speech != null) {
            speech.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (speech != null) {
            speech.stopListening();
            indicatorView.hide();
        }
    }

    /* Function validates the search term and searches by starting the ProductSearchAsyncTask.
     * @param term The term to search
     *
     * @return void
     */
    private void search(String term) {
        if(validateSearchTerm(term)) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put(Constants.SEARCH_KEY_TERM, term);
            ProductSearchAsyncTask task = new ProductSearchAsyncTask(MainActivity.this, HttpRequests.SEARCH_PRODUCT, parameters, String.format(getString(R.string.searching_display_string), term));
            task.execute((Void) null);
        }
        else {
            showError(getResources().getString(R.string.error_no_search_term_message));
        }
    }

    /*
     * Method initializes the recognizerIntent with necessary parameters.
     */
    private void initializeRecognizerIntent() {
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
    }

    /*
     * Method starts the speech recognition process.
     * However, before starting, a check to ensure that RECORD_AUDIO permissions have been granted
     * to the app is performed. In case the app does not have the required permission corresponding
     * dialog to request permission is created.
     */
    private void startVoiceRecognitionActivity() {
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            txtVoiceSearchInfo.setText(getString(R.string.voice_search_commant_message));
            speech.startListening(recognizerIntent);
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    VOICE_RECOGNITION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case VOICE_RECOGNITION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startVoiceRecognitionActivity();

                } else {
                    showError(getString(R.string.permission_denied));
                }
            }
        }
    }

    /* Function validates the term to be searched.
     *
     * @return boolean Validates the search term and returns true if successful else false.
     *
     * Note: Currently only basic validation is been done and it is assumed that any special
     * character can be part of the term being searched and thus not checked for.
     */
    private boolean validateSearchTerm(String searchTerm) {
        return !searchTerm.isEmpty();
    }

    /*
     * Listener handles the Send/Go key press event for searching a product.
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
            case R.id.btnCrazySearch:
                search(Constants.CRAZY_SEARCH_TERM);
                break;
            case R.id.startVoiceSearch:
                startVoiceRecognitionActivity();
                break;
        }
    }

    /*
     * Implementation of the DownloadCallback Interface method.
     * Carries out post asynctask completion activities.
     */
    @Override
    public void finishedDownloading(int operation) {
        switch (operation)  {
            case SEARCH_COMPLETE:
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                intent.putExtra(Constants.SEARCH_RESULTS_KEY, response);
                startActivity(intent);
                break;
            case SEARCH_FAILED:
                showError(getResources().getString(R.string.error_no_products_found_message));
                searchTextEditText.setText("");
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
    private void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /* Function displays an error message in a Snackbar.
     *
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

    //Mandatory handler implementations for the RecognitionListener interface.

    @Override
    public void onReadyForSpeech(Bundle params) {
        indicatorView.show();
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        speech.stopListening();
        indicatorView.hide();
    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle results) {
        ArrayList matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if(matches != null && matches.size() > 0) {
            String bestMatch = matches.get(0).toString();
            if (bestMatch.toLowerCase().contains(Constants.VOICE_SEARCH_PREFIX.toLowerCase()) && bestMatch.length() > Constants.VOICE_SEARCH_PREFIX.length()) {
                String term = bestMatch.substring(Constants.VOICE_SEARCH_PREFIX.length(), bestMatch.length());
                search(term);
            }
            else {
                showError(getString(R.string.voice_search_wrong_command));
            }
        }
        else {
            showError(getString(R.string.voice_search_failed));
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if(matches != null && matches.size() > 0) {
            searchTextEditText.setText(matches.get(0).toString());
        }
        else {
            showError(getString(R.string.voice_search_failed));
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    //AsyncTask to perform product search operation.
    private class ProductSearchAsyncTask extends BaseHttpRequestAsyncTask<ProductSearchResponse> {

        DownloadCallback mCallback;

        private ProductSearchAsyncTask(Context context, int apiMethod, Map<String, String> parameters, String displayMessage) {
            super(context, apiMethod, parameters, displayMessage);
            this.mCallback = (DownloadCallback) context;
        }

        @Override
        protected void onPostExecute(ProductSearchResponse searchResponse) {
            super.onPostExecute(searchResponse);
            if(searchResponse != null && searchResponse.results.size() > 0) {
                response = searchResponse;
                mCallback.finishedDownloading(SEARCH_COMPLETE);
            }
            else {
                mCallback.finishedDownloading(SEARCH_FAILED);
            }
        }
    }

    /*
     * Method to save the necessary data in the save
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.SEARCH_KEY_TERM, searchTextEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
