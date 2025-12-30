package com.jlrutilities.subnetapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.jlrutilities.subnetapp.adapters.CheatsheetArrayAdapter;
import com.jlrutilities.subnetapp.fragments.IpAdjustmentDialogFragment;
import com.jlrutilities.subnetapp.models.SubnetCalculator;
import com.jlrutilities.subnetapp.R;


/** Creates and populates main entry point for application. */
public class MainActivity extends AppCompatActivity implements IpAdjustmentDialogFragment.IpAdjustmentDialogListener {

    SubnetCalculator subnetCalc;
    EditText inputTextView;
    Spinner spinner;
    AlertDialog.Builder builder;
    private int defaultNetmask;

    private ListView list;
    String[] bitsArr;
    String[] netmaskArr;
    String[] hostsArr;

    protected static final String IP_STRING_MESSAGE = "com.example.IPSTRING.Message";
    protected static final String CIDR_NETMASK_MESSAGE = "com.example.NETMASK.Message";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    //** Sets up reference tables and populates view. */
    private void init() {
        subnetCalc = new SubnetCalculator();

        list = findViewById(R.id.cheatsheet_list);
        setupTableValues();
        ArrayAdapter aa = new CheatsheetArrayAdapter(this, bitsArr, netmaskArr, hostsArr);
        list.setAdapter(aa);

        // Init TextView and Spinner
        inputTextView = findViewById(R.id.ipEntryTv);

        spinner = findViewById(R.id.subnet_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.split_locations, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        defaultNetmask = 16; // default is "/24"
        spinner.setSelection(defaultNetmask);

        // Dialog Builder
        builder = new AlertDialog.Builder(MainActivity.this);
    }


    /** Transition to Splitter View. */
    public void subnetTransition(View view) {
        String message = inputTextView.getText().toString();
        String[] ipArray = message.split("\\.");

        // InputValidation
        boolean check = isValidIp(ipArray);
        if (check == false) {
            return;
        }

        // Get Netmask From Spinner
        Spinner spinner = findViewById(R.id.subnet_spinner);
        String spinnerItem = spinner.getSelectedItem().toString();

        int cidr = Integer.parseInt(spinnerItem);
        String ipBinary = subnetCalc.ipFormatToBinary(message);
        String cutBinary = subnetCalc.trimCidrIp(ipBinary, cidr);
        String ipFormatted = subnetCalc.ipBinaryToFormat(cutBinary);

        message = subnetCalc.ipBinaryToFormat(ipBinary);

        if (ipFormatted.equals(message)){
            Intent intent = new Intent( this, SplitterActivity.class);
            intent.putExtra(IP_STRING_MESSAGE, message);
            intent.putExtra(CIDR_NETMASK_MESSAGE, spinnerItem);
            startActivity(intent);

        } else {
            // Request user input if suggested adjustment is ok before continuing
            IpAdjustmentDialogFragment dialogFragment = IpAdjustmentDialogFragment.newInstance(message, ipFormatted, spinnerItem);
            dialogFragment.show(getSupportFragmentManager(), "DIALOG_FRAGMENT");
        }
    }


    //** Reset input */
    public void clearInput(View view){
        inputTextView.setText("");
        spinner.setSelection(defaultNetmask);
    }


    //** Validate provided IP is within expected parameters. */
    private boolean isValidIp(String[] array) {
        // Number of Tokens
        if (array.length != 4) {
            Toast.makeText(getApplicationContext(),"Invalid IP", Toast.LENGTH_SHORT).show();
            return false;
        }

        Integer[] intArray = new Integer[4];
        for (int i = 0; i < array.length; i++) {
            // Validate integer and set to intArray
            try {
                intArray[i] = Integer.parseInt(array[i]);

            } catch (NumberFormatException nfe) {
                Toast.makeText(getApplicationContext(),"Invalid IP", Toast.LENGTH_SHORT).show();
                return false;
            }

            // First Token of IP cannot be zero
            if (intArray[0] == 0) {
                Toast.makeText(getApplicationContext(),"Invalid IP", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Validate bounds
            if ( intArray[i] >= 256 ) {
                Toast.makeText(getApplicationContext(),"Invalid IP", Toast.LENGTH_SHORT).show();
                return false;

            } else if (intArray[i] < 0) {
                Toast.makeText(getApplicationContext(),"Invalid IP", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }


    /** User declares intent with valid ip/cidr, transitioning to Splitter Activity. */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String ipAddress, String cidr) {
        Intent intent = new Intent( this, SplitterActivity.class);
        intent.putExtra(IP_STRING_MESSAGE, ipAddress);
        intent.putExtra(CIDR_NETMASK_MESSAGE, cidr);

        startActivity(intent);
        dialog.dismiss();
    }


    //** User declares adjusted IP is not valid. */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }


    //** Generates CIDR reference table values. */
    private void setupTableValues(){
        bitsArr = new String[25];
        netmaskArr = new String[25];
        hostsArr = new String[25];

        for(int i = 8; i <= 32; i++){
            bitsArr[i-8] = "/" + i;
            netmaskArr[i-8] = subnetCalc.subnetMask(i);
            hostsArr[i-8] = String.format("%,d", subnetCalc.numberOfHosts(i));
        }
    }
}
