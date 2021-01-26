package com.example.florim.thirremjeshtrin;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GiveFeedback extends AppCompatActivity {

    EditText feedback;
    Button btnSendFeedback;
    RatingBar rb;
    String RequestID;
    String UserID;
    ConnectToServer connectToServer;

    int ratingValue=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_feedback);




            RequestID = getIntent().getStringExtra("RequestID");
            UserID = getIntent().getStringExtra("UserID");



        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        final boolean connectivity = PermissionUtils.connectivityCheck(cm);

        feedback = (EditText)findViewById(R.id.txtFeedback);
        btnSendFeedback = (Button)findViewById(R.id.btnSendFeedback);
        rb = (RatingBar)findViewById(R.id.ratingBar);
            rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    ratingValue = (int)v + 1;
                }
            });
            btnSendFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ratingValue != -1) {
                        String feed = feedback.getText().toString();

                            final Map<String, String> params = new HashMap<>();
                            params.put("UserID", UserID);
                            params.put("ReqID", RequestID);
                            params.put("Feedback", feed);
                            params.put("Rating", String.valueOf(ratingValue));

                            if (connectivity) {
                                connectToServer = new ConnectToServer();
                                connectToServer.sendRequest(ConnectToServer.FEEDBACK, params, false);
                                List<Map<String, String>> response = connectToServer.results;
                                Map<String, String> result = new HashMap<>();
                                result = response.get(0);
                                if (result.get("message").equals("INSERTED")) {
                                    Toast.makeText(getApplicationContext(), "SENT!", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "ERROR!", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.no_connectivity, Toast.LENGTH_LONG).show();
                            }

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_rating, Toast.LENGTH_LONG).show();
                    }
                }
            });


    }
}
