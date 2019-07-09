package com.example.mysafety;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Details> {
    public CustomAdapter( Context context, List<Details> objects) {
        super(context,0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listitemview=convertView;
        if(listitemview==null)//Check if the existing view is being used if not inflate a new view
            listitemview = LayoutInflater.from(getContext()).inflate(R.layout.complaint, parent, false);

        Details details=getItem(position);

        TextView date=listitemview.findViewById(R.id.date);
        date.setText(details.getDate());

        TextView username=listitemview.findViewById(R.id.user);
        username.setText(details.getUsername());

        TextView complaint=listitemview.findViewById(R.id.complaint);
        complaint.setText(details.getComplaint());


        return listitemview;
    }
}
