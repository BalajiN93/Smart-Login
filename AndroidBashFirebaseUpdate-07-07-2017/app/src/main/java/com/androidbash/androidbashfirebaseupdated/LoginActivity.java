package com.androidbash.androidbashfirebaseupdated;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.ResultCallback;


import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterConfig;
import android.view.View.OnClickListener;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;

//import com.twitter.sdk.android.Twitter;
import io.fabric.sdk.android.Fabric;


/**
 * Created by AndroidBash on 10/07/16
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "AndroidBash";
    public User user;
    public EditText email;
    public EditText password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgressDialog;
    Button customfb;
    Button customtw;
    Button customgoo;
    private SignInButton mGoogleSignInButton;
    //Add YOUR Firebase Reference URL instead of the following URL
    Firebase mRef=new Firebase("https://login-multiple.firebaseio.com/users/");

    //FaceBook callbackManager
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    //Google Sign In
    private static final int RC_SIGN_IN = 9001;

    //Twitter Sign In
    private TwitterLoginButton mTwitterLoginButton;
    private static final String TWITTER_KEY ="NckgyiepzQSZBRsSV5F2JSNqn";
    private static final String TWITTER_SECRET ="Hq1sarcAEOSoj0A82Crnr5rOWOjB6JzOQwzyuQ9Y1FTvghIbmp";

    private TwitterAuthClient authConfig;
    private TextView forgotpass;
//    private Button forgotpass;
    LoginButton loginButton;

//    private TwitterAuthClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());


//          Twitter
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics());

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("NckgyiepzQSZBRsSV5F2JSNqn","Hq1sarcAEOSoj0A82Crnr5rOWOjB6JzOQwzyuQ9Y1FTvghIbmp"))
                .debug(true)
                .build();
        Twitter.initialize(config);
        setContentView(R.layout.activity_login);




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//       mGoogleSignInButton = (SignInButton)findViewById(R.id.google_sign_in_button);

        customgoo=(Button) findViewById(R.id.button5);
//        LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);


//Forget Password
        forgotpass=(TextView) findViewById(R.id.forgot_password);
//        forgotpass=(Button) findViewById(R.id.forgot_password);
//        forgotpass.setOnClickListener((OnClickListener) LoginActivity.this);

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateFormPass()) {
                    return;
                }

                showProgressDialog();
                Intent inten = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                Bundle b = new Bundle();
                b.putString("useremail", email.getText().toString());
                inten.putExtras(b);
                startActivity(inten);

                hideProgressDialog();
            }
        });


        customgoo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("-------signin---");
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                  }
        });
//         Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            // User is signed in
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            String uid = mAuth.getCurrentUser().getUid();
//            String image=mAuth.getCurrentUser().getPhotoUrl().toString();
            intent.putExtra("user_id", uid);
//            if(image!=null || image!=""){
//                intent.putExtra("profile_picture",image);
//            }
            startActivity(intent);
            finish();
            Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


        //FaceBook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
      loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        loginButton.setBackgroundResource(R.drawable.facebook);
        loginButton.setCompoundDrawablePadding(0);
        loginButton.setPadding(0, 0, 0, 0);
        loginButton.setText("");
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                signInWithFacebook(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }
            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                System.out.println("-----facebook:onError-----"+error);
                Toast.makeText(LoginActivity.this, "facebook:onError-."+error, Toast.LENGTH_SHORT).show();
            }
        });

//      Twitter login
        mTwitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_sign_in_button);
        mTwitterLoginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        mTwitterLoginButton.setBackgroundResource(R.drawable.twitter);
        mTwitterLoginButton.setCompoundDrawablePadding(0);
        mTwitterLoginButton.setPadding(0, 0, 0, 0);
        mTwitterLoginButton.setText("");
        Callback twitterCallback = new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                Log.d(TAG, "twitterLogin:success" + result);
                handleTwitterSession(result.data);
          }
            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Log.w(TAG, "twitterLogin:failure", exception);
            }
        };
        mTwitterLoginButton.setCallback(twitterCallback);

//        twitter.setCallback(twitterCallback);


        customfb=(Button) findViewById(R.id.button3);
        customfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("----fb-----");
                loginButton.performClick();
                //TO DO put login method here eg : authorizefacebook();
            }
        });


        customtw=(Button) findViewById(R.id.button4);
        customtw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("----twitter-----");
                mTwitterLoginButton.performClick();
                //TO DO put login method here eg : authorizefacebook();
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();
        email = (EditText) findViewById(R.id.edit_text_email_id);
        password = (EditText) findViewById(R.id.edit_text_password);
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

//     Pass the activity result to the Twitter login button.
        mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);

//     Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void setUpUser() {
        user = new User();
        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());
    }

    public void onSignUpClicked(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void onLoginClicked(View view) {
        setUpUser();
        signIn(email.getText().toString(), password.getText().toString());
    }


   public void clear(){
       email.setText("");
       password.setText("");


   }
    //Sign in With Email
    private void signIn(String email, final String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
               mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
// If sign in fails, display a message to the user. If sign in succeeds the auth state listener will be notified and logic to handle the signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG,"signInWithEmail", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                            String uid = mAuth.getCurrentUser().getUid();
                            intent.putExtra("user_id", uid);

                             startActivity(intent);
//                            finish();
                            clear();
                        }
                        hideProgressDialog();
                    }
                });
          }


    private boolean validateForm() {
        boolean valid = true;
        String userEmail = email.getText().toString();
        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Please enter the Email.");
            valid = false;
        } else {
            email.setError(null);
        }
        String userPassword = password.getText().toString();
        if (TextUtils.isEmpty(userPassword)) {
            password.setError("Please enter the Password.");
            valid = false;
        } else {
            password.setError(null);
        }
        return valid;
    }




    private boolean validateFormPass() {
        boolean valid = true;
        String userEmail = email.getText().toString();
        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Please enter the Email.");
            valid = false;
        } else {
            email.setError(null);
        }

        return valid;
    }






    //     Sign in With google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            String uid=task.getResult().getUser().getUid();
                            String name=task.getResult().getUser().getDisplayName();
                            String email=task.getResult().getUser().getEmail();
                            String image=task.getResult().getUser().getPhotoUrl().toString();
//    Create a new User and Save it in Firebase database
                            User user = new User(uid,name,null,email,null);
                            mRef.child(uid).setValue(user);
                            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                            intent1.putExtra("user_id",uid);
                            intent1.putExtra("profile_picture",image);
                            startActivity(intent1);

//                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                            String uid = mAuth.getCurrentUser().getUid();
//                            intent.putExtra("user_id", uid);
//                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                     }
                });
    }

//Sign in With facebook
    private void signInWithFacebook(AccessToken token) {
        Log.d(TAG, "signInWithFacebook:" + token);
        showProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
//   If sign in fails, display a message to the user.If sign in succeeds the auth state listener will be notified and logic to handle the signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }else{
                            String uid=task.getResult().getUser().getUid();
                            String name=task.getResult().getUser().getDisplayName();
                            String email=task.getResult().getUser().getEmail();
                            String image=task.getResult().getUser().getPhotoUrl().toString();
                            String fb=task.getResult().getUser().toString();
//    Create a new User and Save it in Firebase database
                            User user = new User(uid,name,null,email,null);
                            mRef.child(uid).setValue(user);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("user_id",uid);
                            intent.putExtra("profile_picture",image);
                            intent.putExtra("fb",fb);
                            startActivity(intent);
                        }
                        hideProgressDialog();
                    }
                });

    }




    //  Sign in With Twitter
    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);

        showProgressDialog();
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid=task.getResult().getUser().getUid();
                            String name=task.getResult().getUser().getDisplayName();
                            String email=task.getResult().getUser().getEmail();
                            String image=task.getResult().getUser().getPhotoUrl().toString();
                            String twitter=task.getResult().getUser().toString();
//                     Create a new User and Save it in Firebase database
                            User user = new User(uid,name,null,email,null);
                            mRef.child(uid).setValue(user);
                            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                            intent1.putExtra("user_id",uid);
                            intent1.putExtra("profile_picture",image);
                            intent1.putExtra("twitter",twitter);
                            startActivity(intent1);
                        } else {
 //  If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
              }
                });
    }




    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


