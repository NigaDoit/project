package com.example;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import util.Util;
import util.json;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


public class MainActivity extends Activity {
	JSONArray jsonArray;
	Context context = this;
	Button btn;
	EditText etext;
	private ListView m_listView;
	private ArrayAdapter<String> m_adapter;
	private ArrayList<String> m_arrList;
	   @Override
	    protected void onCreate(Bundle savedInstanceState) {
		   
	        super.onCreate(savedInstanceState);
	        new GetServerInfo().execute();
	        setContentView(R.layout.activity_main);
	        
	        
	        
	       
	        btn=(Button)findViewById(R.id.button1);
	        etext =(EditText)findViewById(R.id.editText1);
	        btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					jsonArray = util.json.JsonRequest(
							etext.getText().toString(),
							true, context);
				}
			});
	        m_listView = (ListView)findViewById(R.id.listView);
	        m_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, m_arrList);
	      //  m_listView.setAdapter(m_adapter);
	    }
	   

	public class GetServerInfo extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			WebView webview = new WebView(context);
			SharedPreferences sp=context.getSharedPreferences("spname", 
					android.content.ContextWrapper.MODE_PRIVATE);
			SharedPreferences.Editor spe = sp.edit();
			spe.putString("UserAgent", 
					webview.getSettings().getUserAgentString()+
					"; android_mobile/" +
							Util.getAppVersion(context)
					);
			spe.commit();
		}
		
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			/*jsonArray = util.json.JsonRequest(
					"http://200.1.4.215:8888/2BAnJinKyo/ch08/Sample.jsp",
					true, context);*/
			return null;
		}

	

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(null==jsonArray){
				return;
			}
			if(0==jsonArray.size()){
				return;
			}
	
		JSONObject jsonObject = (JSONObject)jsonArray.get(0);
		Hashtable<String, String> ht = json.changeJsonHt(jsonObject);
		SharedPreferences sp=context.getSharedPreferences("spname", 
				android.content.ContextWrapper.MODE_PRIVATE);
		SharedPreferences.Editor spe = sp.edit();
		//spe.putString("ab",ht.get("ab"));
	//	spe.putString("bc",ht.get("bc"));
		spe.commit();
		//Log.i("ab",sp.getString("ab", "error"));
		//Log.i("bc",sp.getString("bc", "error"));

		Enumeration<String> key = null;
		Object value = null;
		while (ht.isEmpty()) {
		    key = ht.keys();
		    value = jsonObject.get(key);
		    
		m_arrList.add(key+","+value);
		m_adapter.notifyDataSetChanged();
		}
		
		
		
		
	}
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
