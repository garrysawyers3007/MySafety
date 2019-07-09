package com.example.mysafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
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

public class Gallery extends AppCompatActivity implements DisplayImage.OnFragmentInteractionListener {

    GridView gridView;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore db;
    TextView NoImage;
    ImageAdapter imageAdapter;
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
        gridView.setVisibility(View.VISIBLE);//gridview to display images


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
        });//Getting images to be displayed in gridview and setting them in the gridview

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path=imageAdapter.getItem(position);

                Bundle bundle=new Bundle();
                bundle.putString("path",path);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                DisplayImage displayImage=new DisplayImage();
                displayImage.setArguments(bundle);

                gridView.setVisibility(View.GONE);

                fragmentTransaction.add(R.id.galleryview,displayImage);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });//Displaying enlarged image on clicking every element

    }

    @Override
    public void onBackPressed(){
        int fragments=getSupportFragmentManager().getBackStackEntryCount();
        if(fragments==1){
            getSupportFragmentManager().popBackStack();
            gridView.setVisibility(View.VISIBLE);
        }
        else
            super.onBackPressed();
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
