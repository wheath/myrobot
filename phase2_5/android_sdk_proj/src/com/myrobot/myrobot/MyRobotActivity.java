package com.myrobot.myrobot;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;
import android.util.Log;
import android.hardware.usb.UsbManager;
import java.io.IOException;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import android.content.Context;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class MyRobotActivity extends Activity {
  // Declare the UI components
  private ListView listView;
  private ArrayAdapter arrayAdapter;
  private PendingIntent mPermissionIntent = null;
  private static final String ACTION_USB_PERMISSION = "com.myrobot.myrobot.USB_PERMISSION";

  private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_USB_PERMISSION.equals(action)) {
          synchronized (this) {
            UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
            if(device.getVendorId() == 9025) { //&&
               //device.getProductId() == 0xea61) {
               Toast.makeText(getApplicationContext(), 
                "Arduino permission granted", Toast.LENGTH_LONG).show();
              } else {
                //Log.d("USBActivity", "Not the Extech - accepting for debug");
                Toast.makeText(getApplicationContext(), 
                "not Arduino granted permission", Toast.LENGTH_LONG).show();
              }
            } else {
                Log.d("USBActivity", "permission denied for device " + device);
                Toast.makeText(getApplicationContext(), 
                "Arduino permission denied", Toast.LENGTH_LONG).show();
            }
          }
        }
      }
    };

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    this.mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
    registerReceiver(mUsbReceiver, filter);
  }

  public ArrayList<String> getUsbDevicesArray() {

    ArrayList<String> devs = new ArrayList<String>();

    UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

    HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

    if (deviceList == null) {
        //log("deviceList is null");
    } else {    
      Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
      while(deviceIterator.hasNext()){
        UsbDevice device = deviceIterator.next();
	devs.add(Integer.toString(device.getVendorId()));
           //manager.requestPermission(device, this.mPermissionIntent);
      }
    }

    return devs;
  }



/*
      for (final UsbDevice device : mUsbManager.getDeviceList().values()) {
	final List<UsbSerialDriver> drivers =
			 UsbSerialProber.probeSingleDevice(mUsbManager, device);
	if (drivers.isEmpty()) {
	  Log.d(TAG, "  - No UsbSerialDriver available.");
	  devs.add(new DeviceEntry(device, null));
	} else {
	  for (UsbSerialDriver driver : drivers) {
	    result.add(new DeviceEntry(device, driver));
	  }
	}
      }
    }
  }
*/

  public void getUsbDevices(View view) {
    Toast.makeText(getApplicationContext(), 
                   "Get USB Devices Clicked", Toast.LENGTH_LONG).show();

    // Get ListView object from xml
    listView = (ListView) findViewById(R.id.listview);

    

    /*
    String[] values = new String[] { "USB 1", 
                                       "USB 2"
                                     };
    */

    ArrayList<String> values = getUsbDevicesArray();

    arrayAdapter = new ArrayAdapter(this, 
                                   android.R.layout.simple_list_item_1, values);
    listView.setAdapter(arrayAdapter);

    listView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view,
	int position, long id) {
	Toast.makeText(getApplicationContext(),
	  "Click ListItem Number " + position, Toast.LENGTH_LONG).show();
      }
    });
  }

  public void getPermission(View view) {
    Toast.makeText(getApplicationContext(), 
                   "Get Permission Clicked", Toast.LENGTH_LONG).show();
      UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

      HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

      if (deviceList == null) {
        //log("deviceList is null");
      } else {
        //log("deviceList is not null");
        //log("deviceList size: " + deviceList.size());

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice device = deviceIterator.next();
            if(device.getVendorId() == 9025) {  //&&
               Toast.makeText(getApplicationContext(), 
                   "Arduino found attempting get permission", Toast.LENGTH_LONG).show();
               manager.requestPermission(device, this.mPermissionIntent);
            }
        }
      }
  }

  public void afterPermission(View view) {
    Toast.makeText(getApplicationContext(), 
                   "After Permission Clicked", Toast.LENGTH_LONG).show();

    // Get UsbManager from Android.
    UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
    
    // Find the first available driver.
    UsbSerialDriver driver = UsbSerialProber.acquire(manager);
    
    try {
      if (driver != null) {
	driver.open();
	try {
	  driver.setBaudRate(9600);
	  byte buffer[] = new byte[2];
          buffer[0] = 'r';
          buffer[1] = '\n';
	  int numBytesRead = driver.write(buffer, 1000);
	  Toast.makeText(getApplicationContext(), 
		    "Read " + numBytesRead + " bytes.", Toast.LENGTH_LONG).show();
	} catch (IOException e) {
	  // Deal with error.
	  Toast.makeText(getApplicationContext(), 
		    "IOException occurred", Toast.LENGTH_LONG).show();
	} finally {
	  driver.close();
	} 
      }
    } catch (IOException e) {
      // Deal with error.
      Toast.makeText(getApplicationContext(), 
                "IOException occurred 2", Toast.LENGTH_LONG).show();
    }
  }
    
}
