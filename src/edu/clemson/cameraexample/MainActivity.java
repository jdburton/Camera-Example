package edu.clemson.cameraexample;

/**
	 * 
	 */
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * 
 * @author James Burton (jburto2 AT clemson DOT edu)
 *
 * @class MainActivity
 * 
 * @brief This Application uses the Camera API to select and take pictures and video.
 */
public class MainActivity extends Activity  {

	
	// Camera variables.
	private static final int IMAGE_REQUEST_CODE = 1;
	private static final int REQUEST_IMAGE_CAPTURE = 2;
	private static final int REQUEST_VIDEO_CAPTURE = 3;
	private static final int VIDEO_REQUEST_CODE = 4;
	
	// Are we using high or low quality video?
	public static final int LOW_QUALITY_VIDEO = 0;
	public static final int HIGH_QUALITY_VIDEO = 1;
	
	// Where do we store new videos and images. 
	// Should be in the subdirectory for this app, not the gallery
	private static final String IMAGE_DIRECTORY="images";
	private static final String VIDEO_DIRECTORY="videos";
	
	/**
	 * @var private Uri mediaUri
	 * @brief This contains the uri of the newly taken picture or video.
	 * 
	 *  I started with the tutorial at http://developer.android.com/training/camera/photobasics.html, but ran into this problem http://stackoverflow.com/questions/13912095/java-lang-nullpointerexception-on-bundle-extras-data-getextras
	 *  If a new picture or video is saved to an external file and not to the gallery, then the data returned from the intent will be null. Therefore, the uri information must be stored in an instance variable.
	 *  
	 */
	
	private  Uri mediaUri;
	
	/**
	 * @var private VideoView videoView
	 * @brief The associated VideoView for this activity.
	 */
	private VideoView videoView;
	
	/**
	 * @var private ImageView imageView
	 * @brief The associated imageView for this activity.
	 */
	private ImageView imageView;
	
	/**
	 * @var private TextView videoTextView
	 * @brief Information for the videoView
	 */
	
	private TextView videoTextView;
	
	/**
	 * @var private TextView videoTextView
	 * @brief Information for the videoView
	 */
	
	private TextView imageTextView;
	
	

	/**
	 * @fn protected void onCreate(Bundle savedInstanceState)
	 * @brief Method called when activity is created. Sets the content view to activity_main or activity_main_landscape, depending on orientation. 
	 * 
	 * @param savedInstanceState
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set the orientation based on the layout 
	    int orientation = getResources().getConfiguration().orientation; 
	    if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
	    	setContentView(R.layout.activity_main_landscape);
	    }
	    else
	    {
	    	setContentView(R.layout.activity_main);
	    }
		
		/// Information about VideoView http://www.techotopia.com/index.php/Implementing_Video_Playback_on_Android_using_the_VideoView_and_MediaController_Classes

		// Get the VideoView
		videoView = (VideoView) findViewById(R.id.videoView1);	
		//Add the media controller.
		MediaController mediaController = new MediaController(this);
		
		// Display controls at the bottom of the VideoView.
		/// http://stackoverflow.com/questions/3686729/mediacontroller-positioning-over-videoview
		mediaController.setAnchorView(videoView);
		videoView.setMediaController(mediaController);
		
		// Get the text label for the video view
		videoTextView = (TextView)findViewById(R.id.textView1);
		
		// Get the imageView.
		imageView = (ImageView) findViewById(R.id.imageView1);	
		
		// Get the text label for the image view
		imageTextView = (TextView)findViewById(R.id.textView2);
		
		
	}
	
	/**
	 *
	 * @fn public boolean onCreateOptionsMenu(Menu menu)
	 * @brief Inflate the menu; this adds items to the action bar if it is present.
	 * @param menu Menu to be created.
	 * @return true
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
 
        case R.id.action_select_photo:
        	dispatchSelectImageIntent();
        	break;
        	
        case R.id.action_select_video:
        	dispatchSelectVideoIntent();
        	break;
        	
        case R.id.action_take_photo:
        	dispatchTakePictureIntent();
        	break;

        case R.id.action_take_video:
        	dispatchTakeVideoIntent(LOW_QUALITY_VIDEO);
        	break;
        	
        case R.id.action_take_video_hd:
        	dispatchTakeVideoIntent(HIGH_QUALITY_VIDEO);
        	break;
        
        }
 
        return super.onOptionsItemSelected(item);
    }
	
	
	/**
	 *
	 * @fn protected void dispatchSelectImageIntent
	 * @brief Start the intent to select an image.
	 * 
	 * 
	 */
	
