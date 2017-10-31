package com.example.android.newsListing;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kanav on 19/09/17.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Activity context, ArrayList<News> locations) {
        super(context, 0, locations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_list, parent, false);
        }
        TextView title = (TextView) listItemView.findViewById(R.id.title);
        TextView author = (TextView) listItemView.findViewById(R.id.author);
        TextView date = (TextView) listItemView.findViewById(R.id.date);
        TextView section = (TextView) listItemView.findViewById(R.id.section);

        News currentNews = getItem(position);
        section.setText(currentNews.getSectionName());
        author.setText(currentNews.getAuthor());
        date.setText(currentNews.getDate());
        title.setText(currentNews.getTitle());

        return listItemView;
    }
}
