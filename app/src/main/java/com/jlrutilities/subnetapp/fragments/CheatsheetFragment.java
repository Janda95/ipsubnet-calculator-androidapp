package com.jlrutilities.subnetapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.jlrutilities.subnetapp.R;

/**
 * Sets text for Cheatsheet fragment view items.
 */
public class CheatsheetFragment extends Fragment {

      public CheatsheetFragment() {
          // Required empty public constructor
      }

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
          return inflater.inflate(R.layout.fragment_cheatsheet, container, false);
      }
}
