package com.example.ununtrium.chatik;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ImgItem implements Serializable {

    private String author;
    private Bitmap image;
    public final String FileName;

    public ImgItem(String author, Bitmap image, String FileName) {
        this.author = author;
        this.image = image;
        this.FileName = FileName;
    }

    public String getAuthor() {
        return author;
    }

    public Bitmap getImage() {
        return image;
    }
}
