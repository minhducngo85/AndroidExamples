package com.android.tuto.ch8todolistdatabase;

import java.text.SimpleDateFormat;
import java.util.List;

import com.android.tuto.ch8todolistdatabase.data.ToDoItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ToDoItemAdapter extends ArrayAdapter<ToDoItem> {

    private int resource;

    public ToDoItemAdapter(Context context, int resource, List<ToDoItem> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Create and inflate the View to display
        LinearLayout todoView;

        ToDoItem item = getItem(position);
        String task = item.getTask();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        String createdDate = sdf.format(item.getCreatedOn());

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

        TextView dateView = (TextView) todoView.findViewById(R.id.rowDate);
        TextView taskView = (TextView) todoView.findViewById(R.id.rowTask);
        dateView.setText(createdDate);
        taskView.setText(task);

        return todoView;

    }

}
