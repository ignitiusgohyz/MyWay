package com.example.myway;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarparkRatesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarparkRatesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CarparkRatesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CarparkRatesFragment newInstance(String rate, String night, String free) {
        CarparkRatesFragment fragment = new CarparkRatesFragment();
        Bundle args = new Bundle();
        args.putString("rate", rate);
        args.putString("night", night);
        args.putString("free", free);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate Start>>>", "STARTING...");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("OnCreateView Start>>>", "STARTING...");
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_carpark_rates, container, false);
        String rates = getArguments().getString("rate");
        String night = getArguments().getString("night");
        String free = getArguments().getString("free");
        TextView viewable_rates = getView().findViewById(R.id.carpark_hdb_rates);
        TextView viewable_night = getView().findViewById(R.id.nightParking);
        TextView viewable_free = getView().findViewById(R.id.freeParking);

        viewable_rates.setText(rates);
        viewable_night.setText(night);
        viewable_free.setText(free);
//        Log.d("Fragment Arguments>>>", rates + " AND " + night + " AND " + free);
//        inflater.inflate(R.layout.fragment_carpark_rates, container, false);
        return view;
    }
}