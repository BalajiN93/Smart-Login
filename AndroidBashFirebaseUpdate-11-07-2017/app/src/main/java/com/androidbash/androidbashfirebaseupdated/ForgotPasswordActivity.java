package com.androidbash.androidbashfirebaseupdated;

//   http://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/


/**
 * Created by Developer on 06-07-2017.
 */

import android.app.ProgressDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class ForgotPasswordActivity extends AppCompatActivity {
    public EditText inputemail;
    Button btn_reset_password;
//    Button btn_back;
    private FirebaseAuth auth;
    private ProgressBar progressBar;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        btn_reset_password=(Button) findViewById(R.id.btn_reset_password);
//        btn_back=(Button) findViewById(R.id.btn_back);
        inputemail=(EditText) findViewById(R.id.email);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        Bundle b = getIntent().getExtras();
        inputemail.setText(b.getCharSequence("useremail"));


        auth = FirebaseAuth.getInstance();

//        btn_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });



//        btn_reset_password.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                final String email = inputemail.getText().toString().trim();
//
//                if (TextUtils.isEmpty(email)) {
//                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                progressBar.setVisibility(View.VISIBLE);
//                AlertDialog.Builder dialog = new AlertDialog.Builder(ForgotPasswordActivity.this);
//                dialog.setCancelable(false);
//                dialog.setTitle("Reset password");
//                dialog.setMessage("Are you sure you want to reset a password" );
//                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        //Action for "Delete".
//                        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//
//                                if (task.isSuccessful()) {
//
//                                    finish();
//
////                                    Toast.makeText(ForgotPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
//                                }
//
//                                progressBar.setVisibility(View.GONE);
//                            }
//                        });
//                    }
//                })
//                        .setNegativeButton("No ", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //Action for "Cancel".
//                                finish();
//                            }
//                        });
//
//                final AlertDialog alert = dialog.create();
//                alert.show();
//            }
//        });


        btn_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputemail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(),"Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(ForgotPasswordActivity.this);
                                    dialog.setCancelable(false);
                                    dialog.setTitle("Reset password");
                                    dialog.setMessage("We have sent a mail to you instructions to reset your password!" );
                                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Action for "Delete".
                                            finish();
                                        }
                                    });
                                  final AlertDialog alert = dialog.create();
                                    alert.show();
//                                    Toast.makeText(ForgotPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }
}
