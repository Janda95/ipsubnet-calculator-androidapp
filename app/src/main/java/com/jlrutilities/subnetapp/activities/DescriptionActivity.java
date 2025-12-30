package com.jlrutilities.subnetapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.jlrutilities.subnetapp.fragments.DetailFragment;
import com.jlrutilities.subnetapp.models.Node;
import com.jlrutilities.subnetapp.R;


/**
 * Creates and populates view displaying description information for Subnet Node.
 */
public class DescriptionActivity extends AppCompatActivity {

      private static final String NODE_KEY = "node_key";

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_description);

          Intent intent = getIntent();
          Node node = intent.getParcelableExtra(NODE_KEY);

          DetailFragment fragment = DetailFragment.newInstance(node);
          FragmentManager fragmentManager = getSupportFragmentManager();
          fragmentManager.beginTransaction()
                  .replace(R.id.description_detail_container, fragment)
                  .commit();
      }
}
