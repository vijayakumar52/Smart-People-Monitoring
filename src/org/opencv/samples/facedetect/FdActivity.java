package org.opencv.samples.facedetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.adsonik.smartpeoplemonitoring.R;

public class FdActivity extends Activity implements CvCameraViewListener {

	Bitmap bitmap ;
	MediaPlayer mp;
	String file_path;
	Button btn;
	 File dir ;
	 File file ;
	 FileOutputStream fOut ;
	 Calendar c;
	 String id=null;
	 String id1=null;
	 int fCount=0;
	 int thresh=1;
    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;

    private MenuItem               mItemFace50;
    private MenuItem               mItemFace40;
    private MenuItem               mItemFace30;
    private MenuItem               mItemFace20;
    private MenuItem               mItemType;

    private Mat                    mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile;
    private CascadeClassifier      mJavaDetector;
    private DetectionBasedTracker  mNativeDetector;

    private int                    mDetectorType       = JAVA_DETECTOR;
    private String[]               mDetectorName;

    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;

    private CameraBridgeViewBase   mOpenCvCameraView;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("detection_based_tracker");

                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.haarcascade_upperbody);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "haarcascade_upperbody.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public FdActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.face_detect_surface_view);
		Typeface tf1=Typeface.createFromAsset(this.getAssets(), "title.TTF");
		mp=MediaPlayer.create(FdActivity.this, R.raw.tone1);
        id=getIntent().getExtras().getString("radio");
        id1=getIntent().getExtras().getString("count");
        thresh=Integer.parseInt(id1);
        btn=(Button)findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder builder = new Builder(FdActivity.this);
	            final EditText input = new EditText(FdActivity.this);
	            builder
	                .setTitle("Threshold Limit")
	                .setMessage("Please Enter the threshold limit for people to make an Alarm")
	                .setView(input)
	                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

	                    public void onClick(DialogInterface dialog, int which) {
	                        String value = input.getText().toString();
	                        if (input.getText().toString().trim().length() == 0) {
	                            Toast.makeText(FdActivity.this, "Please Enter some value", Toast.LENGTH_SHORT).show();
	                        } else {
	                        	Integer val=Integer.parseInt(value);
	                           thresh=val;
	                        }
	                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
	                    }
	                })
	                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

	                    public void onClick(DialogInterface dialog, int which) {
	                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
	                    }

	                });

	                builder.show();
	                input.requestFocus();
	                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	                
				
				
			}
		});
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        c= Calendar.getInstance(); 
   	
    }

    @Override
    public void onPause()
    {
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(Mat inputFrame) {

        inputFrame.copyTo(mRgba);
        Imgproc.cvtColor(inputFrame, mGray, Imgproc.COLOR_RGBA2GRAY);

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);
        }
        else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++){
        	
        	
        	 Rect r = facesArray[i];
             r.x += Math.abs(r.width*0.1);
 	        r.width = (int) Math.abs(r.width*0.8);
 	        r.y += Math.abs(r.height*0.06);
 	        r.height = (int) Math.abs(r.height*0.9);
 	     
            Core.putText(mRgba," "+(i+1),new Point((r.tl().x+r.br().x)/2,(facesArray[i].tl().y+facesArray[i].br().y)/2) , 3, 1, new Scalar(80,90,35),2);

            Core.rectangle(mRgba, r.tl(), r.br(), FACE_RECT_COLOR, 3);
        }
        Core.putText(mRgba, "Current People Count: "+facesArray.length,new Point(40,40) , 3, 1, new Scalar(133,200,13),2);
        
        if(thresh<=facesArray.length){

        	if(id.contentEquals("first")){
        	if(!mp.isPlaying()){
        	mp.start();
        	}
        	}
        	else if(id.contentEquals("second")){
        	bitmap= Bitmap.createBitmap(mOpenCvCameraView.getWidth()/4,mOpenCvCameraView.getHeight()/4, Bitmap.Config.ARGB_8888);
            try {
                  bitmap = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
                  Utils.matToBitmap(mRgba, bitmap);
                file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Smart People Monitor";
                dir= new File(file_path);
               if(!dir.exists()){
            	   dir.mkdirs();
               }
               int year_=c.get(Calendar.YEAR);
               int date_=c.get(Calendar.DATE);
               int minutes_=c.get(Calendar.MINUTE);
               int seconds = c.get(Calendar.SECOND);
          file= new File(dir, "screenshot"+"_" +year_+"_"+date_+"_"+minutes_+"_"+String.valueOf(fCount++) + ".png");
       fOut= new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
        fOut.flush();
        fOut.close();
            }catch(Exception ex){
                  System.out.println(ex.getMessage());
            }
        }
       
        else if(id.contentEquals("third")){
        	if(!mp.isPlaying()){
        	mp.start();
        	}
        	bitmap= Bitmap.createBitmap(mOpenCvCameraView.getWidth()/4,mOpenCvCameraView.getHeight()/4, Bitmap.Config.ARGB_8888);
            try {
                  bitmap = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
                  Utils.matToBitmap(mRgba, bitmap);
                file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Smart People Monitor";
                dir= new File(file_path);
               if(!dir.exists()){
            	   dir.mkdirs();
               }
               int year_=c.get(Calendar.YEAR);
               int date_=c.get(Calendar.DATE);
               int minutes_=c.get(Calendar.MINUTE);
               int seconds = c.get(Calendar.SECOND);
               file= new File(dir, "screenshot"+"_" +year_+"_"+date_+"_"+minutes_+"_"+String.valueOf(fCount++) + ".png");
       fOut= new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
        fOut.flush();
        fOut.close();
            }catch(Exception ex){
                  System.out.println(ex.getMessage());
            }
        }
        }
        
        return mRgba;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemFace50 = menu.add("Human size 50%");
        mItemFace40 = menu.add("Human size 40%");
        mItemFace30 = menu.add("Human size 30%");
        mItemFace20 = menu.add("Human size 20%");
        mItemType   = menu.add(mDetectorName[mDetectorType]);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item == mItemFace50)
            setMinFaceSize(0.5f);
        else if (item == mItemFace40)
            setMinFaceSize(0.4f);
        else if (item == mItemFace30)
            setMinFaceSize(0.3f);
        else if (item == mItemFace20)
            setMinFaceSize(0.2f);
        else if (item == mItemType) {
            mDetectorType = (mDetectorType + 1) % mDetectorName.length;
            item.setTitle(mDetectorName[mDetectorType]);
            setDetectorType(mDetectorType);
        }
        return true;
    }
    

    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if(mp.isPlaying()){
			mp.stop();
		}
		mp.release();
		
	}

	private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    private void setDetectorType(int type) {
        if (mDetectorType != type) {
            mDetectorType = type;

            if (type == NATIVE_DETECTOR) {
                Log.i(TAG, "Detection Based Tracker enabled");
                mNativeDetector.start();
            } else {
                Log.i(TAG, "Cascade detector enabled");
                mNativeDetector.stop();
            }
        }
    }
    
}
