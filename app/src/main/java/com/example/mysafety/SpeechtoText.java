package com.example.mysafety;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class SpeechtoText extends AppCompatActivity {

    static private EditText txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT=100;
    private final int REQ_IMAGE_CAPTURE=1;
    private final int REQ_PHOTO_PICKER=2;
    Button next,photo,gallery;
    static AlertDialog.Builder dialog;
    FirebaseFirestore db;
    FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speechto_text);

        txtSpeechInput = (EditText) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        next=findViewById(R.id.next);
        db=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        next=findViewById(R.id.next);
        photo=findViewById(R.id.photo);
        gallery=findViewById(R.id.gallery);

        dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Any other complaint?").setCancelable(true);

        dialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addComplaint(txtSpeechInput.getText().toString().trim());
                        txtSpeechInput.setText("");
                        dialog.cancel();
                    }
                });

        dialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(SpeechtoText.this,MainPage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        addComplaint(txtSpeechInput.getText().toString().trim());
                        txtSpeechInput.setText("");
                        dialog.cancel();
                        finish();
                    }
                });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtSpeechInput.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(),"No text entered",Toast.LENGTH_SHORT).show();
                else
                dialog.show();
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Takepicture();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQ_PHOTO_PICKER);
            }
        });

    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        TimeZone tz=TimeZone.getTimeZone("Asia/Kolkata");
        Calendar calendar=Calendar.getInstance(tz);
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat timeformat=new SimpleDateFormat("HH:mm:ss",Locale.getDefault());

        String strDate = mdformat.format(calendar.getTime());
        String strTime=timeformat.format(calendar.getTime());
        String finaldate=strDate+" "+strTime;

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                }
                break;
            }

            case REQ_IMAGE_CAPTURE:{
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.READ_CONTACTS)) {
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                        } else {
                            // No explanation needed; request the permission
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.READ_CONTACTS},
                                    0);

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    }
                    Log.d("TAG",""+imageBitmap);
                    try{
                    Uri imageuri=getImageUri(this,imageBitmap);
                    StorageReference storageReference=firebaseStorage.getReference().child("MySafety");
                    String path=finaldate.trim()+" "+imageuri.getLastPathSegment().trim();

                        StorageReference childreference = storageReference.child(path);
                        childreference.putFile(imageuri);
                        addPhoto(imageuri.getLastPathSegment(),finaldate);
                        Toast.makeText(SpeechtoText.this, "Photo Successfully Uploaded", Toast.LENGTH_SHORT).show();
                    }catch (NullPointerException e){
                        Toast.makeText(SpeechtoText.this,"Error in uploading photo",Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            }

            case REQ_PHOTO_PICKER:{
                if(resultCode==RESULT_OK) {
                    Uri imageuri = data.getData();
                    StorageReference storageReference = firebaseStorage.getReference().child("MySafety");
                    String path=finaldate.trim()+" "+imageuri.getLastPathSegment().trim();
                    try {
                        StorageReference childreference = storageReference.child(path);
                        childreference.putFile(imageuri);
                        addPhoto(imageuri.getLastPathSegment(),finaldate);
                        Toast.makeText(SpeechtoText.this, "Photo Successfully Uploaded", Toast.LENGTH_SHORT).show();
                    } catch (NullPointerException e) {
                        Toast.makeText(SpeechtoText.this, "Error in uploading photo", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }
    }

    public void addComplaint(String complaint){
        TimeZone tz=TimeZone.getTimeZone("Asia/Kolkata");
        Calendar calendar=Calendar.getInstance(tz);
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat timeformat=new SimpleDateFormat("HH:mm:ss",Locale.getDefault());

        String strDate = mdformat.format(calendar.getTime());
        String strTime=timeformat.format(calendar.getTime());
        String finaldate=strDate+" "+strTime;

        SharedPreferences sharedPreferences=this.getSharedPreferences("Userdetails",MODE_PRIVATE);
        String user=sharedPreferences.getString("User","");

        Map<String,Object> details=new HashMap<>();
        details.put("User",user);
        details.put("Date",strDate);
        details.put("Complaint",complaint);
        details.put("Time",finaldate);

        db.collection(getString(R.string.complaint)).document(finaldate)
                .set(details, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "Complaint added with ID: " );
                        Toast.makeText(SpeechtoText.this,"Complaint registered",Toast.LENGTH_SHORT).show();
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Error adding document", e);
                Toast.makeText(SpeechtoText.this,"Unable to register complaint",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void addPhoto(String url,String finaldate){

        SharedPreferences sharedPreferences=this.getSharedPreferences("Userdetails",MODE_PRIVATE);
        String user=sharedPreferences.getString("User","");

        Map<String,Object> details=new HashMap<>();
        details.put("Image",url.trim());
        details.put("User",user);
        details.put("Time",finaldate);

        db.collection("Images").document(finaldate)
                .set(details,SetOptions.merge());

    }

    public void Takepicture() {
        PackageManager pm = this.getPackageManager();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQ_IMAGE_CAPTURE);
            }
        }
        else
            Toast.makeText(this,"Camera unavailable",Toast.LENGTH_SHORT).show();
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
