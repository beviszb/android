package bevis.demo.upnpdemoclient;

import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.UDAServiceId;
import android.app.Activity;
import org.teleal.cling.model.meta.Service;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ChatActivity extends Activity implements OnClickListener {
	
	private DeviceItem device;
	private Button btnPlay;
	private Button btnPause;
	private Button btnStop;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_chat);
		btnPlay=(Button) findViewById(R.id.btnPlay);
		btnPause=(Button) findViewById(R.id.btnPause);
		btnStop=(Button) findViewById(R.id.btnStop);
		device = MainActivity.mInstance.getCurDeviceItem();
		btnPlay.setOnClickListener(this);
		btnPause.setOnClickListener(this);
		btnStop.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btnPlay:
			sendMsg("play");
			break;
		case R.id.btnPause:
			sendMsg("pause");
			break;
		case R.id.btnStop:
			sendMsg("stop");
			break;
		}
	}
	
	private void sendMsg(String command) {		
		ServiceId serviceId = new UDAServiceId("MessageHandler");
		Service messageHandlerService = device.getDevice().findService(serviceId);
		MainActivity.mInstance.sendMsg(messageHandlerService, command, device.toString());
	}
}
