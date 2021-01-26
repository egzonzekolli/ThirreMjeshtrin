package com.example.florim.thirremjeshtrin;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Gresa on 28-Nov-16.
 */

public class ConnectToServer {
    public static final String LOG_IN = "http://200.6.254.247/thirremjeshtrin/login.php";
    public static final String REGISTER = "http://200.6.254.247/thirremjeshtrin/register.php";
    public static final String SEARCH = "http://200.6.254.247/thirremjeshtrin/get-list.php";
    public static final String REQUEST = "http://200.6.254.247/thirremjeshtrin/request.php";
    public static final String CHECKREQUEST = "http://200.6.254.247/thirremjeshtrin/checkrequest.php";
    public static final String UPDATETOKEN = "http://200.6.254.247/thirremjeshtrin/updatetoken.php";
    public static final String FEEDBACK = "http://200.6.254.247/thirremjeshtrin/feedback.php";
    public static final String CHAT = "http://200.6.254.247/thirremjeshtrin/chat.php";
    public static final String BUDDIES = "http://200.6.254.247/thirremjeshtrin/buddies.php";
    /**
     * The url to initiate the HTTP connection to
     */
    private String url;
    /**
     * The parameters to be sent with the HTTP request by POST method
     */
    private Map<String, String> urlParameters;
    /**
     * The data returned from the HTTP response, parsed from JSON
     */
    public List<Map<String, String>> results;

    /**
     * The constructor for the class
     *
     * @param url           the url that the object will communicate via HTTP with
     * @param urlParameters the parameters that will be sent with the HTTP request
     */
    public void sendRequest(String url, Map<String, String> urlParameters, boolean isAsync)  {
        this.url = url;
        this.urlParameters = urlParameters;
        if(!isAsync) {
            try {
                this.results = new NetworkTask().execute(this).get();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else{
            new NetworkTask().execute(this);
        }

    }

    /**
     * A subclass of Asynctask that makes network communication possible in the background
     */
    private class NetworkTask extends AsyncTask<ConnectToServer,Void,List<Map<String,String>>> {

        @Override
        protected List<Map<String,String>> doInBackground(ConnectToServer... requestContents) {
            List<Map<String,String>> response=null;
            for (ConnectToServer request : requestContents) {

                response= getResponse(request);
            }

            return response;
        }
        /**
         * Method that creates a URL Connection via HTTP, sends a request by POST method and saves the response as a result variable of the
         */
        private List<Map<String,String>> getResponse(ConnectToServer request) {
            JsonReader jReader;
            try {
                URL url = new URL(request.url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");

                byte[] postData = request.urlParameters().getBytes(StandardCharsets.UTF_8);
                int postDataLength = postData.length;
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                urlConnection.setUseCaches(false);
                urlConnection.getOutputStream().write(postData);

                urlConnection.connect();



                InputStream is = urlConnection.getInputStream();
                jReader = new JsonReader(new InputStreamReader(is, "UTF-8"));
                urlConnection.disconnect();

                return ParseJson(jReader);


            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        /**
         * Method that parses a JSON Array into a Map List
         *
         * @param jsonReader The object that contains unparsed JSON data
         * @return The JSON data, parsed
         */
        private List<Map<String, String>> ParseJson(JsonReader jsonReader) {
            try {
                List<Map<String, String>> results = new ArrayList<>();
                Map<String, String> row;
                if (jsonReader != null) {
                    jsonReader.beginObject();

                    while (jsonReader.hasNext()) {
                        String name=jsonReader.nextName();
                        jsonReader.beginObject();
                        row = new HashMap<>();
                        while (jsonReader.hasNext()) {
                            row.put(jsonReader.nextName(), jsonReader.nextString());
                        }
                        results.add(row);
                        jsonReader.endObject();

                    }
                    jsonReader.endObject();
                }
                return results;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }


    }

    private String urlParameters() {
        StringBuilder parameters = new StringBuilder();
        Iterator entries = urlParameters.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) entries.next();
            parameters.append(thisEntry.getKey()).append("=").append(thisEntry.getValue());
            if (entries.hasNext()) {
                parameters.append("&");
            }

        }
        return parameters.toString();
    }
}

