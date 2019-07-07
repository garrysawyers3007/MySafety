package com.example.mysafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class MyGallery extends AppCompatActivity {

    GridView gridView;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore db;
    TextView NoImage;
    ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gallery);

        firebaseStorage=FirebaseStorage.getInstance();
        db=FirebaseFirestore.getInstance();

        NoImage=findViewById(R.id.myerror);
        NoImage.setVisibility(View.GONE);

        gridView=findViewById(R.id.mygridview);

        SharedPreferences sharedPreferences=this.getSharedPreferences("Userdetails",MODE_PRIVATE);
        String user=sharedPreferences.getString("User","");

        db.collection("Images").whereEqualTo("User",user).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<String> images=new ArrayList<>();
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
                Toast.makeText(MyGallery.this,"Error Loading Photos",Toast.LENGTH_SHORT).show();
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String path=imageAdapter.getItem(position);
                buildDialog(path);
                return true;
            }
        });
    }
    private void buildDialog(final String path){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete the photo?");

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PhotoDelete(path);
                        int index=path.lastIndexOf(' ');
                        String time=path.substring(0,index);
                        ReferenceDelete(time);
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    private void PhotoDelete(final String path){
        firebaseStorage.getReference().child("MySafety").child(path)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MyGallery.this,"Photo deleted",Toast.LENGTH_SHORT).show();
                imageAdapter.remove(path);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyGallery.this,"Error Deleting Photo",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ReferenceDelete(String time){
            db.collection("Images").document(time)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG","Document deleted");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("TAG", "Error deleting document", e);
                }
            });
    }
}
