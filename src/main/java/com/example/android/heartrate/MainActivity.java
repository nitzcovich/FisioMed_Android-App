package com.example.android.heartrate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private Button mRunButton,signOutButton;
    public String s;
    private static final int RESULTS_CODE = 123;
    private int mHeartRate;
    public static Context context;
    private FirebaseAuth.AuthStateListener authListener;
    public TextView email;
    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    @Override
    protected void onResume() {
        super.onResume();
    }
/*
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }*/

   /* @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }*/
   /* @Override
    protected void onDestroy() {
        super.onDestroy();
    }*/

    /*@Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }*/
}
