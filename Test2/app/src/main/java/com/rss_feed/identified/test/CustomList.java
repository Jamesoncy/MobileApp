package com.rss_feed.identified.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Acer on 10/22/2015.
 */public class CustomList extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private int i = 0;


    public CustomList(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
        //just return 0 if your list items do not have an Id variable.
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
       // this.i++;
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        TextView link = (TextView)view.findViewById(R.id.link);
        TextView blurb = (TextView)view.findViewById(R.id.blurb);
        TextView picture = (TextView)view.findViewById(R.id.picture);
        String str[] = list.get(position).split("\\|");
        //listItemText.setText(list.get(position));
        link.setText(str[1]);
        listItemText.setText(str[0]);
        blurb.setText(str[2]);
        picture.setText(str[3]);
        Button LikeBtn = (Button) view.findViewById(R.id.like);
        LikeBtn.setTag(str[4]);

        return view;
    }
}