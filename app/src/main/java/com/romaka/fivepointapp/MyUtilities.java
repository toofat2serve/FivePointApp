package com.romaka.fivepointapp;
import android.content.*;
import android.widget.*;

class MyUtilities
{
   
   public static void myToast(String str, Context context)
   {
	  CharSequence text = str;
	  int duration = Toast.LENGTH_LONG;
	  Toast toast = Toast.makeText(context, text, duration);
	  toast.show();
   }


    public static String htmlWrapper(String s) {
        return "<!DOCTYPE html><html><head></head><body>" + s + "\n</body></html>";
    }
   
}
