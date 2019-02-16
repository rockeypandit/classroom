package com.example.classroom;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadManager extends AsyncTask<Void, Void, Void> {
    URL downloadUrl;
    File downloadFile;

    public DownloadManager(String url, String fileName) {
        try {
            this.downloadUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        this.downloadFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            FileUtils.copyURLToFile(this.downloadUrl, this.downloadFile);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("FAILURE", "Download Failed");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}