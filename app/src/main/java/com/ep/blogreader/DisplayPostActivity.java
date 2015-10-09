package com.ep.blogreader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;

public class DisplayPostActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_post);
		
		Intent intent = getIntent();
		Uri blogUri = intent.getData();
		
		WebView webView = (WebView) findViewById(R.id.webView1);
		webView.loadUrl(blogUri.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_blog_web_view, menu);
		return true;
	}

}
