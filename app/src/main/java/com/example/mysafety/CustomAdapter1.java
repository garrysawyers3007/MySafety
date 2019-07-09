package com.example.mysafety;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mysafety.MyComplaint;
import com.example.mysafety.R;

import java.util.List;

public class CustomAdapter1 extends ArrayAdapter<MyComplaint> {
    public CustomAdapter1(Context context, List<MyComplaint> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemview=convertView;
        if(listitemview==null)//Check if the existing view is being used if not inflate a new view
            listitemview = LayoutInflater.from(getContext()).inflate(R.layout.mycomplaint, parent, false);

        MyComplaint myComplaint=getItem(position);

        TextView date=listitemview.findViewById(R.id.date1);
        date.setText(myComplaint.getDate());

        TextView department=listitemview.findViewById(R.id.mydepartment);
        department.setText(myComplaint.getDepartment());

        TextView complaint=listitemview.findViewById(R.id.complaint1);
        complaint.setText(myComplaint.getComplaint());

        return listitemview;
    }
}
