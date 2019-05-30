package com.example.chef.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chef.R;
import com.example.chef.search.SearchActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private EditText mEmail;
    private EditText mPassword;
    private CallbackManager mFacebookCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmail = findViewById(R.id.email_input);
        mPassword = findViewById(R.id.password_input);
        mEmail.setText("");
        mPassword.setText("");
        mFirebaseAuth = FirebaseAuth.getInstance();

        mFacebookCallbackManager = CallbackManager.Factory.create();
        LoginButton facebookLoginButton = findViewById(R.id.facebook_login_button);
        facebookLoginButton.setPermissions("email");

        facebookLoginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                displayToast("Action cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                displayToast("Ups something went wrong");
                Log.d("App-com.example.chef", error.getMessage());
            }
        });

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null) {
                    mFirebaseAuth.signOut();
                }
            }
        };
        if(!accessTokenTracker.isTracking())
            accessTokenTracker.startTracking();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(AccessToken.getCurrentAccessToken() != null)
            Log.d("App-com.example.chef","I am still here");
    }

    //Pass activity result to facebook sdk
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startAppWithoutSigning(View v){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential facebookCredentials= FacebookAuthProvider.getCredential(accessToken.getToken());
        mFirebaseAuth.signInWithCredential(facebookCredentials)
                .addOnCompleteListener(this, authTaskResult -> {
                    if(authTaskResult.isSuccessful()){
                        Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                        startActivity(intent);
                    }
                    else
                        displayToast(authTaskResult.getException().getMessage());
                });
    }



    public void register(View v){

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty()) {
            displayToast(getString(R.string.missing_email_password_message));
            return;
        }

        mFirebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, authTaskResult -> {
                    if(authTaskResult.isSuccessful()) {
                        displayToast("Your account has been successfully registered\nPlease sign in to proceed");
                    }
                    else{
                        displayToast("Registration Failed: " + authTaskResult.getException().getMessage());
                    }
                });
    }

    public void signIn(View v){

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty()) {
            displayToast(getString(R.string.missing_email_password_message));
            return;
        }

        mFirebaseAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(this, authTaskResult -> {
                    if(authTaskResult.isSuccessful()){
                        Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                        startActivity(intent);
                    }
                    else{
                        displayToast(authTaskResult.getException().getMessage());
                    }
                });
    }

    public void displayToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
