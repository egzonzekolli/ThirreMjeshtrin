package com.example.florim.thirremjeshtrin;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static android.accounts.AccountManager.KEY_USERDATA;

/**
 * Subclass of AbstractAccountAuthenticator that deals with the authentication
 * of this app's accounts with AccountManager
 */
public class Authenticator extends AbstractAccountAuthenticator {

    public static  String ACCOUNT_TYPE="com.example.florim.thirremjeshtrin";
    public static String AUTHTOKEN_TYPE="User";
    public static String AUTHTOKEN_TYPE_LABEL="Registered user of ThirreMjeshtrin";
    private String TAG = "Authenticator";
    private final Context mContext;

    public Authenticator(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Log.d("ThirreMjeshtrin", TAG + "> addAccount");

        final Intent intent = new Intent(mContext, Login.class);
        intent.putExtra(Login.ACCOUNT_TYPE, accountType);
        intent.putExtra(Login.AUTH_TYPE, authTokenType);
        intent.putExtra(Login.IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {

        Log.d(TAG,"Thirremjeshtirin " + "> getAuthToken");



        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);

        String authToken = FirebaseInstanceId.getInstance().getToken();

        Log.d("Thirremjshtrin", TAG + "> peekAuthToken returned - " + authToken);

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's authToken - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, Login.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(Login.ACCOUNT_TYPE, account.type);
        intent.putExtra(Login.AUTH_TYPE, authTokenType);
        intent.putExtra(Login.ACCOUNT_NAME, account.name);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }


    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (AUTHTOKEN_TYPE.equals(authTokenType))
            return AUTHTOKEN_TYPE_LABEL;
        else
            return authTokenType + " (Label)";
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }
    public static Map<String,String> findAccount(AccountManager mAccountManager,Context context) {
        boolean hasPermission= ContextCompat.checkSelfPermission(context, android.Manifest.permission.GET_ACCOUNTS)== PackageManager.PERMISSION_GRANTED;
        if(hasPermission&&mAccountManager.getAccounts()!=null ) {
            for (Account account : mAccountManager.getAccounts())
                if (TextUtils.equals(account.type, Authenticator.ACCOUNT_TYPE)) {
                    Map<String,String> accountData=new HashMap<>();
                    accountData.put("Name",account.name);
                    accountData.put("Token",mAccountManager.getUserData(account,KEY_AUTHTOKEN));
                    accountData.put("UserID",mAccountManager.getUserData(account,KEY_USERDATA));
                    accountData.put("Email",mAccountManager.getUserData(account,"Email"));
                    accountData.put("Phone",mAccountManager.getUserData(account,"Phone"));
                    accountData.put("Category",mAccountManager.getUserData(account,"Category"));
                    accountData.put("Location",mAccountManager.getUserData(account,"Location"));
                    accountData.put("Radius",mAccountManager.getUserData(account,"Radius"));
                    accountData.put("Password",mAccountManager.getPassword(account));
                    return accountData;
                }
        }
        return null;
    }
    public static void updateToken(AccountManager accountManager,String Token,Context context){
        boolean hasPermission= ContextCompat.checkSelfPermission(context, android.Manifest.permission.GET_ACCOUNTS)== PackageManager.PERMISSION_GRANTED;
        if(hasPermission) {
            Account account=accountManager.getAccountsByType(ACCOUNT_TYPE)[0];
            accountManager.setAuthToken(account,AUTHTOKEN_TYPE,Token);
        }
    }
}
