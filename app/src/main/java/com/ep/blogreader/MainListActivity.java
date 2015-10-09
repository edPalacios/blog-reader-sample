package com.ep.blogreader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainListActivity extends ListActivity implements UpdateJsonBlog{
	
	public static final int POSTS_TO_SHOW = 20;
	public static final String TAG = MainListActivity.class.getSimpleName();
	private JSONObject jsonBlog;
	public static final String TITLE = "title";
	public static final String AUTHOR = "author";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        if (isNetworkAvailable()) {
        	progressBar.setVisibility(View.VISIBLE);
			GetPostAsyncTask getPostAsyncTask
					= new GetPostAsyncTask(this, progressBar, jsonBlog, MainListActivity.this);
			getPostAsyncTask.execute();
		}
        else {
        	Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_LONG).show();
        }
        

    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	try {
    		JSONArray jsonPosts = jsonBlog.getJSONArray("posts");
    		JSONObject jsonPost = jsonPosts.getJSONObject(position);
    		String blogUrl = jsonPost.getString("url");

			Intent intent = getIntent(blogUrl);
    		startActivity(intent);
    	}
    	catch (JSONException e) {
    		logException(e);
    	}
    }

	@NonNull
	private Intent getIntent(String blogUrl) {
		Intent intent = new Intent(this, DisplayPostActivity.class);
		intent.setData(Uri.parse(blogUrl));
		return intent;
	}

	private void logException(Exception e) {
    	Log.e(TAG, "Exception caught!", e);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		
		boolean isAvailable = false;
		if (networkInfo != null && networkInfo.isConnected()) {
			isAvailable = true;
		}
		
		return isAvailable;
	}

	@Override
	public void update(JSONObject jsonBlog) {
		this.jsonBlog = jsonBlog;
	}

    
}
