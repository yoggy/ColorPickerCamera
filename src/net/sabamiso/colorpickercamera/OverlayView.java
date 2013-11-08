package net.sabamiso.colorpickercamera;

import android.annotation.SuppressLint;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class OverlayView extends View {

	Paint p_target;
	String color_str = "";
	int color_r;
	int color_g;
	int color_b;
	int color_h;
	int color_s;
	int color_v;
	int h_color_r;
	int h_color_g;
	int h_color_b;

	ClipboardManager cm;

	public OverlayView(Context context) {
		super(context);

		cm = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);

		p_target = new Paint();
		p_target.setColor(Color.GREEN);
		p_target.setStyle(Style.STROKE);
		p_target.setStrokeWidth(5);
	}

	public void setRGB(int r, int g, int b) {
		color_r = r;
		color_g = g;
		color_b = b;

		color_str = String.format("#%02x%02x%02x", r, g, b);

		convertToHSV();
		createHColor();

		invalidate();
	}

	@SuppressLint("DrawAllocation")
	public void onDraw(Canvas canvas) {
		int w = this.getWidth();
		int h = this.getHeight();
		int cx = w / 2;
		int cy = h / 2;
		int size = 15;

		// target scope
		canvas.drawRect(cx - size, cy - size, cx + size, cy + size, p_target);

		// pickup color
		drawColor(canvas, 40, 20, 140, 120, color_r, color_g, color_b);
		drawText(canvas, color_str, 20, 180, 48);
		drawText(canvas, String.format("R=%3d", color_r),  20, 240, 48);
		drawText(canvas, String.format("G=%3d", color_g), 180, 240, 48);
		drawText(canvas, String.format("B=%3d", color_b), 340, 240, 48);
		drawText(canvas, String.format("H=%3d", color_h),  20, 300, 48);
		drawText(canvas, String.format("S=%3d", color_s), 180, 300, 48);
		drawText(canvas, String.format("V=%3d", color_v), 340, 300, 48);

		// H only color
		drawColor(canvas, 40, 360, 140, 460, h_color_r, h_color_g, h_color_b);
		drawText(canvas, String.format("#%02x%02x%02x", h_color_r, h_color_g, h_color_b), 20, 520, 48);
		drawText(canvas, String.format("R=%3d", h_color_r),  20, 580, 48);
		drawText(canvas, String.format("G=%3d", h_color_g), 180, 580, 48);
		drawText(canvas, String.format("B=%3d", h_color_b), 340, 580, 48);

	}

	private void drawColor(Canvas canvas, int left, int top, int right,
			int bottom, int r, int g, int b) {

		Paint p_hcolor = new Paint();
		p_hcolor.setARGB(255, r, g, b);
		p_hcolor.setStyle(Style.FILL);
		p_hcolor.setStrokeWidth(1);
		canvas.drawRect(left, top, right, bottom, p_hcolor);

		Paint p_black = new Paint();
		p_black.setColor(Color.BLACK);
		p_black.setStyle(Style.STROKE);
		p_black.setStrokeWidth(5);
		canvas.drawRect(left, top, right, bottom, p_black);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			cm.setText(color_str);
			Toast.makeText(getContext(), "copy color code to clipboard...",
					Toast.LENGTH_LONG).show();
			break;
		}
		return true;
	}

	void drawText(Canvas canvas, String msg, float x, float y, int size) {
		Paint p = new Paint();
		p.setTypeface(Typeface.MONOSPACE);
		p.setTextSize(size);
		p.setColor(Color.BLACK);

		int w = 5;

		for (int dy = -w; dy <= w; ++dy) {
			for (int dx = -w; dx <= w; ++dx) {
				if (dx * dx + dy * dy > w * w)
					continue;
				canvas.drawText(msg, x + dx, y + dy, p);
			}
		}

		p.setColor(Color.WHITE);
		canvas.drawText(msg, x, y, p);
	}

	void convertToHSV() {
		float max = Math.max(color_r, Math.max(color_g, color_b));
		float min = Math.min(color_r, Math.min(color_g, color_b));

		if (max == min) {
			color_h = 0;
		} else if (max == color_r) {
			color_h = (int) ((60 * (color_g - color_b) / (max - min) + 360) % 360);
		} else if (max == color_g) {
			color_h = (int) ((60 * (color_b - color_r) / (max - min)) + 120);
		} else if (max == color_b) {
			color_h = (int) ((60 * (color_r - color_g) / (max - min)) + 240);
		}

		if (max == 0) {
			color_s = 0;
		} else {
			color_s = (int) (255 * ((max - min) / max));
		}

		color_v = (int) max;
	}

	void createHColor() {
		int h = color_h;
		int s = 255;
		int v = 255;

		float f;
		int i, p, q, t;

		i = (int) Math.floor(h / 60.0f) % 6;
		f = (float) (h / 60.0f) - (float) Math.floor(h / 60.0f);
		p = (int) Math.round(v * (1.0f - (s / 255.0f)));
		q = (int) Math.round(v * (1.0f - (s / 255.0f) * f));
		t = (int) Math.round(v * (1.0f - (s / 255.0f) * (1.0f - f)));

		switch (i) {
		case 0:
			h_color_r = v;
			h_color_g = t;
			h_color_b = p;
			break;
		case 1:
			h_color_r = q;
			h_color_g = v;
			h_color_b = p;
			break;
		case 2:
			h_color_r = p;
			h_color_g = v;
			h_color_b = t;
			break;
		case 3:
			h_color_r = p;
			h_color_g = q;
			h_color_b = v;
			break;
		case 4:
			h_color_r = t;
			h_color_g = p;
			h_color_b = v;
			break;
		case 5:
			h_color_r = v;
			h_color_g = p;
			h_color_b = q;
			break;
		}
	}
}
