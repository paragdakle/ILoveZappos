package com.test.zappos.ilovezappos.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.test.zappos.ilovezappos.common.DownloadCallback;

import java.util.Map;


/**
 * Created by Parag Dakle on 7/3/16.
 *
 * Generic template class for all HTTP request AsyncTasks.
 *
 * @param T Response Class
 */
public class BaseHttpRequestAsyncTask<T> extends AsyncTask<Void, T, T> {

    private ProgressDialog pDialog;
    private Context mContext;
    private int apiMethod;
    private Map<String, String> parameters;

    public BaseHttpRequestAsyncTask(Context context, int apiMethod, Map<String, String> parameters, String displayMessage) {
        this.mContext = context;
        this.apiMethod = apiMethod;
        this.parameters = parameters;

        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage(displayMessage);
        pDialog.show();
    }

    @Override
    protected T doInBackground(Void... params) {
        if(((DownloadCallback)mContext).checkNetworkConnection()) {
            HttpRequests<T> httpRequest = new HttpRequests<T>();
            return httpRequest.execute(apiMethod, parameters);
        }
        return null;
    }

    @Override
    protected void onPostExecute(T object) {
        super.onPostExecute(object);
        if(pDialog != null)
            pDialog.cancel();
    }
}
