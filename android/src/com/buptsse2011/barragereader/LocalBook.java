package com.buptsse2011.barragereader;

import android.content.ContentValues;

public class LocalBook {
    private String name;
    private String path;
    
    public ContentValues toContentValues(){
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("path", path);
        return cv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    
}
