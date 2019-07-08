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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.DeleteDocumentRequest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static com.example.mysafety.SpeechtoText.dialog;

public class MyComplaints extends AppCompatActivity {

    TextView error;
    ListView listView;
    FirebaseFirestore db;
    CustomAdapter1 customAdapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_complaints);

        error=findViewById(R.id.error1);
        listView=findViewById(R.id.list1);
        error.setVisibility(View.GONE);

        db=FirebaseFirestore.getInstance();


        SharedPreferences sharedPreferences=this.getSharedPreferences("Userdetails",MODE_PRIVATE);
        String user=sharedPreferences.getString("User","");

        db.collection(getString(R.string.complaint)).whereEqualTo("User",user).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<MyComplaint> myComplaints=new ArrayList<>();
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()){
                                MyComplaint myComplaint=new MyComplaint(document.getString("Date"),document.getString("Complaint"),document.getString("Time"));
                                myComplaints.add(myComplaint);
                            }
                            customAdapter1 = new CustomAdapter1(getApplicationContext(),myComplaints);
                            listView.setAdapter(customAdapter1);
                        }
                        if(task.getResult().isEmpty())error.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG","Error accessing documents");
            }
        });

        listView.setLongClickable(true);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                MyComplaint myComplaint=customAdapter1.getItem(position);
                buildDialog(myComplaint);
                return true;
            }
        });
    }

    private void buildDialog(final MyComplaint myComplaint){

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete the complaint?");

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataDelete(myComplaint);
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("TAG",""+myComplaint.getTime());
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    private void DataDelete(final MyComplaint myComplaint){
        db.collection("Complaints").document(myComplaint.getTime())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                        Toast.makeText(MyComplaints.this,"Complaint Deleted",Toast.LENGTH_SHORT).show();
                        customAdapter1.remove(myComplaint);
                        if(customAdapter1.isEmpty())
                            error.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error deleting document", e);
                    }
                });

    }
}
