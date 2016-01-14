package bevis.demo.siriwavedemo.view;

import java.util.ArrayList;
import java.util.List;

import bevis.demo.siriwavedemo.R;
import bevis.demo.siriwavedemo.util.AndroidUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SiriWaveViewNine extends View {

	private boolean run = false;
	private int ratio;
	private int width;
	private int height;
	private float MAX;
	private double amplitude;
	private float speed;
	private int[] COLORS = { R.color.green, R.color.blue, R.color.pink };
	private int[] lightColor = { R.color.light_green, R.color.light_blue,
			R.color.light_pink };
	private List<SiriWave9Curve> curvesList = new ArrayList<SiriWaveViewNine.SiriWave9Curve>();
	private SiriWave9Curve[] curves;

	private Canvas mCanvas;

	public SiriWaveViewNine(Context context) {
		super(context);
		init();
	}

	public SiriWaveViewNine(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		this.ratio = 1;
		this.width = this.ratio * AndroidUtil.getScreenWidth(getContext());
		this.height = this.width / 4;
		this.MAX = this.height / 4;
		this.amplitude = 1;
		this.speed = 0.3f;
		curvesList.clear();
		for (int i = 0; i < COLORS.length; i++) {
			int color = COLORS[i];
			for (int j = 0; j < 3; j++) {
				curvesList.add(new SiriWave9Curve(this, color));
			}
			;
		}
		this.curves = new SiriWave9Curve[curvesList.size()];
		for (int j = 0; j < curvesList.size(); j++) {
			curves[j] = curvesList.get(j);
		}
		start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		mCanvas = canvas;
		run = true;
		if (this.run == false)
			return;
		this.clear(canvas);
		Log.i("zeng", "curves.lengt:" + curves.length);
		for (int i = 0, len = this.curves.length; i < len; i++) {
			this.curves[i].draw();
		}
		postInvalidateDelayed(20);
	}

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
		this.run = true;
		invalidate();
	}

	class SiriWave9Curve {
		SiriWaveViewNine controller;
		int color;
		double tick;
		private double open_class = 0;
		private double amplitude;
		private double seed;

		public SiriWave9Curve(SiriWaveViewNine controller, int color) {
			this.controller = controller;
			this.color = color;
			this.tick = 0;
			respawn();
		}

		public void respawn() {
			this.amplitude = 0.3 + Math.random() * 0.7;
			this.seed = Math.random();
			this.open_class = 2 + (int) (Math.random() * 3) | 0;
		}

		double equation(double i) {
			double p = this.tick;
			double y = -1 * Math.abs(Math.sin(p)) * this.controller.amplitude
					* this.amplitude * this.controller.MAX
					* Math.pow(1 / (1 + Math.pow(this.open_class * i, 2)), 2);
			if (Math.abs(y) < 0.001) {
				this.respawn();
			}
			return y;
		};

		public void draw() {
			this._draw(-1);
			this._draw(1);
		}

		public void _draw(int m) {
			this.tick += this.controller.speed
					* (1 - 0.5 * Math.sin(this.seed * Math.PI));

			Canvas ctx = this.controller.mCanvas;
			Path path = new Path();
			path.moveTo(0, controller.height / 2);
			double x_base = this.controller.width
					/ 2
					+ (-this.controller.width / 4 + this.seed
							* (this.controller.width / 2));
			double y_base = this.controller.height / 2;

			double x, y, x_init = 0;
			double i = -3;
			while (i <= 3) {
				x = x_base + i * this.controller.width / 4;
				y = y_base + (m * this.equation(i));
				if (x_init > 0 || x > 0) {
					x_init = 1;
				}
				path.lineTo((float) x, (float) y);
				i += 0.01;
			}
			double h = Math.abs(this.equation(0));
			Paint paint = new Paint();
			// LinearGradient linearGradient = new LinearGradient((float)
			// x_base,
			// (float) y_base, (float) x_base, (float) y_base,
			// Color.RED, Color.YELLOW, Shader.TileMode.MIRROR);
			// paint.setShader(linearGradient);
			RadialGradient radialGradient;
			switch (color) {
			case R.color.green:
				radialGradient = new RadialGradient((float) x_base,
						(float) y_base, (float) (h == 0 ? 1 * 1.15 : h), color,
						getResources().getColor(lightColor[0]),
						Shader.TileMode.CLAMP);
				paint.setShader(radialGradient);
				path.lineTo((float) x_init, (float) y_base);
				path.close();
				ctx.drawPath(path, paint);
				break;
			case R.color.blue:
				radialGradient = new RadialGradient((float) x_base,
						(float) y_base, (float) (h == 0 ? 1 * 1.15 : h), color,
						getResources().getColor(lightColor[2]),
						Shader.TileMode.CLAMP);
				paint.setShader(radialGradient);
				path.lineTo((float) x_init, (float) y_base);
				path.close();
				ctx.drawPath(path, paint);
				break;
			case R.color.pink:
				radialGradient = new RadialGradient((float) x_base,
						(float) y_base, (float) (h == 0 ? 1 * 1.15 : h), color,
						getResources().getColor(lightColor[1]),
						Shader.TileMode.CLAMP);
				paint.setShader(radialGradient);
				path.lineTo((float) x_init, (float) y_base);
				path.close();
				ctx.drawPath(path, paint);
				break;
			default:
				break;
			}
		}
	}

}
