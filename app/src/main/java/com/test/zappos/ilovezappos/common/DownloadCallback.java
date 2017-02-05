package com.test.zappos.ilovezappos.common;

/**
 * Created by root on 4/2/17.
 */

public interface DownloadCallback {

    /* Method must be implemented and called when the background network download is finished.
     * @param operation Flag to identify which download was finished
     *
     * @return void
     */
    void finishedDownloading(int operation);

    /* Method must be implemented and check network connection and
     * return true if connected to internet else false.
     *
     * @return boolean Boolean value indicating whether Internet Connection is present.
     */
    boolean checkNetworkConnection();
}
