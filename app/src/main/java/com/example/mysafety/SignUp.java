package com.example.mysafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText memail,mpassword,mconfirmpw;
    String email,password,confirmpw;
    Button createuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        memail=findViewById(R.id.email1);
        mpassword=findViewById(R.id.password1);
        mconfirmpw=findViewById(R.id.confirmpw);

        createuser=findViewById(R.id.create);
        mAuth=FirebaseAuth.getInstance();

        createuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=memail.getText().toString().trim();
                password=mpassword.getText().toString();
                confirmpw=mconfirmpw.getText().toString();

                if(email.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!email.trim().isEmpty() && password.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!confirmpw.trim().equals(password.trim())) {
                    Toast.makeText(getApplicationContext(), "Password and Confirm password not same", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences sharedPreferences=SignUp.this.getSharedPreferences("Userdetails",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("User",email);
                editor.apply();
                email=email.trim()+"@mysafety.com";
                firebasecreateuser();
            }
        });
    }

    private void firebasecreateuser() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(),"Account successfully created!!",Toast.LENGTH_SHORT).show();
                        }
                         else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Invalid Email/Password",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

}
