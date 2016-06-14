package com.tima.upnpdemo;

import java.io.File;
import java.io.IOException;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.binding.LocalServiceBindingException;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.ManufacturerDetails;
import org.teleal.cling.model.meta.ModelDetails;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.RegistrationException;
import org.teleal.cling.registry.Registry;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.tima.upnpdemo.R;

public class MainActivity extends Activity implements SurfaceHolder {

	public static final String LOGTAG = "UpnpDemo";

	private AndroidUpnpService upnpService;
	private ListView deviceListView;

	private ArrayAdapter<DeviceItem> deviceListAdapter;

	private DeviceListRegistryListener deviceListRegistryListener;

	public static MainActivity mInstance;

	private DeviceItem mCurDeviceItem;
	private MediaPlayer mediaPlayer;
	private SurfaceView surfaceView;
	private int COMMAND_PLAY = 8;
	private int COMMAND_PAUSE = 9;
	private int COMMAND_STOP = 10;
	private boolean mediaReady;
	private SurfaceHolder mSurfaceHolder;
	private Handler mHandler; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInstance = this;

		setContentView(R.layout.activity_main);
		mediaPlayer = new MediaPlayer();
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
		
		deviceListView = (ListView) findViewById(R.id.deviceList);
		deviceListAdapter = new ArrayAdapter<DeviceItem>(this,
				android.R.layout.simple_list_item_1);
		deviceListView.setAdapter(deviceListAdapter);
		deviceListRegistryListener = new DeviceListRegistryListener();

