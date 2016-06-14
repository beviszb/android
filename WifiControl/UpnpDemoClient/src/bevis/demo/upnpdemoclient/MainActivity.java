package bevis.demo.upnpdemoclient;

import java.io.IOException;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.binding.LocalServiceBindingException;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.action.ActionArgumentValue;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.RegistrationException;
import org.teleal.cling.registry.Registry;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnItemClickListener {
	private ListView deviceListView;
	private ArrayAdapter<DeviceItem> deviceListAdapter;
	private DeviceItem mCurDeviceItem;
	public static MainActivity mInstance;
	public static final String LOGTAG = "UpnpDemo";
	private AndroidUpnpService upnpService;
	private DeviceListRegistryListener deviceListRegistryListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mInstance = this;
		deviceListView = (ListView) findViewById(R.id.deviceList);
		deviceListView.setOnItemClickListener(this);
		deviceListAdapter = new ArrayAdapter<DeviceItem>(this,
				android.R.layout.simple_list_item_1);
		deviceListView.setAdapter(deviceListAdapter);
		deviceListRegistryListener = new DeviceListRegistryListener();
		getApplicationContext().bindService(
	            new Intent(this, DemoUpnpService.class),
	            serviceConnection,
	            Context.BIND_AUTO_CREATE
	        );
		
	}
	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			upnpService = (AndroidUpnpService) service;
			
			if (upnpService == null) {
				return;
			} 
			deviceListAdapter.clear();
			Log.i("zeng", "serviceConnection");
			for (Device device : upnpService.getRegistry().getDevices()) {
				Log.i("zeng", "device:"+device.getDisplayString());
				deviceListRegistryListener.deviceAdded(device);
		    }
			upnpService.getRegistry().addListener(deviceListRegistryListener);
			upnpService.getControlPoint().search();
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
//			if (!(device instanceof RemoteDevice)) {
//				return;
//			}
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
//
//					int position = deviceListAdapter.getPosition(di);
//					if (position >= 0) {
//						// Device already in the list, re-set new value at same
//						// position
//						deviceListAdapter.remove(di);
//						deviceListAdapter.insert(di, position);
//					} else {
						deviceListAdapter.add(di);
//					}
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, ChatActivity.class);
		mCurDeviceItem = deviceListAdapter.getItem(arg2);
		startActivity(intent);
	}
	public void sendMsg(Service messageHandlerService, String msg, String from) {

		@SuppressWarnings("rawtypes")
		ActionInvocation sendMsgInvocation = new SendMsgActionInvocation(messageHandlerService, msg);

		// Executes asynchronous in the background
		upnpService.getControlPoint().execute(
				new ActionCallback(sendMsgInvocation) {

					@Override
					public void success(@SuppressWarnings("rawtypes") ActionInvocation invocation) {
						// assert invocation.getOutput().length == 0;
						@SuppressWarnings("unused")
						int len = invocation.getOutput().length;
						@SuppressWarnings({ "rawtypes", "unused" })
						ActionArgumentValue value = invocation
								.getOutput("Ret");
					}

					@Override
					public void failure(ActionInvocation invocation,
							UpnpResponse operation, String defaultMsg) {
						System.err.println(defaultMsg);
					}
				});
	};
	class SendMsgActionInvocation extends ActionInvocation {

		@SuppressWarnings("unchecked")
		SendMsgActionInvocation(@SuppressWarnings("rawtypes") Service service, String msg) {
			super(service.getAction("SetMsg"));
			try {

				// Throws InvalidValueException if the value is of wrong type
				setInput("Msg", msg);
				setInput("From", android.os.Build.MODEL);

			} catch (InvalidValueException ex) {
				System.err.println(ex.getMessage());
				System.exit(1);
			}
		}
	};
	public void setCurDeviceItem(DeviceItem mCurDeviceItem) {
		this.mCurDeviceItem = mCurDeviceItem;
	}

	public DeviceItem getCurDeviceItem() {
		return mCurDeviceItem;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (upnpService != null) {
			upnpService.getRegistry()
					.removeListener(deviceListRegistryListener);
		}
		getApplicationContext().unbindService(serviceConnection);
	}
}
