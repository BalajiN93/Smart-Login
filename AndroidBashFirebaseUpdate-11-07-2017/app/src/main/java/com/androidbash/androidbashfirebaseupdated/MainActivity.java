package com.androidbash.androidbashfirebaseupdated;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;

import com.facebook.login.LoginManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.twitter.sdk.android.Twitter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.android.gms.common.api.GoogleApiClient;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Cygnus";
    private Firebase myFirebaseRef;
    private FirebaseAuth mAuth;
    private TextView name;
    private TextView welcomeText;
    private Button changeButton;
    private Button revertButton;

    // To hold Facebook profile picturemmmm
    private ImageView profilePicture;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//  Creates a reference for  your Firebase database Add YOUR Firebase Reference URL instead of the following URL
        myFirebaseRef = new Firebase("https://login-multiple.firebaseio.com/users/");
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        name = (TextView) findViewById(R.id.text_view_name);
        welcomeText = (TextView) findViewById(R.id.text_view_welcome);
        changeButton = (Button) findViewById(R.id.button_change);
        revertButton = (Button) findViewById(R.id.button_revert);
        profilePicture = (ImageView) findViewById(R.id.profile_picture);
        //Get the uid for the currently logged in User from intent data passed to this activity
        String uid = getIntent().getExtras().getString("user_id");
        //Get the imageUrl  for the currently logged in User from intent data passed to this activity
        String imageUrl = getIntent().getExtras().getString("profile_picture");

        new ImageLoadTask(imageUrl, profilePicture).execute();

        //Referring to the name of the User who has logged in currently and adding a valueChangeListener
        myFirebaseRef.child(uid).child("name").addValueEventListener(new ValueEventListener() {
            //onDataChange is called every time the name of the User changes in your Firebase Database
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//   Inside onDataChange we can get the data as an Object from the dataSnapshot getValue returns an Object. We can specify the type by passing the type expected as a parameter
                String data = dataSnapshot.getValue(String.class);
                System.out.println("Data:" +data);
                name.setText("Hello" + " "+data + ", ");
            }

            //onCancelled is called in case of any error
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        //A firebase reference to the welcomeText can be created in following ways :
        // You can use this :
        //Firebase myAnotherFirebaseRefForWelcomeText=new Firebase("https://androidbashfirebaseupdat-bd094.firebaseio.com/welcomeText");*/
        //OR as shown below
        myFirebaseRef.child("welcomeText").addValueEventListener(new ValueEventListener() {
            //onDataChange is called every time the data changes in your Firebase Database
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//   Inside onDataChange we can get the data as an Object from the dataSnapshot getValue returns an Object. We can specify the type by passing the type expected as a parameter
                String data = dataSnapshot.getValue(String.class);
                welcomeText.setText(data);
            }

            //onCancelled is called in case of any error
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "" + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        //onClicking changeButton the value of the welcomeText in the Firebase database gets changed
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myFirebaseRef.child("welcomeText").setValue("Android App Development @ Cyguns");
            }
        });

        //onClicking revertButton the value of the welcomeText in the Firebase database gets changed
        revertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myFirebaseRef.child("welcomeText").setValue("Welcome to Learning @ Cyguns");
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        menu.add(0, 0, 0, "Add").setIcon(android.R.drawable.ic_menu_add);
//        return super.onCreateOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//   Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
            if (id == R.id.action_logout) {


           mAuth.getInstance().signOut();


                   Bundle extras = getIntent().getExtras();
                if(extras!=null) {
                    if (extras.getString("fb") != null) {
//         Facebook signout
                        LoginManager.getInstance().logOut();

                                        }
                   else if (extras.getString("twitter") != null) {
//         Twitter signout
                         CookieSyncManager.createInstance(this);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeSessionCookie();
                Twitter.getSessionManager().clearActiveSession();
                        Twitter.logOut();
                    }

                }



          // Google sign out
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                        new ResultCallback<Status>() {
//                            @Override
//                            public void onResult(@NonNull Status status) {
//                                Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
//                            }
//                        });

//         Facebook signout
//                LoginManager.getInstance().logOut();

//          Twitter logout
//                CookieSyncManager.createInstance(this);
//                CookieManager cookieManager = CookieManager.getInstance();
//                cookieManager.removeSessionCookie();
//                Twitter.getSessionManager().clearActiveSession();
//                Twitter.logOut();
                finish();
        }
return super.onOptionsItemSelected(item);
    }


//    private void signOut() {
        // Firebase sign out
//        mAuth.signOut();
//        mAuth.getInstance().signOut();
        // Google sign out
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(@NonNull Status status) {
//
//                    }
//                });
        //         Facebook signout
//                LoginManager.getInstance().logOut();

//    }





 public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
        private String url;
        private ImageView imageView;
        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }
    }


}

