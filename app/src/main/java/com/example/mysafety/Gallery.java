package com.example.mysafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {

    GridView gridView;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore db;
    TextView NoImage;
    ImageAdapter imageAdapter;
    SearchView searchView;
    List<String> images;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        firebaseStorage=FirebaseStorage.getInstance();
        db=FirebaseFirestore.getInstance();

        NoImage=findViewById(R.id.error);
        NoImage.setVisibility(View.GONE);

        gridView=findViewById(R.id.gridview);
        searchView=findViewById(R.id.searchView);


        db.collection("Images").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        images=new ArrayList<>();
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()){
                                String path=document.getString("Time")+" "+document.getString("Image");
                                images.add(path.trim());
                            }
                            imageAdapter=new ImageAdapter(getApplicationContext(),images);
                            gridView.setAdapter(imageAdapter);
                            if(task.getResult().isEmpty())NoImage.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG","Error in getting documnets");
                Toast.makeText(Gallery.this,"Error Loading Photos",Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
