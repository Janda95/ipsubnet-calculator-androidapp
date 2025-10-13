package com.jlrutilities.subnetapp.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jlrutilities.subnetapp.R;


//** Generates and provides main Activity Fragment  */
public class MainActivityFragment extends Fragment {

  public MainActivityFragment() {}


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);
    return rootView;
  }
}
