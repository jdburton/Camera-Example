package edu.clemson.cameraexample;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.webkit.WebView;

/**
 * 
 * @author jburton
 *
 * @class DisplayInfoActivity
 * 
 * @brief This class controls activities that displays the information page. 
 */

public class DisplayInfoActivity extends Activity {

	@SuppressLint("NewApi")
	@Override
	/**
	 * @fn protected void onCreate(Bundle savedInstanceState)
	 * @brief Method called when activity is created. 
	 * This method sets the content view to activity_display_info, then creates an WebView view on which it displays the help information.
	 * The help information is stored in an an HTML file in the assets directory. R.string.about_text gives the location of the file.
	 * 
	 * More on scrolling from 	http://stackoverflow.com/questions/16623337/how-to-scroll-table-layout-in-horizontal-and-vertical-in-android 
	 * 
	 * @param savedInstanceState
	 */
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // The about_text resource is the location of the HTML file.
        String message = getResources().getString(R.string.about_html_file);

        // Create the text view - html style.
        /// Webview information at http://stackoverflow.com/questions/3295381/android-html-resource-with-references-to-other-resources
        WebView webView = new WebView(this);
        
        webView.loadUrl(message);

        // Set the text view as the activity layout
        setContentView(webView);
	}

	/**
	 * @fn private void setupActionBar()
	 * 
	 * Set up the android.app.ActionBar, if the API is available.
	 * This enables the up/home button to allow users to return to the main screen.
	 * 
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}


	@Override
	/**
	 * @fn onOptionsItemSelected(MenuItem item) 
	 * This ID represents the Home or Up button. In the case of this
	 * activity, the Up button is shown. Use NavUtils to allow users
	 * to navigate up one level in the application structure. For
	 * more details, see the Navigation pattern on Android Design:
	 * 
	 * http://developer.android.com/design/patterns/navigation.html#up-vs-back	
	 * 
	 * @param item The MenuItem
	 * @return If Home or Up, navigate up and return true.
	 * @return Otherwise, parent class functionality.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:

			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