		getApplicationContext().bindService(
				new Intent(this, DemoUpnpService.class), serviceConnection,
				Context.BIND_AUTO_CREATE);
		init(Environment.getExternalStorageDirectory() + "/testvideo.mp4");
	}

	private void init(final String videoFileName) {
		mHandler= new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:
					mediaReady = true;
					break;
				case 8:
					if (mediaReady) {
						if (!mediaPlayer.isPlaying()) {
							mediaPlayer.start();
							Toast.makeText(MainActivity.this, "播放",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(MainActivity.this, "正在播放",
									Toast.LENGTH_SHORT).show();
						}

					} else {

						Toast.makeText(MainActivity.this, "播放器或文件未准备好",
								Toast.LENGTH_SHORT).show();
					}

					break;
				case 9:
					if (mediaReady) {
						if (mediaPlayer.isPlaying()) {
							mediaPlayer.pause();
							Toast.makeText(MainActivity.this, "暂停",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(MainActivity.this, "未播放",
									Toast.LENGTH_SHORT).show();
						}

					} else {

						Toast.makeText(MainActivity.this, "播放器或文件未准备好",
								Toast.LENGTH_SHORT).show();
					}

					break;
				case 10:
					if (mediaReady) {
						if (mediaPlayer.isPlaying()) {
							mediaPlayer.pause();
							mediaPlayer.seekTo(0);
							Toast.makeText(MainActivity.this, "停止",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(MainActivity.this, "未播放",
									Toast.LENGTH_SHORT).show();
						}
					} else {

						Toast.makeText(MainActivity.this, "播放器或文件未准备好",
								Toast.LENGTH_SHORT).show();
					}

					break;

				default:
					break;
				}
			}
		};
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(final MediaPlayer mp) {
				mHandler.sendEmptyMessage(0);
			}
		});
		
	    mSurfaceHolder=surfaceView.getHolder();
	    mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    mSurfaceHolder.setKeepScreenOn(true);
	    mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
	        @Override
	        public void surfaceCreated(SurfaceHolder holder) {
	        	mSurfaceHolder=holder;
	        	mediaPlayer.setDisplay(mSurfaceHolder);
	        	try {
	    			mediaPlayer.setDataSource(videoFileName);
	    		} catch (IllegalArgumentException e1) {
	    			// TODO Auto-generated catch block
	    			e1.printStackTrace();
	    		} catch (IllegalStateException e1) {
	    			// TODO Auto-generated catch block
	    			e1.printStackTrace();
	    		} catch (IOException e1) {
	    			// TODO Auto-generated catch block
	    			e1.printStackTrace();
	    		}
	    		
	    		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	            try {
					mediaPlayer.prepareAsync();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        @Override
	        public void surfaceDestroyed(SurfaceHolder holder) {  }
	        @Override
	        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }
	    });

	}

	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			upnpService = (AndroidUpnpService) service;

			if (upnpService == null) {
				return;
			}
			// Getting ready for future device advertisements
			upnpService.getRegistry().addListener(deviceListRegistryListener);
			try {
				upnpService.getRegistry().addDevice(createDevice());
			} catch (RegistrationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LocalServiceBindingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			deviceListAdapter.clear();

			for (Device device : upnpService.getRegistry().getDevices()) {
				Log.i("zeng", "device:" + device.getDisplayString());
				deviceListRegistryListener.deviceAdded(device);
			}
			// Refresh device list
			// upnpService.getControlPoint().search();
		}

		public void onServiceDisconnected(ComponentName className) {
			upnpService = null;
		}
	};

	public class DeviceListRegistryListener extends DefaultRegistryListener {

		@Override
		public void localDeviceAdded(Registry registry, LocalDevice device) {
			// TODO Auto-generated method stub
			Log.i("zeng", "localDeviceAdded");
			deviceAdded(device);
		}

		@Override
		public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
			Log.i("zeng", "remoteDeviceAdded");
			deviceAdded(device);
		}

		@Override
		public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
			final DeviceItem display = new DeviceItem(device,
					device.getDisplayString());
			deviceRemoved(display);
		}

		public void deviceAdded(final Device device) {
			// if (!(device instanceof RemoteDevice)) {
			// return;
			// }
			ServiceId serviceId = new UDAServiceId("MessageHandler");

			if (device.findService(serviceId) == null) {
				return;
			}

			DeviceItem item = new DeviceItem(device);
			deviceAdded(item);
		}

		public void deviceAdded(final DeviceItem di) {
			runOnUiThread(new Runnable() {
				public void run() {

					int position = deviceListAdapter.getPosition(di);
					if (position >= 0) {
						// Device already in the list, re-set new value at same
						// position
						deviceListAdapter.remove(di);
						deviceListAdapter.insert(di, position);
					} else {
						deviceListAdapter.add(di);
					}
					deviceListView.setAdapter(deviceListAdapter);
				}
			});
		}

		public void deviceRemoved(final DeviceItem di) {
			runOnUiThread(new Runnable() {
				public void run() {
					deviceListAdapter.remove(di);
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (upnpService != null) {
			upnpService.getRegistry()
					.removeListener(deviceListRegistryListener);
		}
		getApplicationContext().unbindService(serviceConnection);
		mediaReady = false;
		if (null != mediaPlayer) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	// DOC: CREATEDEVICE
	private LocalDevice createDevice() throws ValidationException,
			LocalServiceBindingException, IOException {
		DeviceIdentity identity = new DeviceIdentity(
				UDN.uniqueSystemIdentifier("UpnpDemo"));

		DeviceType type = new UDADeviceType("UpnpDemo", 1);

		DeviceDetails details = new DeviceDetails(android.os.Build.MODEL,
				new ManufacturerDetails(android.os.Build.MANUFACTURER),
				new ModelDetails("UpnpDemo", "A demo for chat", "v1"));

		LocalService<MessageHandler> messageHandlerService = new AnnotationLocalServiceBinder()
				.read(MessageHandler.class);

		messageHandlerService.setManager(new DefaultServiceManager(
				messageHandlerService, MessageHandler.class));

		return new LocalDevice(identity, type, details, messageHandlerService);
	}

	public void messageReceived(String msg,String from) {
		// Message message = mHandler.obtainMessage();
		// Bundle data = new Bundle();
		// data.putString("msg", msg);
		// message.setData(data);
		//
		// mHandler.sendMessage(message);
		if (msg.equals("play")) {
			mHandler.sendEmptyMessage(COMMAND_PLAY);
		}
		if (msg.equals("pause")) {
			mHandler.sendEmptyMessage(COMMAND_PAUSE);
		}
		if (msg.equals("stop")) {
			mHandler.sendEmptyMessage(COMMAND_STOP);
		}
	}

	public void setCurDeviceItem(DeviceItem mCurDeviceItem) {
		this.mCurDeviceItem = mCurDeviceItem;
	}

	public DeviceItem getCurDeviceItem() {
		return mCurDeviceItem;
	}

	@Override
	public void addCallback(Callback arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Surface getSurface() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rect getSurfaceFrame() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCreating() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Canvas lockCanvas() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canvas lockCanvas(Rect arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeCallback(Callback arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFixedSize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFormat(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setKeepScreenOn(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSizeFromLayout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setType(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unlockCanvasAndPost(Canvas arg0) {
		// TODO Auto-generated method stub
		
	}
}
