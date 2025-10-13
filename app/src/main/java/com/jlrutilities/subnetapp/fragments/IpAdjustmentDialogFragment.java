package com.jlrutilities.subnetapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jlrutilities.subnetapp.R;


//** Creates and populates Ip adjusted dialog. */
public class IpAdjustmentDialogFragment extends DialogFragment{

  private static final String OG_IP_KEY = "og_ip_key";
  private static final String ADJ_IP_KEY = "adj_ip_key";
  private static final String CIDR_KEY = "cidr_key";
  private String originalIp;
  private String adjustedIp;
  private String cidr;

  IpAdjustmentDialogListener listener;


  //** Defines listener interface. */
  public interface IpAdjustmentDialogListener {
    public void onDialogPositiveClick(DialogFragment dialog, String ipAddress, String cidr);
    public void onDialogNegativeClick(DialogFragment dialog);
  }


  public IpAdjustmentDialogFragment(){}


  //** Populates fragment instance. */
  public static IpAdjustmentDialogFragment newInstance(String originalIp, String adjustedIp, String cidr) {
    Bundle args = new Bundle();
    args.putString(OG_IP_KEY, originalIp);
    args.putString(ADJ_IP_KEY, adjustedIp);
    args.putString(CIDR_KEY, cidr);

    IpAdjustmentDialogFragment fragment = new IpAdjustmentDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }


  /** Attatches listener to context. */
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    // Verify that the host activity implements the callback interface
    try {
      // Instantiate the NoticeDialogListener so we can send events to the host
      listener = (IpAdjustmentDialogListener) context;

    } catch (ClassCastException e) {
      // The activity doesn't implement the interface, throw exception
      throw new ClassCastException(getActivity().toString()
          + " must implement NoticeDialogListener");
    }
  }


  /** Sets placeholder IP informatino. */
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle args = getArguments();
    originalIp = args.getString(OG_IP_KEY);
    adjustedIp = args.getString(ADJ_IP_KEY);
    cidr = args.getString(CIDR_KEY);
  }


  /** Generates IP comparison for provided CIDR before intent switch. */
  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

    LayoutInflater inflater = requireActivity().getLayoutInflater();
    View content = inflater.inflate(R.layout.dialog_ip_check, null);

    //Custom Dialog fragment for persistence
    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setView(content);

    TextView oGTextView = content.findViewById(R.id.dialog_originalIp);
    TextView adjTextView = content.findViewById(R.id.dialog_formattedIp);
    TextView explanationTv = content.findViewById(R.id.dialog_adjusted_ip_explanation);
    oGTextView.setText(originalIp);
    adjTextView.setText(adjustedIp);

    String explanation = "Adjusted IP address for /"+ cidr +".\n\nWould you like to continue?";
    explanationTv.setText(explanation);

    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
        (dialog, which) -> listener.onDialogPositiveClick(this, adjustedIp, cidr));

    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
        (dialog, which) -> listener.onDialogNegativeClick(this));

    alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
      @Override
      public void onShow(DialogInterface arg0) {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(18);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(18);
      }
    });

    return alertDialog;
  }


  /** Returns CIDR. */
  public String getCidr() { return cidr; }


  /** Returns suggested adjusted IP based on CIDR provided */
  public String getAdjustedIp() { return adjustedIp; }
}
