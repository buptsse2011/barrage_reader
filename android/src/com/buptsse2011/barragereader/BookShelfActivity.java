package com.buptsse2011.barragereader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class BookShelfActivity extends Activity {
    private ListView mListView;
    private BookManager mBookManager;
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookshelf);
        mListView = (ListView) findViewById(R.id.bookshelf);
        mBookManager = new BookManager(this);
        Cursor cursor = mBookManager.getLocalBooks();
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cursor,
                new String[] { BookManager.COLUMN_NAME },
                new int[] { android.R.id.text1 }, SimpleCursorAdapter.FLAG_AUTO_REQUERY);
        
        mListView.setAdapter(mAdapter);

        showDownloadDialog();
    };

    private void showDownloadDialog() {
        final EditText etRemoteAddr = new EditText(this);
        etRemoteAddr.setHint("address");
        etRemoteAddr
                .setText("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superplus/img/logo_white_ee663702.png");
        final EditText etSaveAs = new EditText(this);
        etSaveAs.setHint("filename");
        etSaveAs.setText("baidu.png");
        final LinearLayout dialogContent = new LinearLayout(this);
        dialogContent.setOrientation(LinearLayout.VERTICAL);
        dialogContent.addView(etRemoteAddr);
        dialogContent.addView(etSaveAs);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Download a book").setView(dialogContent)
                .setPositiveButton("Start", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        String addr = "";
                        String name = "";
                        addr = etRemoteAddr.getText().toString();
                        name = etSaveAs.getText().toString();

                        mBookManager.download(name, addr);
                    }
                }).setNegativeButton("Dismiss", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        arg0.dismiss();
                    }
                }).create().show();
    }
}
