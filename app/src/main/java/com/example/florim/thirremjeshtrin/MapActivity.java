package com.example.florim.thirremjeshtrin;


import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.roughike.bottombar.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

   private List<Map<String,String>> results;
    private static String LIST_TAG="ListSearch";

    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private String lat;
    private String lon;
    private AccountManager am;
    com.roughike.bottombar.BottomBar mBottomBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String category = String.valueOf(getIntent().getIntExtra("category", -1));
        lat = String.valueOf(getIntent().getDoubleExtra("lat", -1));
        lon = String.valueOf(getIntent().getDoubleExtra("lon", -1));

        getDataFromServer(category, lat, lon);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mBottomBar = com.roughike.bottombar.BottomBar.attach(this, savedInstanceState);
        mBottomBar.setItems(R.menu.menu_main);
        mBottomBar.setDefaultTabPosition(1);
        mBottomBar.setOnMenuTabClickListener(new com.roughike.bottombar.OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.profile) {

                    Intent i = new Intent(MapActivity.this, UserInfo.class);
                    i.putExtra("isUser", true);
                    startActivity(i);

                } else if (menuItemId == R.id.inbox) {

                    Intent i = new Intent(MapActivity.this, UserList.class);
                    startActivity(i);

                }
            }

            /**
             * The method being called when currently visible {@link BottomBarTab} is
             * reselected. Use this method for scrolling to the top of your content,
             * as recommended by the Material Design spec
             *
             * @param menuItemId the reselected tab's id that was assigned in the menu
             *                   xml resource file.
             */
            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });

        mBottomBar.mapColorForTab(0,"#F44346");
        mBottomBar.mapColorForTab(1,"#795548");




        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(am==null){
            am= AccountManager.get(this);
        }
        if(Authenticator.findAccount(am,this)==null){
            Intent i =new Intent(this, Login.class);
            startActivity(i);
        }
    }

    private List<Map<String,String>> searchFromServer(String cat, String lat,String lon){
        ConnectToServer connectToServer=new ConnectToServer();
        Map<String,String> params=new HashMap<>();
        params.put("Cat",cat);
        params.put("Lat",lat);
        params.put("Lon",lon);
        connectToServer.sendRequest(ConnectToServer.SEARCH,params,false);
        return connectToServer.results;
    }
    private void getDataFromServer(String category,String lat,String lon){
        results=searchFromServer(category,lat,lon);
    }
    public List<Map<String,String>> getData(){
        return results;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;

        if(results != null) {

                        int size = results.size();

            for (int i = 0; i < size; i++) {
                latlngs.add(new LatLng(Double.parseDouble(results.get(i).get("Lat")),
                        Double.parseDouble(results.get(i).get("Lon"))));
            }
            final String[] markers = new String[size];

            int sizeLatLon = latlngs.size();
            int countSize = 0;
            for (LatLng point : latlngs) {
                if (countSize < sizeLatLon) {
                    options.position(point);
                    options.draggable(true);
                    options.title(results.get(countSize).get("Username"));
                    options.snippet("Phone number: " + results.get(countSize).get("Phone"));
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    markers[countSize] = results.get(countSize).get("Username");
                    googleMap.addMarker(options);
                }
                countSize++;
            }


            options.position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)));
            options.title("You are here");
            options.snippet("Your current location!");
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            googleMap.addMarker(options);

            CameraUpdate center =
                    CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)), 13);
            mMap.moveCamera(center);



            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                int position=-1;
                @Override
                public void onMarkerDragStart(Marker arg0) {
                    for (int i = 0; i < markers.length; i++) {
                        if (markers[i].equals(arg0.getTitle())) {
                            position = i;
                            break;
                        }
                    }


                    arg0.showInfoWindow();
                }

                @SuppressWarnings("unchecked")
                @Override
                public void onMarkerDragEnd(Marker arg0) {
                    arg0.setPosition(latlngs.get(position));
                }

                @Override
                public void onMarkerDrag(Marker arg0) {
                }
            });

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {

                    int position = -1;
                    String title = marker.getTitle();
                    for (int i = 0; i < markers.length; i++) {
                        if (markers[i].equals(title)) {
                            position = i;
                            break;
                        }
                    }


                    if (position != -1) {
                        Intent intent = new Intent(getApplicationContext(), UserInfo.class);

                        String Username = results.get(position).get("Username");
                        String Email = results.get(position).get("Email");
                        String UserID = results.get(position).get("ID");
                        String Phone = results.get(position).get("Phone");
                        String Lat = results.get(position).get("Lat");
                        String Lon = results.get(position).get("Lon");
                        String Radius = results.get(position).get("Radius");
                        String Category=results.get(position).get("Category");

                        intent.putExtra("RepairmanID", UserID);
                        intent.putExtra("Username", Username);
                        intent.putExtra("Lat", Lat);
                        intent.putExtra("Lon", Lon);
                        intent.putExtra("Radius", Radius);
                        intent.putExtra("Phone", Phone);
                        intent.putExtra("Email", Email);
                        intent.putExtra("Category", Category);
                        startActivity(intent);
                    } else {
                        marker.showInfoWindow();
                    }


                    // Event was handled by our code do not launch default behaviour.
                    return true;
                }
            });
        }
        else
        {
            Toast.makeText(this,R.string.no_results, Toast.LENGTH_LONG).show();
        }

    }
}
