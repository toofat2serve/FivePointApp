package com.romaka.fivepointapp;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.drawable.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class HelpActivity extends Activity {

   public ListView lv_help; 
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.help_layout);
	  String[] titles = getResources().getStringArray(R.array.string_array_help_titles);
	  String[] texts = getResources().getStringArray(R.array.string_array_help_text);
	  TypedArray imgs = getResources().obtainTypedArray(R.array.integer_array_help_image);
	  ArrayList al = new ArrayList();
	  for (int i = 0; i < titles.length; i++) {
		 ArrayList ial = new ArrayList();
		 ial.add(titles[i]);
		 ial.add(texts[i]);
		 ial.add(imgs.getResourceId(i, 000));
		 al.add(ial);	 
	  }
	  //Log.i("ME", "al: " + al.toString());
	  lv_help = (ListView) findViewById(R.id.help_lv);
      lv_help.setAdapter(new HelpViewAdapter(al));
   }
   
   public class HelpViewAdapter extends BaseAdapter {
	  private ArrayList data;
	  public HelpViewAdapter(ArrayList<ArrayList> arr) 
	  { data = arr;
		 //Log.i("ME", "arr: " + arr.toString());
	  }

	  @Override
	  public int getCount()
	  { return data.size(); }

	  @Override
	  public Object getItem(int _i)
	  { return data.get(_i); }

	  @Override
	  public long getItemId(int p1)
	  { return p1; }

	  @Override
	  public View getView(int position, View cV, ViewGroup group)
	  {
		 LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		 if (cV == null) {
			cV = _inflater.inflate(R.layout.helpitem, null);
		 }
		 ArrayList pal = (ArrayList) data.get(position);
		 TextView title = (TextView) cV.findViewById(R.id.help_item_title);
		 TextView text = (TextView) cV.findViewById(R.id.help_item_text);
		 ImageView img = (ImageView) cV.findViewById(R.id.help_item_image);

		 title.setTag(title.getTag().toString() + String.valueOf(position));
		 title.setText((String) pal.get(0));

		 text.setTag(text.getTag().toString() + String.valueOf(position));
		 text.setText((String) pal.get(1));

		 Drawable image = getDrawable(pal.get(2));
		 img.setTag(img.getTag().toString() + String.valueOf(position));
		 img.setImageDrawable(image);

		 return cV;
	 }  
   }
}
