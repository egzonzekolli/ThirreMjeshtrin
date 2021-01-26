package com.example.florim.thirremjeshtrin;

import android.accounts.AccountManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import java.util.Map;


public class UserInfo extends AppCompatActivity implements SendFeedback.OnFragmentInteractionListener, ListFeedback.OnFragmentInteractionListener{


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private AccountManager am;
    com.roughike.bottombar.BottomBar mBottomBar;
    boolean isUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        am = AccountManager.get(this);
        Map<String, String> accountData = Authenticator.findAccount(am, this);
        String userID = accountData.get("UserID");
        isUser=getIntent().getBooleanExtra("isUser",false);
        TabHost tabs=(TabHost)findViewById(R.id.TabHost); //Id of tab host

        tabs.setup();
        mBottomBar = com.roughike.bottombar.BottomBar.attach(this, savedInstanceState);
        mBottomBar.setItems(R.menu.menu_main);
        if(isUser){
            mBottomBar.setDefaultTabPosition(0);
        }
        else{
            mBottomBar.setDefaultTabPosition(1);
        }
        mBottomBar.setOnMenuTabClickListener(new com.roughike.bottombar.OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.profile) {
                    if (!isUser) {
                        Intent i = new Intent(UserInfo.this, UserInfo.class);
                        i.putExtra("isUser", true);
                        startActivity(i);
                    }
                }
                    else if(menuItemId==R.id.inbox){
                        Intent i = new Intent(UserInfo.this, UserList.class);
                        startActivity(i);
                    }


                }


            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.inbox) {
                }
            }
        });

        mBottomBar.mapColorForTab(0,"#F44346");
        mBottomBar.mapColorForTab(2,"#795548");
        String repairmanID = getIntent().getStringExtra("RepairmanID");









        TabHost.TabSpec spec=tabs.newTabSpec("Profile");//make a new tab

        spec.setContent(R.id.Tab1);  //What is in the tab (not an activity but rather a view)
        spec.setIndicator("Profile"); //Name of tab
        tabs.addTab(spec); //Add it
        Profile profile=new Profile();
        Profile.accountData = accountData;
        FragmentTransaction ftProfile = getFragmentManager().beginTransaction();
        ftProfile.add(R.id.Tab1,profile,"").disallowAddToBackStack().commit();


        if(repairmanID !=null || !accountData.get("Category").equals("")) {
            spec = tabs.newTabSpec("Reviews");//make a new tab

            spec.setContent(R.id.Tab2);  //What is in the tab (not an activity but rather a view)
            spec.setIndicator("Reviews"); //Name of tab
            tabs.addTab(spec); //Add it
            ListFeedback list = new ListFeedback();
            if(isUser){
                repairmanID = userID;
            }
            list.RepairmanID = repairmanID;
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.Tab2, list, "").disallowAddToBackStack().commit();

            if (!isUser) {
                spec = tabs.newTabSpec("Feedback"); //Same thing here
                spec.setContent(R.id.Tab3);
                spec.setIndicator("Send Feedback");
                tabs.addTab(spec);
                SendFeedback list2 = new SendFeedback();
                SendFeedback.RepairmanID = repairmanID;
                Log.d("UserInfo: ", userID +" "+ repairmanID);
                list2.UserID = userID;
                FragmentTransaction ft2 = getFragmentManager().beginTransaction();
                ft2.add(R.id.Tab3, list2, "").disallowAddToBackStack().commit();
            }
        }


    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(am==null){
            am=AccountManager.get(this);
        }
        if(Authenticator.findAccount(am,this)==null){
            Intent i =new Intent(this, Login.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
