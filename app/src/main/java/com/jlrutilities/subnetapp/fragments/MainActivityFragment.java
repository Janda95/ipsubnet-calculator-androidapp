package com.jlrutilities.subnetapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.jlrutilities.subnetapp.R;


/**
 * Generates and provides main Activity Fragment.
 */
public class MainActivityFragment extends Fragment {

      public MainActivityFragment() {
          // Required empty public constructor
      }

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
          return inflater.inflate(R.layout.fragment_main, container, false);
      }
}
