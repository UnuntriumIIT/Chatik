package com.example.ununtrium.chatik;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class ImageAdapter extends ArrayAdapter<ImgItem> {

    private final Context context;
    private final ArrayList<ImgItem> mList;

    public ImageAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ImgItem> objects) {
        super(context, resource, objects);
        this.context = context;
        mList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.img_view, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.author);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img_item);

        textView.setText(mList.get(position).getAuthor());
        imageView.setImageBitmap(mList.get(position).getImage());

        return rowView;
    }
}