    protected void dispatchSelectImageIntent()
    {
    	/// See more about working with an image picker from http://www.vogella.com/tutorials/AndroidCamera/article.html

    	Intent intent = new Intent();
    	// Only get image files
    	intent.setType("image/*");
    	intent.setAction(Intent.ACTION_GET_CONTENT);
    	intent.addCategory(Intent.CATEGORY_OPENABLE);
    	startActivityForResult(intent, IMAGE_REQUEST_CODE);
    	
    }
    
	/**
	 *
	 * @fn protected void dispatchSelectVideoIntent
	 * @brief Start the intent to select an video.
	 * 
	 * 
	 */
	
    public void dispatchSelectVideoIntent()
    {
    	/// See more about working with an image picker from http://www.vogella.com/tutorials/AndroidCamera/article.html

    	// create the intent
    	Intent intent = new Intent();
    	intent.setType("video/*");
    	intent.setAction(Intent.ACTION_GET_CONTENT);
    	intent.addCategory(Intent.CATEGORY_OPENABLE);
    	startActivityForResult(intent, VIDEO_REQUEST_CODE);
    	
    }
    
	/**
	 *
	 * @fn protected void dispatchSelectVideoIntent
	 * @brief Start the intent take a picture with the camera. 
	 * 
	 * 
	 */

    
    public void dispatchTakePictureIntent() {
		
		
		/// Started with this http://developer.android.com/training/camera/photobasics.html
		/// But ran into this problem http://stackoverflow.com/questions/13912095/java-lang-nullpointerexception-on-bundle-extras-data-getextras
    	/// If a new picture or video is saved to an external file and not to the gallery, then the data returned from the intent will be null. Therefore, the uri information must be stored in an instance variable.
		
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) 
        {
            // Create the File where the photo should go
            File photoFile = null;
            try 
            {
                photoFile = createImageFile();
            } catch (IOException ex) 
            {
                // Error occurred while creating the File
            	ex.printStackTrace();
                
            }
            // Continue only if the File was successfully created
            if (photoFile != null)
            {
            	// Save the Uri.
            	mediaUri = Uri.fromFile(photoFile);
            	// Tell the intent to save the output externally.
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,  mediaUri );
            	// Start the activity.
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

	/**
	 *
	 * @fn protected file createImageFile()
	 * @brief Create a .jpg file to hold the image. 
	 * @return New image File.
	 * 
	 */
	
	protected File createImageFile() throws IOException {
	    // Create an image file name as a jpg file.
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = this.getExternalFilesDir(IMAGE_DIRECTORY);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // return the file.
	    return image;
	}
	
	/**
	 *
	 * @fn protected void dispatchSelectVideoIntent
	 * @brief Start the intent take a picture with the camera. 
	 * 
	 * 
	 */
	
    public void dispatchTakeVideoIntent(int videoQuality) {
		


    	/// Started with this http://developer.android.com/training/camera/photobasics.html
    	/// But ran into this problem http://stackoverflow.com/questions/13912095/java-lang-nullpointerexception-on-bundle-extras-data-getextras
    	/// If a new picture or video is saved to an external file and not to the gallery, then the data returned from the intent will be null. Therefore, the uri information must be stored in an instance variable.
    			
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) 
        {
            // Create the File where the video should go
            File videoFile = null;
            try 
            {
                videoFile = createVideoFile(videoQuality);
            } catch (IOException ex) 
            {
                // Error occurred while creating the File
                
            }
            // Continue only if the File was successfully created
            if (videoFile != null)
            {
            	// Save the Uri.
            	mediaUri = Uri.fromFile(videoFile);
            	// Set video quality.
            	// High quality = mp4 video. Good looking, but takes up space.
            	// Low quality = 3gp video. Dumbphone quality video. 
            	intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, videoQuality);
            	
            	// Tell the intent to save the output externally.
                intent.putExtra(MediaStore.EXTRA_OUTPUT,  mediaUri );
            	// Start the activity.
                startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
            }
        }
    }

	/**
	 *
	 * @fn protected file createVideoFile(int videoQuality)
	 * 
	 * 
	 * @brief Create a .jpg file to hold the image. 
	 * @param videoQuality High or low quality video
	 * @return New Video File.
	 * 
	 */
    
	private File createVideoFile(int videoQuality) throws IOException {
	    // Create an video file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String videoFileName = "VID_" + timeStamp + "_";
	    File storageDir = this.getExternalFilesDir(VIDEO_DIRECTORY);
	    String suffix = null;
	    
	    // Set appropriate suffix for video. 
	    // If suffix doesn't match container, your file will have no data and your video will be in the gallery.
	    if (videoQuality == LOW_QUALITY_VIDEO)
	    {
	    	suffix = ".3gp";
	    }
	    else
	    {
	    	suffix = ".mp4";
	    }
	    
	    File video = File.createTempFile(
	        videoFileName,  /* prefix */
	        suffix,         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file
	    return video;
	}
	
	
	/**
	 * @fn protected void onSaveInstanceState(Bundle outState)
	 * @brief Here we store the file url as it will be null after returning from camera
	 * app.
	 * 
	 * 	  
	 *  See #11 at: http://www.androidhive.info/2013/09/android-working-with-camera-api/ 
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	 

	    outState.putParcelable("media_uri", mediaUri);
	}
	 
	/**
	 * @fn protected void onRestoreInstanceState(Bundle savedInstanceState)
	 * @brief Here we restore the fileUri again
	 * 
	 * See #11 at: http://www.androidhive.info/2013/09/android-working-with-camera-api/
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
		mediaUri = savedInstanceState.getParcelable("media_uri");
	}
    
	/**
	 * @fn protected void rotateAndSetImage(ImageView imageView, String path)
	 * @brief Rotate the image to always be in portrait mode. Workaround needed for some Androids.
	 * 
	 * Rotating images from http://www.higherpass.com/Android/Tutorials/Working-With-Images-In-Android/3/
	 */
	

	protected void rotateAndSetImage(ImageView imageView, String path)
	{
    	
        // bitmap factory
        BitmapFactory.Options options = new BitmapFactory.Options();

        // downsizing image as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = 4;

        
        final Bitmap bitmap = BitmapFactory.decodeFile(path,
                options);
	    // is this damn thing sideways? Should be in portrait mode
	    /// Rotating images from http://www.higherpass.com/Android/Tutorials/Working-With-Images-In-Android/3/
	    System.err.println("Bitmap: w="+bitmap.getWidth()+" h="+bitmap.getHeight());
	    if (bitmap.getWidth() <= bitmap.getHeight())
	    {
	    	imageView.setImageBitmap(bitmap);
	    } 
	    else
	    {
	        Matrix mat = new Matrix();
	        mat.postRotate(90);
	        Bitmap bMapRotate = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
	        imageView.setImageBitmap(bMapRotate);
	    }
	}
	
	/**
	 * @fn onActivityResult(int requestCode, int resultCode, Intent data)
	 * @brief Work with results of activity.
	 *
	 * @param requestCode Request Code sent to activity
	 * @param resultCode Result Code returned to activity
	 * @param data Data associated with the intent that called this activity.
	 * 
	 * Big catchall for all the result of all the activities that can be launched by this activity.
	 * Action is determined by requestCode and resultCode. 
	 * 
	 * 	  
	 */
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
		// Image picker returned successfully.
		if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK)
		{
			try
			{
				imageTextView.setText(data.getDataString());
				imageView.setImageURI(data.getData());
			}
			catch (Exception e)
			{
				e.printStackTrace();	
			}
		}
		
		// Video picker returned successfully.
		else if (requestCode == VIDEO_REQUEST_CODE && resultCode == Activity.RESULT_OK)
		{

			try
			{
				
				videoTextView.setText(data.getDataString());
				videoView.setVideoURI(data.getData());
				videoView.start();
				
			}
			catch (Exception e)
			{
				e.printStackTrace();	
			}
			
			
		}
		// Camera successfully took the picture.
		else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) 
		{
			/// Fixed null pointer exceptions from - http://www.androidhive.info/2013/09/android-working-with-camera-api/
	        try {
	        	
	        	imageTextView.setText(mediaUri.getPath());
	        	imageView.setImageURI(mediaUri);
	        	
	        	// Image may be rotated incorrectly. If so, you will need to rotate it.
	        	// Comment out previous line and uncomment next line to do that.
	            // rotateAndSetImage(imageView,mediaUri.getPath());
	        } catch (NullPointerException e) {
	            e.printStackTrace();
	        }
	        
	    }
		// Video camera successfully took the video
		else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) 
		{
			/// Fixed null pointer exceptions from - http://www.androidhive.info/2013/09/android-working-with-camera-api/
		        try {
		        	
					try
					{
						videoTextView.setText(mediaUri.getPath());
						videoView.setVideoPath(mediaUri.getPath());
						videoView.start();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
		        	

		        } catch (NullPointerException e) {
		            e.printStackTrace();
		        }
		        
		    }
		// Image or Video was canceled.
		else if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_VIDEO_CAPTURE) && resultCode == RESULT_CANCELED)
		{
			// Clean up after yourself!
			File file = new File(mediaUri.getPath());
			file.delete();
		}
			
		
	}
		


}
