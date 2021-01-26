package com.example.florim.thirremjeshtrin;


import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Login extends AccountAuthenticatorActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private EditText inputEmail;
    private EditText inputPassword;
    private AccountManager mAccountManager;
    private final String TAG = this.getClass().getSimpleName();
    private boolean isPermissionGranted;
    String authtoken;
    String accountType;
    String accountName;
    String password;
    boolean isDataValid;
    Map<String, String> accountData;
    ConnectivityManager cm;

    public final static String ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String AUTH_TYPE = "AUTH_TYPE";
    public final static String ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String IS_ADDING_NEW_ACCOUNT = "IS_ADDING_NEWACCOUNT";
    public static String REFRESH_TOKEN="Refresh";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        isPermissionGranted = false;
        mAccountManager=AccountManager.get(this);
        cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);





                authtoken = FirebaseInstanceId.getInstance().getToken();


                mAuth = FirebaseAuth.getInstance();

                mAuthListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // User is signed in
                            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                        } else {
                            // User is signed out
                            Log.d(TAG, "onAuthStateChanged:signed_out");
                        }
                    }
                };
        if(mAccountManager!=null) {
            isUserRegistered();
        }

        setContentView(R.layout.activity_login);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
                // Login button Click Event
                btnLogin.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        isDataValid=true;
                        accountName = inputEmail.getText().toString().trim();
                        password = inputPassword.getText().toString().trim();

                        // Check for empty data in the form
                        if (!accountName.isEmpty() && !password.isEmpty()) {

                            boolean isNotUsername=!Validation.validateData(accountName,Validation.USERNAME_REGEX);
                            boolean isNotEmail=!Validation.validateData(accountName,Validation.EMAIL_REGEX);
                            if(isNotUsername && isNotEmail){
                                inputEmail.setError(getString(R.string.invalid_username_or_email));
                                isDataValid=false;
                            }
                            if(!Validation.validateData(password,Validation.PASSWORD_REGEX)){
                                inputPassword.setError(getString(R.string.invalid_password));
                                isDataValid=false;
                            }

                            // login user
                            if(isDataValid) {
                                userSignIn();
                            }
                        } else {
                            // Prompt user to enter credentials
                            Toast.makeText(getApplicationContext(),
                                    "Please enter the credentials!", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }

                });

                // Link to Register Screen
                btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(),
                                RegisterAs.class);
                        startActivity(i);
                        finish();
                    }
                });
            }







    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onStart();
        isUserRegistered();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        isPermissionGranted = PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    private void sendRegistrationToServer(String newToken,Map<String,String> Account) {
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String> entry : Account.entrySet()) {
            if (entry.getKey().equals("UserID"))
                params.put("UserID", entry.getValue());
        }
        params.put("Token", newToken);
        if (PermissionUtils.connectivityCheck(cm)) {
            ConnectToServer connectToServer = new ConnectToServer();
            connectToServer.sendRequest(ConnectToServer.UPDATETOKEN, params, true);
        }
        else{
            Toast.makeText(Login.this, R.string.no_connectivity, Toast.LENGTH_LONG).show();
        }
    }
    private void userSignIn() {
        if (PermissionUtils.connectivityCheck(cm)) {
            authtoken = FirebaseInstanceId.getInstance().getToken();
            accountType = Authenticator.ACCOUNT_TYPE;
            ConnectToServer connectToServer = new ConnectToServer();
            Map<String, String> parameters = new HashMap<>();
            parameters.put("account", accountName);
            parameters.put("password", password);
            parameters.put("token", authtoken);
            connectToServer.sendRequest(ConnectToServer.LOG_IN, parameters, false);
            List<Map<String, String>> response = connectToServer.results;

            String message = "";
            String UserID = "";
            String Email = "";
            String Lat = "";
            String Lon = "";
            String Phone = "";
            String Category = "";
            String Location = "";
            String Radius="";

            if (!response.isEmpty()) {
                Map<String, String> data = response.get(0);
                if (data.get("error") != null) {
                    switch (Integer.valueOf(data.get("error"))) {
                        case 0:
                            message = "Incorrect credentials! ";
                            break;
                        case 1:
                            message = "User doesn't exist!";
                            break;
                        case 2:
                            message = "Credentials missing.";
                            break;
                        case 3:
                            message="User already logged in on another device!";
                            break;
                        default: break;

                    }
                }
                if (data.get("UserID") != null) {
                    UserID = data.get("UserID");
                }
                if (data.get("Email") != null) {
                    Email = data.get("Email");
                }
                if (data.get("Lat") != null) {
                    Lat = data.get("Lat");
                }
                if (data.get("Lon") != null) {
                    Lon = data.get("Lon");
                }
                if (data.get("Phone") != null) {
                    Phone = data.get("Phone");
                }
                if (data.get("Category") != null) {
                    Category = data.get("Category");
                }
                if(data.get("Radius")!=null){
                    Radius=data.get("Radius");
                }

            }


            if (message.equals("")) {
                if (!Lat.equals("") && !Lon.equals("")) {
                    Location = getLocation(Lat, Lon);
                }
                Bundle data = new Bundle();
                data.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
                data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                data.putString(AccountManager.KEY_PASSWORD, password);
                // Some extra data about the user
                data.putString(AccountManager.KEY_USERDATA, UserID);
                data.putString("Email", Email);
                data.putString("Phone", Phone);
                data.putString("Location", Location);
                data.putString("Category", Category);
                data.putString("Radius",Radius);


                //Make it an intent to be passed back to the Android Authenticator
                final Intent res = new Intent();
                res.putExtras(data);

                //Create the new account with Account Name and TYPE
                final Account account = new Account(accountName, accountType);

                //Add the account to the Android System
                if (mAccountManager.addAccountExplicitly(account, password, data)) {
                    // worked
                    Log.d(TAG, "Account added");
                    mAccountManager.setAuthToken(account, accountType, authtoken);
                    setAccountAuthenticatorResult(data);
                    setResult(RESULT_OK, res);
                    Log.d("FIREBASE AUTH: ",Email + " " + password);
                    mAuth.signInWithEmailAndPassword(Email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Logger.getLogger(Login.class.getName()).log(Level.ALL, "signInWithEmail:onComplete:" + task.isSuccessful());
                                    //loginProgressDlg.dismiss();
                                    if (!task.isSuccessful()) {
                                        Logger.getLogger(Login.class.getName()).log(Level.ALL, "signInWithEmail", task.getException());
                                        Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        ArrayList<String> defaultRoom = new ArrayList<>();
                                        defaultRoom.add("home");
                                        UserList.user = new ChatUser(task.getResult().getUser().getUid(), task.getResult().getUser().getDisplayName(), task.getResult().getUser().getEmail(), true, defaultRoom);
                                        Intent i = new Intent(Login.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }

                                }
                            });
                } else {
                    // guess not
                    Log.d(TAG, "Account NOT added");
                }

            }
            else{
                Toast.makeText(this,message,Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(Login.this, R.string.no_connectivity, Toast.LENGTH_LONG).show();
        }
    }
    private String getLocation(String lat, String lon) {
        Geocoder g = new Geocoder(this);
        try {
            List<Address> address = g.getFromLocation(Double.valueOf(lat), Double.valueOf(lon), 5);
            if (address.isEmpty()) {
                return "";
            }
            return address.get(0).getLocality();
        } catch (IOException e) {
            return "";
        }
    }
    private void isUserRegistered(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED ) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)){
                Toast.makeText(this,R.string.permission_rationale,Toast.LENGTH_LONG).show();
            }
            // Permission has not been granted yet, request it.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, 1);
        }
        if(mAccountManager!=null) {
            mAccountManager = AccountManager.get(this);
        }
        accountData = Authenticator.findAccount(mAccountManager, this);
        if (accountData != null) {

            Toast.makeText(this, accountType + ", accountName : " + accountName, Toast.LENGTH_LONG);
            for (Map.Entry<String, String> entry : accountData.entrySet()) {
                if (entry.getKey().equals("Token")) {
                    if (!entry.getValue().equals(authtoken)) {
                        sendRegistrationToServer(authtoken, accountData);
                        Authenticator.updateToken(mAccountManager,authtoken,this);
                    }
                }
            }
            mAuth.signInWithEmailAndPassword(accountData.get("Email"), accountData.get("Password"))
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Logger.getLogger(Login.class.getName()).log(Level.ALL, "signInWithEmail:onComplete:" + task.isSuccessful());
                            //loginProgressDlg.dismiss();
                            if (!task.isSuccessful()) {
                                Logger.getLogger(Login.class.getName()).log(Level.ALL, "signInWithEmail", task.getException());
                                Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Log.d("FIREBASE AUTH: ", "onComplete: Succesfully logged into Firebase");
                                ArrayList<String> defaultRoom = new ArrayList<>();
                                defaultRoom.add("home");
                                UserList.user = new ChatUser(task.getResult().getUser().getUid(),
                                        task.getResult().getUser().getDisplayName(), task.getResult().getUser().getEmail(), true, defaultRoom);
                                Intent i = new Intent(Login.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    });
        }

    }
}



