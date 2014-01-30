package com.myrobot.myrobot;

import java.util.concurrent.TimeUnit;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.net.CookieHandler;
import java.net.CookieManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import java.util.List;
import org.apache.http.entity.StringEntity;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONArray;
 
import android.app.Activity;
import android.os.AsyncTask;

import com.opentok.android.Connection;
import com.opentok.android.OpentokException;
import com.opentok.android.Publisher;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

public class MyRobotActivity extends Activity implements Session.Listener,
Publisher.Listener, Subscriber.Listener {

   private static final String LOGTAG = "myrobot";

   private static final String SESSION_ID = "1_MX4yMjA4OTk2Mn5-RnJpIERlYyAyNyAyMzozMDo0NiBQU1QgMjAxM34wLjI3OTE3NzI1fg";
    // Replace with your generated Token (use Project Tools or from a server-side library)
    private static final String TOKEN = "T1==cGFydG5lcl9pZD0yMjA4OTk2MiZzZGtfdmVyc2lvbj10YnJ1YnktdGJyYi12MC45MS4yMDExLTAyLTE3JnNpZz1iMTgzNmM3MWRlOWYzMzIwZmQ2N2I2NDkzYTc2MjFhYTMyMjkwYTg2OnJvbGU9cHVibGlzaGVyJnNlc3Npb25faWQ9MV9NWDR5TWpBNE9UazJNbjUtUm5KcElFUmxZeUF5TnlBeU16b3pNRG8wTmlCUVUxUWdNakF4TTM0d0xqSTNPVEUzTnpJMWZnJmNyZWF0ZV90aW1lPTEzODgyMTU5MDQmbm9uY2U9MC4xNTYxOTYzMDk4MTQ2NTc4MiZleHBpcmVfdGltZT0xMzkwODA4MzY1JmNvbm5lY3Rpb25fZGF0YT0=";

  // Replace with a generated Session ID
  // Replace with a generated token (from the dashboard or using an OpenTok server SDK)
  // Replace with your OpenTok API key
  private static final String API_KEY= "22089962";

  private String K_API_KEY= "";
  private String K_TOKEN = "";
  private String K_SESSION_ID = "";
 
  private Session mSession;
  private Publisher mPublisher;
  private Subscriber mSubscriber;

  private boolean is_logged_in = false;

  // Declare the UI components
  private ListView listView;
  private ArrayAdapter arrayAdapter;
  private String sess_id = "";
  private String sess_name = "";
  private String cellbot_nid = "";
  private TextView statusTextView = null;
  private boolean is_available = false;
  private PendingIntent mPermissionIntent = null;
  private static final String ACTION_USB_PERMISSION = "com.myrobot.myrobot.USB_PERMISSION";
  private HttpClient httpClient = null;

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
    CookieHandler.setDefault(new CookieManager());
    setContentView(R.layout.main);
    statusTextView = (TextView) findViewById(R.id.status);
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
  public void changeCbCmdToProcessed(View view) {
    Toast.makeText(getApplicationContext(), 
                   "Change to Processed Clicked", Toast.LENGTH_LONG).show();

    new ChangeNodeToProcessedTask().execute("364");
  }

  public void publishStream(View view) {
    Toast.makeText(getApplicationContext(), 
                   "Publish Stream Clicked", Toast.LENGTH_LONG).show();

    sessionConnect();
  }

  private void sessionConnect() {
    mSession = Session.newInstance(MyRobotActivity.this, K_SESSION_ID, MyRobotActivity.this);
    mSession.connect(K_API_KEY, K_TOKEN);
  }
  
  public void onSessionConnected() {
    mPublisher = Publisher.newInstance(MyRobotActivity.this, MyRobotActivity.this, "publisher");
    mSession.publish(mPublisher);
  }

  @Override
    public void onSessionDroppedStream(Stream stream) {
        /*
        mStreams.remove(stream);
                if (mSubscriber.getStream().getStreamId().equals(stream.getStreamId())) {
                        subscriberViewContainer.removeView(mSubscriber.getView());
                        mSubscriber = null;
                        if (!mStreams.isEmpty()) {
                                subscribeToStream(mStreams.get(0));
                        }
                }
       */
    }

  @Override
    public void onSessionReceivedStream(Stream stream) {
       /*
        boolean isMyStream = mSession.getConnection().equals(stream.getConnection());

        if ((SUBSCRIBE_TO_SELF && isMyStream) || (!SUBSCRIBE_TO_SELF && !isMyStream)) {
                mStreams.add(stream);
                if (mSubscriber == null) {
                        subscribeToStream(stream);
                }
        }
      */
    }

  @Override
    public void onSessionDisconnected() {
        /*
        if (mPublisher != null) {
                publisherViewContainer.removeView(mPublisher.getView());
        }

        if (mSubscriber != null) {
                subscriberViewContainer.removeView(mSubscriber.getView());
        }

        mPublisher = null;
        mSubscriber = null;
        mStreams.clear();
        mSession = null;
        */
    }

  @Override
    public void onSubscriberConnected(Subscriber subscriber) {
        Log.i(LOGTAG, "Subscriber connected.");
    }

  @Override
        public void onSubscriberVideoDisabled(Subscriber subscriber) {
                Log.i(LOGTAG, "Video quality changed. It is disabled for the subscriber.");
        }

  @Override
    public void onSubscriberException(Subscriber subscriber,
                OpentokException exception) {
        Log.i(LOGTAG, "Subscriber exception: " + exception.getMessage());
    }

  @Override
    public void onPublisherChangedCamera(int newCameraId) {
        Log.i(LOGTAG, "The publisher changed camera.");
    }

  @Override
    public void onPublisherStreamingStarted() {
        Log.i(LOGTAG, "The publisher started streaming.");
    }

  @Override
    public void onPublisherStreamingStopped() {
        Log.i(LOGTAG, "The publisher stopped streaming.");
    }

  @Override
  public void onSessionException(OpentokException exception) {
      Log.i("_dbg", "Session exception: " + exception.getMessage());
  }

  @Override
    public void onPublisherException(OpentokException exception) {
        Log.i(LOGTAG, "Publisher exception: " + exception.getMessage());
    }

    @Override
    public void onSessionCreatedConnection(Connection connection) {
        Log.i(LOGTAG, "New client connected to the session.");
    }

    @Override
    public void onSessionDroppedConnection(Connection connection) {
        Log.i(LOGTAG, "A client disconnected from the session.");
    }

  public void goAvailable(View view) {
    Toast.makeText(getApplicationContext(), 
                   "Go Available Clicked", Toast.LENGTH_LONG).show();

    is_available = true;
    new GoAvailableTask().execute();
  }

  public void getUsbDevices(View view) {
    Toast.makeText(getApplicationContext(), 
                   "Get USB Devices Clicked", Toast.LENGTH_LONG).show();

    // Get ListView object from xml
    //listView = (ListView) findViewById(R.id.listview);

    

    /*
    String[] values = new String[] { "USB 1", 
                                       "USB 2"
                                     };
    */

    ArrayList<String> values = getUsbDevicesArray();

    arrayAdapter = new ArrayAdapter(this, 
                                   android.R.layout.simple_list_item_1, values);
    /*
    listView.setAdapter(arrayAdapter);

    listView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view,
	int position, long id) {
	Toast.makeText(getApplicationContext(),
	  "Click ListItem Number " + position, Toast.LENGTH_LONG).show();
      }
    });
    */
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

  public void loginToDrupal(View view) {
    Toast.makeText(getApplicationContext(), 
                   "Login", Toast.LENGTH_LONG).show();
    //if(is_logged_in == false) {
      Log.d("_dbg", "is_logged_in false");
      new LoginToDrupalTask().execute(
            "http://www.myrobot.com/drupal/rest/user/login.json"
            );
    /* }  else {
      Log.d("_dbg", "is_logged_in true");
      new LogoffFromDrupalTask().execute(
            "http://www.myrobot.com/drupal/rest/user/logout.json"
            );
    } */

  }

  public void logoffFromDrupal(View view) {
    Toast.makeText(getApplicationContext(), 
                   "Logoff", Toast.LENGTH_LONG).show();

      new LogoffFromDrupalTask().execute(
            "http://www.myrobot.com/drupal/rest/user/logout.json"
            );

  }

  public void getUnprocessedCmdsBtn(View view) {
    Toast.makeText(getApplicationContext(), 
                   "Get Unprocessed CMDs Clicked", Toast.LENGTH_LONG).show();

    new GetUnprocessedCmdsTask().execute(
            "http://myrobot.com/drupal/rest/view/newcbcmds.json"
            );

  }

  public void sendArduinoCmd(String cmd) {
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
          byte[] cmd_array = cmd.getBytes();
          //buffer[0] = 'r';
          buffer[0] = cmd_array[0];
          Log.d("_dbg", "sending arduino command: " + buffer[0]);
          buffer[1] = '\n';
	  int numBytesWrite = driver.write(buffer, 1000);
	  Toast.makeText(getApplicationContext(), 
		    "Read " + numBytesWrite + " bytes.", Toast.LENGTH_LONG).show();
	} catch (IOException e) {
	  // Deal with error.
	  Toast.makeText(getApplicationContext(), 
		    "IOException occurred", Toast.LENGTH_LONG).show();
	} finally {
	  driver.close();
	} 
      } else {
        Log.d("_dbg", "arduino driver null");

      }
    } catch (IOException e) {
      // Deal with error.
      Toast.makeText(getApplicationContext(), 
                "IOException occurred 2", Toast.LENGTH_LONG).show();
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

  /*
  $.ajax({
        url: "http://www.myrobot.com/drupal/rest/node/" + cb_cmd_nid + '.json',
        type: 'PUT',
        data: '{"field_state":{"und":[{"value":"processed"}]}}',
        dataType: 'json',
        contentType: 'application/json',
        error: function(XMLHttpRequest, textStatus, errorThrown) {
        console.log('failed to updated cellbot command to processed');
        console.log(JSON.stringify(XMLHttpRequest));
        console.log(JSON.stringify(textStatus));
        console.log(JSON.stringify(errorThrown));
        },
        success: function (data) {
        console.log("success updating node to processed");
        }
        });
  */ 

  public String goAvailableJSONFeed() {
     while(is_available == true) {
       new GetUnprocessedCmdsTask().execute("http://myrobot.com/drupal/rest/view/newcbcmds.json");
       try {
         Log.d("_dbg", "sleeping 10 second...");
         TimeUnit.SECONDS.sleep(10);
        } catch (Exception e) {
            Log.d("sleep exception", e.getLocalizedMessage());
        }        

     }
     return "";
  }
  public String readCellbotInfoJSONFeed(String URL) {
        URL = "http://www.myrobot.com/drupal/rest/node/" + cellbot_nid + ".json";
        StringBuilder stringBuilder = new StringBuilder();
        httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }        
        return stringBuilder.toString();
    }

  public String changeCmdToProcessedJSONFeed(String cb_cmd_nid) {
        String URL = "http://www.myrobot.com/drupal/rest/node/" + cb_cmd_nid + ".json";
        StringBuilder stringBuilder = new StringBuilder();
        httpClient = new DefaultHttpClient();
        //session_name+"="+sessid
      
        /* 
        CookieStore cookieStore = new BasicCookieStore();
        Cookie cookie = new BasicClientCookie(sess_name, sess_id);
        cookieStore.addCookie(cookie); 

        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        */
         

        HttpPut httpPut = new HttpPut(URL);
        httpPut.addHeader("Content-Type", "application/json");
        httpPut.addHeader("Accept", "application/json");

        /*
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("key1", "value1"));
        pairs.add(new BasicNameValuePair("key2", "value2"));
        put.setEntity(new UrlEncodedFormEntity(pairs));
        */

        try {
            httpPut.setEntity(new StringEntity("{\"field_state\":{\"und\":[{\"value\":\"processed\"}]}}"));
            //HttpResponse response = httpClient.execute(httpPut, localContext);
            HttpResponse response = httpClient.execute(httpPut);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.d("JSON", "_dbg statusCode: " + Integer.toString(statusCode));
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }        
        return stringBuilder.toString();
    }

  public String logoffFromDrupalJSONFeed(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(URL);
        try {

            HttpResponse response = httpClient.execute(httpPost);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.d("JSON", "_dbg post statusCode: " + Integer.toString(statusCode));
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }        
        Log.d("_dbg", "login.json raw string: " + stringBuilder.toString());
        return stringBuilder.toString();
    }

  /*
 *
 *
 *
 * */

  public String loginToDrupalJSONFeed(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(URL);
        try {

            // Create a local instance of cookie store
            //CookieStore cookieStore = new BasicCookieStore();
            
            // Create local HTTP context
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            EditText mLoginEdit   = (EditText)findViewById(R.id.login_text);
            EditText mPasswordEdit   = (EditText)findViewById(R.id.password_text);

            nameValuePairs.add(new BasicNameValuePair("username", mLoginEdit.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("password", mPasswordEdit.getText().toString()));

            

/* 
            nameValuePairs.add(new BasicNameValuePair("username", "MyRobotadmin"));
            nameValuePairs.add(new BasicNameValuePair("password", "myrobot"));
*/
            

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(httpPost);

            /*
            httpPost = new HttpPost("http://www.myrobot.com/drupal/rest/user/logout.json");
            response = httpClient.execute(httpPost);
            */

/*
            HttpPut httpPut = new HttpPut("http://www.myrobot.com/drupal/rest/node/364.json");
            httpPut.addHeader("Content-Type", "application/json");
            httpPut.addHeader("Accept", "application/json");
            httpPut.addHeader("Accept-Encoding", "identity");
            httpPut.setEntity(new StringEntity("{\"field_state\":{\"und\":[{\"value\":\"processed\"}]}}"));
            response = httpClient.execute(httpPut);
*/

            /*
            List<Cookie> cookies = httpClient.getCookieStore.getCookies();
            for (int i = 0; i < cookies.size(); i++) {
              Log.e("_dbg cookie", "Local cookie: " + cookies.get(i));
            }
            */
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.d("JSON", "_dbg post statusCode: " + Integer.toString(statusCode));
            //Log.d("JSON", "_dbg put statusCode: " + Integer.toString(statusCode));
            if (statusCode == 200) {
                /*
                Button login_btn = (Button)findViewById(R.id.login_btn);
                login_btn.setText("Logoff");
                is_logged_in = true;
                */
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                //is_logged_in = false;
                Log.d("JSON", "Failed to download file");
                statusTextView.setText("status: login failed");
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }        
        return stringBuilder.toString();
    }

  public String readNewCmdsJSONFeed(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }        
        return stringBuilder.toString();
    }

//		    new ChangeNodeToProcessedTask().execute(cmd_nid);
  private class GetUnprocessedCmdsTask extends AsyncTask
    <String, Void, String> {
        protected String doInBackground(String... urls) {
            return readNewCmdsJSONFeed(urls[0]);
        }

/*
 * [{"vid":"364","uid":"0","title":"node 16  backward cellbot command","log":"","status":"1","comment":"2","promote":"1","sticky":"0","nid":"364","type":"cellbot_command","language":"und","created":"1383516089","changed":"1384509963","tnid":"0","translate":"0","revision_timestamp":"1384509963","revision_uid":"1","body":{"und":[{"value":"b","summary":"","format":"filtered_html","safe_value":"<p>b</p>\n","safe_summary":""}]},"field_cb_node_id":{"und":[{"value":"16"}]},"field_state":{"und":[{"value":"unprocessed","format":null,"safe_value":"unprocessed"}]},"rdf_mapping":{"rdftype":["sioc:Item","foaf:Document"],"title":{"predicates":["dc:title"]},"created":{"predicates":["dc:date","dc:created"],"datatype":"xsd:dateTime","callback":"date_iso8601"},"changed":{"predicates":["dc:modified"],"datatype":"xsd:dateTime","callback":"date_iso8601"},"body":{"predicates":["content:encoded"]},"uid":{"predicates":["sioc:has_creator"],"type":"rel"},"name":{"predicates":["foaf:name"]},"comment_count":{"predicates":["sioc:num_replies"],"datatype":"xsd:integer"},"last_activity":{"predicates":["sioc:last_activity_date"],"datatype":"xsd:dateTime","callback":"date_iso8601"}},"cid":"0","last_comment_timestamp":"1383516089","last_comment_name":null,"last_comment_uid":"0","comment_count":"0","name":"","picture":"0","data":null,"user_relationship_node_access":false},{"vid":"89","uid":"1","title":"node 16  left cellbot command","log":"","status":"1","comment":"2","promote":"1","sticky":"0","nid":"89","type":"cellbot_command","language":"und","created":"1363348360","changed":"1384760162","tnid":"0","translate":"0","revision_timestamp":"1384760162","revision_uid":"1","body":{"und":[{"value":"l","summary":"","format":"filtered_html","safe_value":"<p>l</p>\n","safe_summary":""}]},"field_cb_node_id":{"und":[{"value":"16"}]},"field_state":{"und":[{"value":"unprocessed","format":null,"safe_value":"unprocessed"}]},"rdf_mapping":{"rdftype":["sioc:Item","foaf:Document"],"title":{"predicates":["dc:title"]},"created":{"predicates":["dc:date","dc:created"],"datatype":"xsd:dateTime","callback":"date_iso8601"},"changed":{"predicates":["dc:modified"],"datatype":"xsd:dateTime","callback":"date_iso8601"},"body":{"predicates":["content:encoded"]},"uid":{"predicates":["sioc:has_creator"],"type":"rel"},"name":{"predicates":["foaf:name"]},"comment_count":{"predicates":["sioc:num_replies"],"datatype":"xsd:integer"},"last_activity":{"predicates":["sioc:last_activity_date"],"datatype":"xsd:dateTime","callback":"date_iso8601"}},"cid":"0","last_comment_timestamp":"1363348360","last_comment_name":null,"last_comment_uid":"1","comment_count":"0","name":"MyRobotadmin","picture":"7","data":"a:5:{s:34:\"user_relationships_ui_auto_approve\";a:1:{i:1;i:0;}s:7:\"overlay\";i:1;s:34:\"user_relationship_mailer_send_mail\";b:1;s:38:\"user_relationship_node_access_defaults\";a:3:{s:4:\"view\";a:1:{i:1;i:0;}s:6:\"update\";a:1:{i:1;i:0;}s:6:\"delete\";a:1:{i:1;i:0;}}s:40:\"user_relationships_allow_private_message\";s:12:\"on all users\";}","user_relationship_node_access":false}]
 * */
 
        protected void onPostExecute(String result) {
            try {

                Log.e("_dbg result: ", result);
                JSONArray jsonArray=new JSONArray(result);
                Log.e("_dbg jsonArray length: ", Integer.toString(jsonArray.length()));
                boolean cmd_processed = false;
                for(int i = 0; i < jsonArray.length(); i++) {
                  JSONObject d1 = jsonArray.getJSONObject(i);
                  String cmd_nid = d1.getString("nid");
                  //"field_cb_node_id":{"und":[{"value":"16"}]}
		  JSONObject d10 = d1.getJSONObject("field_cb_node_id");
		  JSONArray d11 = d10.getJSONArray("und");
		  JSONObject d12 = d11.getJSONObject(0);
		  String cmd_cellbot_nid = d12.getString("value");
                   
                  
                  Log.e("_dbg ", "_dbg cmd_cellbot_nid: " + cmd_cellbot_nid);
                  Log.e("_dbg ", "_dbg cmd nid: " + d1.getString("nid"));
                  Log.e("_dbg ", "_dbg cellbot_nid: " + cellbot_nid);
                  
                  if(cellbot_nid.equals(cmd_cellbot_nid)) {
                    cmd_processed = true;
		    JSONObject d2 = d1.getJSONObject("body");
		    JSONArray d3 = d2.getJSONArray("und");
		    JSONObject d4 = d3.getJSONObject(0);
		    String cmd = d4.getString("value");
		    sendArduinoCmd(cmd);
                    Log.e("_dbg ", "_dbg processing: cmd nid: " + d1.getString("nid"));
		    Log.e("_dbg d4.getString('value'): ", d4.getString("value"));
		    statusTextView.setText("status: cmd " + cmd + " retrieved");
		    new ChangeNodeToProcessedTask().execute(cmd_nid);
                  }
                }

                  if(cmd_processed != true) {
		    statusTextView.setText("status: no cmds found in last poll");
                  }

                /*
                for (int i = 0; i < jsonArray.length(); i++) {
                  String valueString=jsonArray.getString(i);
                  Log.e("_dbg json", i+"="+valueString);
                }
                JSONObject jsonObject = new JSONObject(result);
                Toast.makeText(getApplicationContext(), 
                  "jsonObject length: " + jsonObject.length, Toast.LENGTH_LONG).show();
                JSONObject weatherObservationItems = 
                    new JSONObject(jsonObject.getString("weatherObservation"));

                Toast.makeText(getBaseContext(), 
                    weatherObservationItems.getString("clouds") + 
                 " - " + weatherObservationItems.getString("stationName"), 
                 Toast.LENGTH_SHORT).show();
                */
            } catch (Exception e) {
                Log.d("_dbg GetUnprocessedCmdsTask", e.getLocalizedMessage());
            }          
        }
    }

  private class GetCellbotInfoTask extends AsyncTask
    <String, Void, String> {
        protected String doInBackground(String... urls) {
            return readCellbotInfoJSONFeed(urls[0]);
        }
 
        protected void onPostExecute(String result) {
            try {

                Log.e("_dbg result: ", result);
                JSONObject jsonObject = new JSONObject(result);

                JSONObject field_opentok_api_key = jsonObject.getJSONObject("field_opentok_api_key");
		JSONArray und = field_opentok_api_key.getJSONArray("und");
		JSONObject api_key_obj = und.getJSONObject(0);
		K_API_KEY = api_key_obj.getString("value");
                Log.d("_dbg", "K_API_KEY: " + K_API_KEY);

                JSONObject field_opentok_session_id = jsonObject.getJSONObject("field_opentok_session_id");
		und = field_opentok_session_id.getJSONArray("und");
		JSONObject session_id_obj = und.getJSONObject(0);
		K_SESSION_ID = session_id_obj.getString("value");

                JSONObject field_opentok_token_id = jsonObject.getJSONObject("field_opentok_token_id");
		und = field_opentok_token_id.getJSONArray("und");
		JSONObject token_id_obj = und.getJSONObject(0);
		K_TOKEN = token_id_obj.getString("value");

                sessionConnect();

            } catch (Exception e) {
                Log.d("_dbg GetCellbotInfoTask", e.getLocalizedMessage());
            }          
        }
    }

  private class GoAvailableTask extends AsyncTask
    <String, Void, String> {
        protected String doInBackground(String... urls) {
            return goAvailableJSONFeed();
        }
 
        protected void onPostExecute() {
        }
    }

  private class ChangeNodeToProcessedTask extends AsyncTask
    <String, Void, String> {
        protected String doInBackground(String... node_ids) {
            return changeCmdToProcessedJSONFeed(node_ids[0]);
        }
 
        protected void onPostExecute(String result) {
            try {

                Log.e("_dbg changeCmdToProcessedJSONFeed", " completed with result: " + result);
            } catch (Exception e) {
                Log.d("_dbg ReadWeatherJSONFeedTask", e.getLocalizedMessage());
            }          
        }
    }

  private class LogoffFromDrupalTask extends AsyncTask
    <String, Void, String> {
        protected String doInBackground(String... urls) {
            return logoffFromDrupalJSONFeed(urls[0]);
        }
 
        protected void onPostExecute(String result) {
            try {

                Log.e("_dbg loginToDrupalJSONFeed", " completed with result: " + result);
                JSONObject jsonObject = new JSONObject(result);
                sess_id = jsonObject.getString("sessid");
                sess_name = jsonObject.getString("session_name");
                Log.e("_dbg jsonObject.getString('sessid'): ", jsonObject.getString("sessid"));
                Log.e("_dbg jsonObject.getString('session_name'): ", jsonObject.getString("session_name"));
            } catch (Exception e) {
                Log.d("_dbg LoginToDrupalTask", e.getLocalizedMessage());
            }          
        }
    }

  private class LoginToDrupalTask extends AsyncTask
    <String, Void, String> {
        protected String doInBackground(String... urls) {
            return loginToDrupalJSONFeed(urls[0]);
        }

/*
 *
 * {"sessid":"FvZXkCshNcJav6C56qDcD4Tq_JikwuSCE4ls3VlKVhI","session_name":"SESS5722f41ab7d358b7a70efa174531cb38","user":{"uid":"1","name":"MyRobotadmin","mail":"wgheath@gmail.com","theme":"","signature":"","signature_format":"filtered_html","created":"1355343695","access":"1384509047","login":1384509081,"status":"1","timezone":"Pacific/Honolulu","language":"","picture":{"fid":"7","uid":"1","filename":"picture-1-1363515922.jpg","uri":"public://pictures/picture-1-1363515922.jpg","filemime":"image/jpeg","filesize":"94349","status":"1","timestamp":"1363515922","type":"image","rdf_mapping":[]},"init":"wgheath@gmail.com","data":{"user_relationships_ui_auto_approve":{"1":0},"overlay":1,"user_relationship_mailer_send_mail":true,"user_relationship_node_access_defaults":{"view":{"1":0},"update":{"1":0},"delete":{"1":0}},"user_relationships_allow_private_message":"on all users"},"roles":{"2":"authenticated user","3":"administrator","6":"robot"},"field_cellbot":{"und":[{"nid":"16"}]},"field_intelligence":[],"rdf_mapping":{"rdftype":["sioc:UserAccount"],"name":{"predicates":["foaf:name"]},"homepage":{"predicates":["foaf:page"],"type":"rel"}},"privatemsg_disabled":false}}
 *
 *
 * */
 
        protected void onPostExecute(String result) {
            try {

                Log.e("_dbg loginToDrupalJSONFeed", " completed with result: " + result);
                JSONObject jsonObject = new JSONObject(result);
                sess_id = jsonObject.getString("sessid");
                sess_name = jsonObject.getString("session_name");
                Log.e("_dbg jsonObject.getString('sessid'): ", jsonObject.getString("sessid"));
                Log.e("_dbg jsonObject.getString('session_name'): ", jsonObject.getString("session_name"));
                JSONObject user = jsonObject.getJSONObject("user");
                JSONObject cellbot = user.getJSONObject("field_cellbot");
                JSONArray ar1 = cellbot.getJSONArray("und");
                JSONObject nid_obj = ar1.getJSONObject(0);
                cellbot_nid = nid_obj.getString("nid");
                Log.e("_dbg",  "_dbg nid_obj.getString(\"nid\"): "+ cellbot_nid);
                statusTextView.setText("status: nid " + cellbot_nid + " logged in");
                new GetCellbotInfoTask().execute("");
            } catch (Exception e) {
                statusTextView.setText("status: Exception logging in");
                Log.d("_dbg LoginToDrupalTask", e.getLocalizedMessage());
            }          
        }
    }
    
}
