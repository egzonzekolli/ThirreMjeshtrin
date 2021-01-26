package com.example.florim.thirremjeshtrin;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.florim.thirremjeshtrin.ConnectToServer;
import com.example.florim.thirremjeshtrin.PermissionUtils;
import com.example.florim.thirremjeshtrin.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFeedback.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListFeedback#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFeedback extends android.app.Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;

    SimpleAdapter adapter;
    ListView listView;
    ConnectToServer connectToServer;
    public static String RepairmanID;

    public ListFeedback() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFeedback.
     */
    public static ListFeedback newInstance(String param1, String param2) {
        ListFeedback fragment = new ListFeedback();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_feedback, container, false);
        listView = (ListView) view.findViewById(R.id.lstFeedback);

        final Map<String, String> params = new HashMap<>();
        params.put("RepID", RepairmanID);
        Log.d("ListFeedback:", RepairmanID);
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final boolean connectivity = PermissionUtils.connectivityCheck(cm);

        if (connectivity) {
            connectToServer = new ConnectToServer();
            connectToServer.sendRequest(ConnectToServer.FEEDBACK, params, false);
            List<Map<String, String>> response = connectToServer.results;
            if(response!=null){
                adapter = new SimpleAdapter(getActivity(), response, android.R.layout.simple_expandable_list_item_2, new String[]{"Username", "Rating"},
                        new int[]{android.R.id.text1, android.R.id.text2});
                if(adapter != null){
                    listView.setAdapter(adapter);
                }else {
                    listView.setEmptyView(view.findViewById(R.id.empty));
                }
            }
            else{
                listView.setEmptyView(view.findViewById(R.id.empty));
            }
        }
        else {
            Toast.makeText(getActivity(), R.string.no_connectivity, Toast.LENGTH_LONG).show();
        }
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
