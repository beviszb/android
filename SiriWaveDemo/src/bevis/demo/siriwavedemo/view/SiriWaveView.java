package bevis.demo.siriwavedemo.view;

import bevis.demo.siriwavedemo.R;
import bevis.demo.siriwavedemo.util.AndroidUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SiriWaveView extends View {

	private double phase = 0;
	private boolean run = false;
	private int ratio;
	private int width;
	private int width_2;
	private int width_4;
	private int height;
	private int height_2;
	private float MAX;
	private float amplitude;
	private float speed;
	private int frequency;

	public SiriWaveView(Context context) {
		super(context);
		init();
	}

	public SiriWaveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		this.ratio = 1;
		this.width = this.ratio * AndroidUtil.getScreenWidth(getContext());
		this.height = this.width/4;
		this.width_2 = this.width / 2;
		this.width_4 = this.width / 4;
		this.height_2 = this.height / 2;
		this.MAX = (this.height_2) - 4;
		this.amplitude = 1;
		this.speed = 0.2f;
		this.frequency = 6;
		start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		run = true;
		if (this.run == false)
			return;
		this.phase = (this.phase + Math.PI * this.speed) % (2f * Math.PI);
		Log.i("zeng", "phase:" + phase);
		this.clear(canvas);
		this._drawLine(canvas, -2, R.color.wave_color_5);
		this._drawLine(canvas, -6, R.color.wave_color_4);
		this._drawLine(canvas, 4, R.color.wave_color_3);
		this._drawLine(canvas, 2, R.color.wave_color_2);
		this._drawLine(canvas, 1, R.color.wave_color_1);
		postInvalidateDelayed(20);
	}

	public void _drawLine(Canvas canvas, int attenuation, int color) {
		Path path = new Path();
		path.moveTo(0, this.height / 2);
		Paint paint = new Paint();
		paint.reset();
		paint.setStrokeWidth(1);
		paint.setStyle(Paint.Style.STROKE);// 设置空心
		if (attenuation == 1) {
			paint.setStrokeWidth(2);
		}
		paint.setColor(getResources().getColor(color));
		float i = -2f;
		while ((i += 0.01) <= 2f) {
			float y = this._ypos(i, attenuation);
			if (Math.abs(i) >= 1.90f)
				y = this.height_2;
			path.lineTo(this._xpos(i), y);
		}
		canvas.drawPath(path, paint);
	};

	public float _xpos(float i) {
		return this.width_2 + i * this.width_4;
	};

	public float _ypos(float i, int attenuation) {
		float att = (this.MAX * this.amplitude) / attenuation;
		return (float) (this.height_2 + this._globAttFunc(i) * att
				* Math.sin(this.frequency * i - this.phase));
	};

	public double _globAttFunc(float x) {
		return Math.pow(4 / (4 + Math.pow(x, 4)), 4);
	};

	public void clear(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		canvas.drawPaint(paint);
	};

	public void start() {
		phase = 0;
		invalidate();
	}

}
