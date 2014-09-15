package com.android.tuto.ch6earthquake;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EarthquakeItemDapter extends ArrayAdapter<Quake> {
    private int resource;

    public EarthquakeItemDapter(Context context, int resource, List<Quake> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Create and inflate the View to display
        LinearLayout todoView;

        Quake item = getItem(position);
        String title = item.toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        String createdDate = sdf.format(item.getDate());

        if (convertView == null) {
            // Inflate a new view if this is not an update.
            todoView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, todoView, true);
        } else {
            // Otherwise we'll update the existing View
            todoView = (LinearLayout) convertView;
        }

        TextView dateView = (TextView) todoView.findViewById(R.id.earthDate);
        TextView infoView = (TextView) todoView.findViewById(R.id.earthquakeInfo);
        dateView.setText(createdDate);
        infoView.setText(title);
        return todoView;
    }
}
