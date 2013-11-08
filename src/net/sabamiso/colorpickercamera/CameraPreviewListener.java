package net.sabamiso.colorpickercamera;

import org.opencv.core.Mat;

public interface CameraPreviewListener {
	void onPreviewFrame(Mat image);
}
