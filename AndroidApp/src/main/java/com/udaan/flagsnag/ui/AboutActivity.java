package com.udaan.flagsnag.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class AboutActivity extends Activity{
	private WebView helpHtml;
	private TextView version;
	
	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		version = (TextView)findViewById(R.id.help_version);
		version.setText(getResources().getString(R.string.app_name) + " " + getResources().getString(R.string.version));
		
		helpHtml = (WebView)findViewById(R.id.help_web_view);
		helpHtml.getSettings().setJavaScriptEnabled(true);
		helpHtml.setWebViewClient(new HelpViewClient());
		
		helpHtml.loadUrl("file:///android_asset/html/" + getResources().getString(R.string.about_file_name));
	}
	
	private class HelpViewClient extends WebViewClient {
		String anchor = null;
		
		public void onPageFinished(WebView view, String url)
	    {
			Log.d(getClass().toString(), "finished loading URL:" + url);
			
			if (this.anchor != null)
	        {
	            view.loadUrl("javascript:window.location.hash='" + this.anchor + "'");
	            this.anchor = null;
	        }
	    }
		
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
	    {
			Log.d(getClass().toString(), "failed loading URL:" + failingUrl);
	    }
	}
}
