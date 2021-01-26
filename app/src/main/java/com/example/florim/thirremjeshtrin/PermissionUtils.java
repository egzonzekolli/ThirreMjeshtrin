package com.example.florim.thirremjeshtrin;


import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

/**
 * Created by Gresa on 30-Nov-16.
 */
/**
 * Utility class for access to runtime permissions.
 */
public abstract class PermissionUtils  {

    public static final int ACCOUNTS_REQUEST_PERMISSION = 1;
    public static final int LOCATION_REQUEST_PERMISSION = 2;





    public static boolean connectivityCheck(ConnectivityManager cm) {

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {

            return false;
        }
        return true;

    }


    public static boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                              @NonNull int[] grantResults) {
        boolean isPermissionGranted;

        switch (requestCode) {
            case ACCOUNTS_REQUEST_PERMISSION: {

            }
            case LOCATION_REQUEST_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted = true;


                } else {
                    isPermissionGranted = false;

                }
                break;
            }
            default: {
                isPermissionGranted = false;
                break;
            }

        }
        return isPermissionGranted;


    }
}


