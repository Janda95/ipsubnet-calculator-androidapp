package com.jlrutilities.subnetapp.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jlrutilities.subnetapp.R;


//** Creates and populates list view fragment. */
public class ListViewFragment extends Fragment {


    public ListViewFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splitter, container, false);
    }
}
