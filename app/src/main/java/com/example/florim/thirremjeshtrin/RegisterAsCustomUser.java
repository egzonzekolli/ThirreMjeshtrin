package com.example.florim.thirremjeshtrin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterAsCustomUser extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputConfirmPassword;
    private static final String TAG = RegisterAsCustomUser.class.getSimpleName();
    private boolean isDataValid;



    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //private SessionManager session;
    //private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputConfirmPassword = (EditText) findViewById(R.id.confirmpassword);
        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        Button btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

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

        
        // Progress dialog
        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                isDataValid=true;
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String confirmPassword = inputConfirmPassword.getText().toString().trim();
                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    if(password.equals(confirmPassword)) {

                        if(!Validation.validateData(name,Validation.USERNAME_REGEX)){
                            inputFullName.setError(getString(R.string.invalid_username));
                            isDataValid=false;
                        }

                        if(!Validation.validateData(email,Validation.EMAIL_REGEX)){
                            inputEmail.setError(getString(R.string.invalid_email));
                            isDataValid=false;
                        }

                        if(!Validation.validateData(password,Validation.PASSWORD_REGEX)){
                            inputPassword.setError(getString(R.string.invalid_password));
                            isDataValid=false;
                        }

                            if(isDataValid) {
                                registerUser(name, email, password);
                            }

                    }
                    else{
                        inputConfirmPassword.setError("Incorrect confirm password!");
                    }
                } else {
                    if(name.isEmpty()){
                        inputFullName.setError("Username field cannot be empty!");
                    }
                    if(email.isEmpty()){
                        inputEmail.setError("Email field cannot be empty!");
                    }
                    if(password.isEmpty()){
                        inputPassword.setError("Password field cannot be empty!");
                    }
                    if(confirmPassword.isEmpty()){
                        inputConfirmPassword.setError("Confirm Password field cannot be empry");
                    }
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        Login.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to RegisterAsCustomUser url
     * */

    private void registerUser(final String username, final String email, final String password) {
        Map<String, String> params = new HashMap<>();
        params.put("password", password);
        params.put("email", email);
        params.put("username", username);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean connectivity = PermissionUtils.connectivityCheck(cm);
        if (connectivity) {
            ConnectToServer connectToServer = new ConnectToServer();
            connectToServer.sendRequest(ConnectToServer.REGISTER, params, false);
            List<Map<String, String>> response = connectToServer.results;

            Map<String, String> success = response.get(0);
            String successful = success.get("registration");

            if (successful.equals("Successful")) {
                Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();
                // firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterAsCustomUser.this, "Auth failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    final ArrayList<String> defaultRoom = new ArrayList<>();
                                    defaultRoom.add("home");

                                    // Update the user profile information
                                    final FirebaseUser user = task.getResult().getUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(username)
                                            .setPhotoUri(Uri.parse("http://1.bp.blogspot.com/-GKLGUFqEMZw/Tq8bXvXqzBI/AAAAAAAAAA0/0RTAmj2IfVU/s1600/250608_213063775394201_201787589855153_659638_3960990_n.jpg"))
                                            .build();
                                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Logger.getLogger(RegisterAsCustomUser.class.getName()).log(Level.ALL, "User profile updated.");
                                                // Construct the ChatUser
                                                UserList.user = new ChatUser(user.getUid(), username, email, true, defaultRoom);
                                                // Setup link to users database
                                                FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).setValue(UserList.user);
                                                // startActivity(new Intent(RegisterAsCustomUser.this, UserList.class));
                                                Intent i = new Intent(RegisterAsCustomUser.this, Login.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
            else {
                Toast.makeText(getApplicationContext(), successful, Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.no_connectivity, Toast.LENGTH_LONG).show();
        }
    }
}
