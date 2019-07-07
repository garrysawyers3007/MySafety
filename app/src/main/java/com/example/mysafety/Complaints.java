package com.example.mysafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class Complaints extends AppCompatActivity {

    TextView nocomplaints;
    ListView listView;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);

        nocomplaints=findViewById(R.id.nocomplaints);
        nocomplaints.setVisibility(View.GONE);

        db=FirebaseFirestore.getInstance();




        db.collection(getString(R.string.complaint)).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Details> details=new ArrayList<>();
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Details details1=new Details(document.getString("Date"),document.getString("User"),document.getString("Complaint"));
                        details.add(details1);
                    }
                    listView=findViewById(R.id.list);
                    CustomAdapter customAdapter=new CustomAdapter(getApplicationContext(),details);
                    listView.setAdapter(customAdapter);
                }
                if(task.getResult().isEmpty())nocomplaints.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG","Error in getting documnets");
            }
        });

    }

}
