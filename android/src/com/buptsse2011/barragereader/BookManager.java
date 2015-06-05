package com.buptsse2011.barragereader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class BookManager {
    private Context mContext;
    private ProgressDialog mProgress;
    private SQLiteDatabase mBookDB;

    public static final String DB_NAME = "book";
    public static final String TABLE_LOCAL_BOOKS = "local_books";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PATH = "path";

    public BookManager(Context context) {
        mContext = context;
        mProgress = new ProgressDialog(context);

        mBookDB = mContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,
                null);
        mBookDB.execSQL("create table if not exists " + TABLE_LOCAL_BOOKS
                + " (" + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_PATH + " varchar(1024), " + COLUMN_NAME
                + " varchar(1024))");
    }

    public Cursor getLocalBooks() {
        return mBookDB.query(TABLE_LOCAL_BOOKS, null, null, null, null, null,
                null);
    }

    public void insertLocalBook(LocalBook book) {
        mBookDB.insert(TABLE_LOCAL_BOOKS, null, book.toContentValues());
    }

    public String getLocalStorageDir() {
        return "/sdcard/";
    }

    public void download(String saveAs, String from) {
        new HttpDownloadTask().execute(new String[] { saveAs, from });
    }

    class HttpDownloadTask extends AsyncTask<String, Integer, Boolean> {

        private String remotePath;
        private String filepath;
        private int length;
        private int finished;
        private String saveAs;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub

            if (params.length < 2) {
                // wrong params
            }
            boolean ret = false;
            saveAs = params[0];
            remotePath = params[1];

            try {
                URL url = new URL(remotePath);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setRequestProperty("Accept-Encoding", "identity");

                length = conn.getContentLength();
                finished = 0;
                Log.i("yxy", "download data len = " + length);

                InputStream is = conn.getInputStream();
                File filesDir = mContext.getFilesDir();
                String filename = saveAs;
                filepath = getLocalStorageDir() + File.separator + filename;

                FileOutputStream fos = new FileOutputStream(filepath);
                byte[] buf = new byte[4096];
                Log.i("yxy", "downloading from server");
                int sz = 0;
                while ((sz = is.read(buf)) != -1) {
                    fos.write(buf, 0, sz);
                    finished += sz;
                    publishProgress(new Integer[] { (int) (100 * finished / (float) length) });
                }
                is.close();
                fos.close();
                conn.disconnect();
                ret = true;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return ret;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            mProgress.dismiss();
            LocalBook lb = new LocalBook();
            lb.setName(saveAs);
            lb.setPath(filepath);
            insertLocalBook(lb);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            mProgress.setMessage("Downloading data file...");
            mProgress.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            mProgress.setProgress(values[0]);
            super.onProgressUpdate(values);
        }
    }
}
