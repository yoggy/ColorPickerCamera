package net.sabamiso.colorpickercamera;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class ColorPickerCameraActivity extends Activity implements CameraPreviewListener{

	OverlayView overlay_view;
	CameraPreviewView preview_view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		super.onCreate(savedInstanceState);

		OpenCVLoader.initDebug();

		RelativeLayout layout = new RelativeLayout(this);
	    setContentView(layout);

	    @SuppressWarnings("deprecation")
		int fp = ViewGroup.LayoutParams.FILL_PARENT;
	    
	    overlay_view = new OverlayView(this);
		preview_view = new CameraPreviewView(this, 640, 480, this);
		layout.addView(preview_view, new RelativeLayout.LayoutParams(fp, fp));
				
		layout.addView(overlay_view, new RelativeLayout.LayoutParams(fp, fp));
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		preview_view.stop();
		super.onPause();
		finish();
	}

	@Override
	public void onPreviewFrame(Mat image) {
		if (image == null || image.empty() == true) return;
		
		int w = image.cols();
		int h = image.rows();
		double [] p = image.get(h/2, w/2);
		if (p == null || p.length < 3) return;
		
		overlay_view.setRGB((int)p[2], (int)p[1], (int)p[0]);
	}
}
