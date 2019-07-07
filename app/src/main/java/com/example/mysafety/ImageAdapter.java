package com.example.mysafety;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

class ImageAdapter extends ArrayAdapter<String> {

    Context context;

    public ImageAdapter(@NonNull Context context, List<String> objects) {
        super(context,0, objects);
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View listitemview=convertView;
        if(listitemview==null)//Check if the existing view is being used if not inflate a new view
            listitemview = LayoutInflater.from(getContext()).inflate(R.layout.image, parent, false);

        String path=getItem(position);
        final ImageView imageView=listitemview.findViewById(R.id.imageview);

        TextView textView=listitemview.findViewById(R.id.label);
        textView.setText("Photo:"+(position+1));


        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
        StorageReference storage=firebaseStorage.getReference().child("MySafety");
        StorageReference childreference=storage.child(path);
        childreference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url=uri.toString();
                        Glide.with(context)
                                .load(url)
                                .into(imageView);
                    }
                });

        return listitemview;
    }
}
