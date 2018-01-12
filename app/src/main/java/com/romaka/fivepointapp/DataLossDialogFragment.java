package com.romaka.fivepointapp;


import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;

import android.support.v4.app.DialogFragment;

public class DataLossDialogFragment extends DialogFragment 
{
   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState)
   {
	  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	  builder.setMessage(R.string.datalosswarning)
		 .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			   mListener.onDialogPositiveClick(DataLossDialogFragment.this);
			}
		 })
		 .setNegativeButton("Stay", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			   mListener.onDialogNegativeClick(DataLossDialogFragment.this);
			   
			}
		 });
	  return builder.create();
   }

   public interface DataLossDialogListener {
	  public void onDialogPositiveClick(DialogFragment dialog);
	  public void onDialogNegativeClick(DialogFragment dialog);
   }

   DataLossDialogListener mListener;

   // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
   @Override
   public void onAttach(Activity activity) {
	  super.onAttach(activity);
	  // Verify that the host activity implements the callback interface
	  try {
		 // Instantiate the NoticeDialogListener so we can send events to the host
		 mListener = (DataLossDialogListener) activity;
	  } catch (ClassCastException e) {
		 // The activity doesn't implement the interface, throw exception
		 throw new ClassCastException(activity.toString()
									  + " must implement NoticeDialogListener");
	  }
   }


}
