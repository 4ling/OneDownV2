/*
 * Segments of this application use public available library Sensorlib
 * please note the following license:
 *
 * original source https://github.com/gradlman/SensorLib
 * This file has been modified by Andreas Vierling
 *
 * The MIT License (MIT)
 *
 *Copyright (c) 2016 Stefan
 *
 *Permission is hereby granted, free of charge, to any person obtaining a copy
 *of this software and associated documentation files (the "Software"), to deal
 *in the Software without restriction, including without limitation the rights
 *to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *copies of the Software, and to permit persons to whom the Software is
 *furnished to do so, subject to the following conditions:
 *
 *The above copyright notice and this permission notice shall be included in all
 *copies or substantial portions of the Software.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *SOFTWARE.
 *
 *
 *
 * Segments of this application use the scheme of a open source custom reciever sample app for chromecast
 * please note the following license:
 *
 * original source https://github.com/googlecast/CastHelloText-android
 * This file has been modified by Andreas Vierling
 *
 * Copyright (C) 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package de.vierling.andreas.onedownv2;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.cast.Cast.MessageReceivedCallback;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.fau.sensorlib.DsBleSensor;
import de.fau.sensorlib.DsSensor;
import de.fau.sensorlib.DsSensorManager;
import de.fau.sensorlib.SensorDataProcessor;
import de.fau.sensorlib.SensorFoundCallback;
import de.fau.sensorlib.SensorInfo;
import de.fau.sensorlib.dataframe.HeartRateDataFrame;
import de.fau.sensorlib.dataframe.SensorDataFrame;

public class Launcher extends AppCompatActivity {


    private static final String TAG = Launcher.class.getSimpleName();

    //Soundpool and sound id variables
    SoundPool myKicksound;
    int kicksoundID;
    int gunnarsoundID1;
    int lisasoundID1;
    int gunnarsoundID2;
    int lisasoundID2;
    int gunnarsoundID3;
    int lisasoundID3;
    int finalID1;
    int finalID2;
    int finalID3;
    int finalID4;
    int finalID5;
    int finalID6;
    int finalID7;
    int finalID8;


    // buffervariable used in ontick to detect full heartrate events
    int spaceholder1;


    // used for logging heartrate data, initialized at countdownstart terminated at couddown stop
    FileOutputStream stream;


    public static ValuesUtil Values = new ValuesUtil(80,2147000000,367499,0);

    // variables needed for streaming activity intitialized in oncreate
    private CastContext NewContext;
    private CastSession NewCastSession;
    private CastChannel NewCastChannel;
    // transfered sting with setter method
    private String Transfertext;
    public void setTransfertext(String text){
        Transfertext = text;
    }



    // sessionmanager handles the behaviour of the session between cast device and phone
    private SessionManagerListener<CastSession> NewCastSessionManagerListener = new SessionManagerListener<CastSession>() {
        @Override
        public void onSessionStarting(CastSession castSession) {

        }

        @Override
        public void onSessionStarted(CastSession castSession, String s) {
            Log.d(TAG, "castdebug Session started");

            NewCastSession = castSession;
            invalidateOptionsMenu();
            startStream();
        }

        @Override
        public void onSessionStartFailed(CastSession castSession, int i) {
            Log.d(TAG, "castdebug Session start failed");
        }



        @Override
        public void onSessionEnding(CastSession castSession) {
            Log.d(TAG, "castdebug Session ended");
        }

        @Override
        public void onSessionEnded(CastSession castSession, int i) {
            Log.d(TAG, "castdebug Session ended");
            if (NewCastSession == castSession) {
                endStream();
            }
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResuming(CastSession castSession, String s) {

        }

        @Override
        public void onSessionResumed(CastSession castSession, boolean b) {
            Log.d(TAG, "castdebug Session resumed");
            NewCastSession = castSession;
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumeFailed(CastSession castSession, int i) {

        }

        @Override
        public void onSessionSuspended(CastSession castSession, int i) {

        }
    };

    // variable for BLE sensor
    DsBleSensor mSensor;
    private Context getThis() {
        return this;
    }
    // Sensordatapracessor with costumized on new data method, sets valuse in Value dataframe
    private SensorDataProcessor mDataHandler = new SensorDataProcessor() {
        @Override
        public void onNewData(SensorDataFrame sensorDataFrame) {
            Log.d(TAG, "new data: " + sensorDataFrame);
            if (sensorDataFrame instanceof HeartRateDataFrame) {
                HeartRateDataFrame hr = (HeartRateDataFrame)sensorDataFrame;
                Log.d(TAG, "Heartratedebug onnewdata: " + hr.getHeartRate());

                Values.setRate((int) hr.getHeartRate());

                Heartrate.setText("" + Values.getRate());
            }
        }
    };



    // custom numberformat used to modify output String
    private numberutil inttostirng = new numberutil();

    //Layout variables
    private TextView countdownnr;
    private TextView Heartrate;
    private Button start;
    private Button settings;
    private Button stop;
    private Button rateplus;
    private Button rateminus;
    private Button connect;
    private Button send;
    private RelativeLayout mRelativelayout;

    //Coundowntimer provides on tick method and coordinates the countdown
    private CountDownTimer countDownTimer;

    // for toast messages
    Toast Toaster;

    // used for status request
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



    // Onclick listener processes userinput and calls required methods according to pushed buttons
    public final ThreadLocal<View.OnClickListener> trigger = new ThreadLocal<View.OnClickListener>() {
        @Override
        protected View.OnClickListener initialValue() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View click) {
                    switch (click.getId()) {

                        //starts countdown and log
                        case R.id.start:

                            start();

                            break;
                        // terminates countdown and log
                        case R.id.stop:

                            stop();

                            break;

                        // changes to settings activity
                        case R.id.settings:
                            Intent intent = new Intent(Launcher.this, SettingsActivity.class);
                            startActivity(intent);
                            break;
                        // manally raises heartrate
                        case R.id.rateplus:
                            //On touch listener for rate minus and rate plus manually changes the heartrate +/- 1 for every 0.2 s of a button down event
                            rateplus.setOnTouchListener(new View.OnTouchListener(){
                                private Handler plusHandler;
                                @Override public boolean onTouch(View plusv, MotionEvent plusevent){
                                    switch (plusevent.getAction()){
                                        case MotionEvent.ACTION_DOWN:
                                            if(plusHandler != null){
                                                return true;
                                            }
                                            plusHandler = new Handler();
                                            plusHandler.postDelayed(mAction, 200);
                                            break;
                                        case MotionEvent.ACTION_UP:
                                            if (plusHandler == null) return true;
                                            plusHandler.removeCallbacks(mAction);
                                            plusHandler = null;
                                            break;
                                    }
                                    return false;
                                }

                                Runnable mAction = new Runnable() {
                                    @Override public void run() {
                                        plusrate();
                                        plusHandler.postDelayed(this, 200);
                                    }
                                };

                            });
                            break;


                        // manually lowers heartrate
                        case R.id.rateminus:
                            //see rate plus
                            rateminus.setOnTouchListener(new View.OnTouchListener(){
                                private Handler minusHandler;
                                @Override public boolean onTouch(View minusv, MotionEvent minusevent){
                                    switch (minusevent.getAction()){
                                        case MotionEvent.ACTION_DOWN:
                                            if(minusHandler != null){
                                                return true;
                                            }
                                            minusHandler = new Handler();
                                            minusHandler.postDelayed(mAction, 200);
                                            break;
                                        case MotionEvent.ACTION_UP:
                                            if (minusHandler == null) return true;
                                            minusHandler.removeCallbacks(mAction);
                                            minusHandler = null;
                                            break;
                                    }
                                    return false;
                                }

                                Runnable mAction = new Runnable() {
                                    @Override public void run() {
                                        minusrate();
                                        minusHandler.postDelayed(this, 200);
                                    }
                                };

                            });
                            break;
                        // sets screen to custom start text
                        case R.id.send:


                            Log.d(TAG,"valuesdebug setnew countdownstate" + (int)Values.getStartvalue() +"  ln" + Values.getLargenumber());


                            myKicksound.play(kicksoundID,1,1,1,0,1);
                            setTransfertext(Values.getStarttext());
                            sendCountdownstate(Transfertext);
                            Log.d(TAG, "Launcher debug send " + Values.getStarttext());


                            break;
                        case R.id.connect:
                            //starts connection event if bluetooth is activated otherwise an errror mesaage will occur
                            if(bluetoothAdapter.isEnabled() != true){
                                Toaster.makeText(getApplicationContext(), "Bitte Bluetooth aktivieren!", Toast.LENGTH_LONG).show();
                                break;
                            }
                            //connection event pairs mobile and sensor if both are activated simultaniously
                            try {
                                //mSensor = new DsBleSensor(this, new SensorInfo("X Cell", "DE:2E:82:E1:65:CD"), mDataHandler);
                                Toaster.makeText(getApplicationContext(), "Verbinde mit XCell", Toast.LENGTH_LONG).show();
                                DsSensorManager.searchBleDevices(new SensorFoundCallback() {
                                    public boolean onKnownSensorFound(SensorInfo sensor) {
                                        // This is called whenever a new BLE sensor was found that can be accessed via the SensorLib.
                                        Log.d(TAG, "BLE Sensor found: " + sensor.getName() + " @ " + sensor.getDeviceAddress());

                                        // Check if it is a TEK sensor
                                        if (sensor.getName().contains("X_CELL")) {
                                            // It is a TEK: create the sensor...
                                            mSensor = new DsBleSensor(getThis(), sensor, mDataHandler){
                                                @Override
                                                public void disconnect(){
                                                    Toaster.makeText(getApplicationContext(), "Verbindung zu XCell unterbrochen!", Toast.LENGTH_LONG).show();
                                                    Values.setBluetoothok(false);
                                                    super.disconnect();
                                                }
                                            };

                                            // ...select the desired hardware sensors...
                                            mSensor.useHardwareSensor(DsSensor.HardwareSensor.HEART_RATE);
                                            //mSensor.addDataHandler(mCsvDataLogger);
                                            try {
                                                // ...connect to it...
                                                mSensor.connect();
                                                Log.d(TAG, "Sleeping for 8");
                                                Thread.sleep(8000);
                                                // ...and start streaming data.
                                                // New data will now appear in the callback above.  DE:2E:82:E1:65:CD
                                                mSensor.startStreaming();
                                                // when sucessfully connected, the user recieves feedback and can call the battery lvl in the settings activity
                                                Values.setBluetoothok(true);
                                                Toaster.makeText(getApplicationContext(), "Verbunden", Toast.LENGTH_LONG).show();
                                                Values.setBatterylvl(mSensor.getBatteryLevel());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            return false;
                                        }
                                        return true;
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                    }
                }
            };
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //load all soundfiles
        myKicksound = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        kicksoundID = myKicksound.load(this,R.raw.heartbeatlisa1final,1);
        gunnarsoundID1 = myKicksound.load(this,R.raw.gunnarheartbeat1final,1);
        lisasoundID1 = myKicksound.load(this,R.raw.heartbeatlisa1final,1);
        gunnarsoundID2 = myKicksound.load(this,R.raw.heartbeatgunnar1,1);
        lisasoundID2 = myKicksound.load(this,R.raw.heartbeatlisa1,1);
        gunnarsoundID3 = myKicksound.load(this,R.raw.heartbeatgunnar2,1);
        lisasoundID3 = myKicksound.load(this,R.raw.heartbeatlisa2,1);
        //initialize all sound ids
        finalID1 = myKicksound.load(this,R.raw.heartbeatlisa2,1);
        finalID2 = myKicksound.load(this,R.raw.heartbeatlisa2,1);
        finalID3 = myKicksound.load(this,R.raw.heartbeatlisa2,1);
        finalID4 = myKicksound.load(this,R.raw.heartbeatlisa2,1);
        finalID5 = myKicksound.load(this,R.raw.heartbeatlisa2,1);
        finalID6 = myKicksound.load(this,R.raw.heartbeatlisa2,1);
        finalID7 = myKicksound.load(this,R.raw.heartbeatlisa2,1);
        finalID8 = myKicksound.load(this,R.raw.heartbeatlisa2,1);


        // For Android 6+ we have to make sure that we have the BLE permissions and the permission to write in the device storage
        try {
            DsSensorManager.checkBtLePermissions(this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    112);
        }

        //Initialize textviews
        Heartrate = (TextView) findViewById(R.id.Heartrate);
        Heartrate.setTextSize(25);
        Heartrate.setText("" + Values.getRate());
        countdownnr = (TextView) findViewById(R.id.countdownnr);
        countdownnr.setTextSize(25);
        countdownnr.setText("" + ((long) Values.getStartvalue() + Values.getLargenumber()));
        Values.setStarttext("<DIV id=\"message\">used heartbeats <HR NOSHADE SIZE=1> <td style=\"vertical-align:top\"> remaining heartbeats</td></DIV>");
        // safe initialized Values
        setshareableInfo();



        // initialize buttons and buttonlisteners
        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(trigger.get());
        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(trigger.get());
        rateplus = (Button) findViewById(R.id.rateplus);
        rateplus.setOnClickListener(trigger.get());
        rateminus = (Button) findViewById(R.id.rateminus);
        rateminus.setOnClickListener(trigger.get());
        settings = (Button) findViewById(R.id.settings);
        settings.setOnClickListener(trigger.get());
        connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener(trigger.get());
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(trigger.get());
        mRelativelayout = (RelativeLayout)findViewById(R.id.content_launcher);

        //initialize cast variables
        NewContext = CastContext.getSharedInstance(this);
        NewContext.registerLifecycleCallbacksBeforeIceCreamSandwich(this, savedInstanceState);




    }

    // adds castbutton to optopns menue
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher, menu);
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.Castbutton);
        return true;
    }




    @Override
    protected void onResume(){
        // reads new Value frame from shared preferences. the settings activity forwards its data this way
        getshareableInfo();
        Log.d(TAG,"settingsdebug onresumecalled" + (int)Values.getStartvalue());
        super.onResume();
        // refreshes textview
        countdownnr.setText("" + ((long) Values.getStartvalue() + Values.getLargenumber()));
        // resumes cast session or restarts it
        NewContext.getSessionManager().addSessionManagerListener(NewCastSessionManagerListener,CastSession.class);
        if(NewCastSession == null){
            NewCastSession = NewContext.getSessionManager().getCurrentCastSession();
        }
    }

    @Override
    protected void onPause(){
        // safes Value data in shared preferences
        setshareableInfo();

        super.onPause();
        // pauses cast session
        NewContext.getSessionManager().removeSessionManagerListener(NewCastSessionManagerListener,CastSession.class);
        // ends loging activity if app is finishing
        if (this.isFinishing() && stream != null) {
            try {
                stream.flush();
                stream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy(){
        // disconnects sensor
        if(mSensor != null) {
            mSensor.stopStreaming();
            mSensor.disconnect();
        }
        // stops timer and log
        stop();
        // closes cast session
        closeSession();
        super.onDestroy();

    }



    //saves Values in shared preferences in order to transfer them to settings activity
    public void setshareableInfo(){
        SharedPreferences sharedValues = getSharedPreferences("Values", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedValues.edit();
        editor.putInt("heartrate", Values.getRate());
        editor.putLong("largenumber", Values.getLargenumber());
        editor.putInt("startvalue", (int)Values.getStartvalue());
        editor.putInt("upcount", (int) Values.getUpcount());
        editor.putString("starttext", Values.getStarttext());
        editor.putInt("selectedsound", Values.getSoundselected());
        editor.putInt("batterylvl", Values.getBatterylvl());
        editor.apply();
        Log.d(TAG,"shared preferences saved in Launcher");
    }
    //reads new Value data from shared preferences
    public void getshareableInfo(){
        SharedPreferences sharedValues = getSharedPreferences("Values", Context.MODE_PRIVATE);
        Values.setRate(sharedValues.getInt("heartrate", Values.getRate()));
        Values.setLargenumber(sharedValues.getLong("largenumber",Values.getLargenumber()));
        Values.setStartvalue((double) sharedValues.getInt("startvalue",(int)Values.getStartvalue()));
        Values.setUpcount((double) sharedValues.getInt("upcount",(int)Values.getUpcount()));
        Values.setSoundselected(sharedValues.getInt("selectedsound",Values.getSoundselected()));
        Values.setStarttext(sharedValues.getString("starttext", Values.getStarttext()));
        Log.d(TAG,"shared preferences called in Launcher");
    }


    private void closeSession(){
        if(NewCastSession != null){
        Log.d(TAG,"Castdebug closesession called");
        endStream();
        NewCastSession = null;
        }
    }
    // initializes message channel with custom cast namespace returns error log if not successfull, user recivies feedback from cast ok boolean(see on tick)
    private void startStream(){
         Log.i(TAG, "startstream wurde aufgerufen");

        if (NewCastSession != null && NewCastChannel == null) {
            NewCastChannel = new CastChannel(getString(R.string.castname));
            try {
                NewCastSession.setMessageReceivedCallbacks(NewCastChannel.getCastname(),
                        NewCastChannel);
                Log.d(TAG, "Message channel started");
            } catch (IOException e) {
                Log.d(TAG, "Error starting message channel", e);
                NewCastChannel = null;
            }
        }

        Values.setCastok(true);

    }



    // ends cast session and cast channel
    private void endStream(){
        Log.d(TAG,"Castdebug endstream called");
        if (NewCastSession!= null && NewCastChannel != null) {
            try {
                NewCastSession.removeMessageReceivedCallbacks(NewCastChannel.getCastname());
                Log.d(TAG, "Message channel closed");
            } catch (IOException e) {
                Log.d(TAG, "Error closing message channel", e);
            } finally {
                NewCastChannel = null;
            }
        }

    }
    // Sends string to cast reciever application
    private void sendCountdownstate(String Countdownstate){
        if (NewCastChannel != null) {
            NewCastSession.sendMessage(NewCastChannel.getCastname(),Countdownstate);
        }
        else {
        //userfeedback if transfer failed; bgcolor changes in ontick if boolean == false
            Values.setCastok(false);
        }

    }


    // cast channel custom class used for callback and logging meassage recieved event
    static class CastChannel implements MessageReceivedCallback {

        private final String Castname;


        CastChannel(String newstate) {
            Castname= newstate;
        }


        public String getCastname() {
            return Castname;
        }



        @Override
        public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
            Log.d(TAG, "onMessageReceived: " + message);
        }

    }

//handles timerelated actions and refreshes all data in ontick uses 10 ticks per second
 public class mycountDownTimer extends CountDownTimer {




        public mycountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        // byteme() method builds a string for a log event and converts it to a byte value
        public byte[] byteme(){

            StringBuilder Test = new StringBuilder();
            Test.append(System.currentTimeMillis());
            Test.append(";");
            Test.append(Values.getRate());
            Test.append(";");
            Test.append(Values.getBluetoothok());
            Test.append("\n");

            return Test.toString().getBytes();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // reduces startvalue by beats per 50ms calculated from the current rate ~20fps
            Values.setStartvalue((double) ((Values.getStartvalue() - ((double) Values.getRate() / 1200))));
            // refreshes display information
            Heartrate.setText("" + Values.getRate());
            countdownnr.setText("" + (long) (Values.getStartvalue() + Values.getLargenumber()));
            Values.setUpcount(Values.getUpcount()+((double) Values.getRate() / 1200));
            //sets and sends new string to chromecast
            setTransfertext(inttostirng.pointify((long)Values.getUpcount()) + " <HR NOSHADE SIZE=1>" +  inttostirng.pointify((long) (Values.getStartvalue() + Values.getLargenumber())));
            sendCountdownstate(Transfertext);

            //checks if sensor is connected
            if(mSensor != null){
                Values.setBluetoothok(mSensor.isConnected());
            }else{
                Values.setBluetoothok(false);
            }

            // on every full heartbeat the following events are triggered
            if((int)Values.getStartvalue() != spaceholder1) {
                // a log entry is pushed to the log array (see byteme() method)
                try {
                    stream.write(byteme());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Logs the duration of a whole heartbeat at a certain rate times the fraction of the heartbeat ofset. like event = 1s ofset is 20% of the beat event -> ofset time is 0.2 s
                Log.d(TAG,"Delay time " + (1-(Values.getStartvalue()-(int)Values.getStartvalue()))* (60/(double)Values.getRate()) + " seconds delay at rate "  + Values.getRate() );
                // the selected sound is played
                switch (Values.getSoundselected()) {
                    case 0:

                    myKicksound.play(kicksoundID, (float) 0.6,(float) 0.6, 1, 0, 1);
                    spaceholder1 = (int) Values.getStartvalue();

                        break;
                    case 1:
                        myKicksound.play(gunnarsoundID1, 1, 1, 1, 0, 1);
                        spaceholder1 = (int) Values.getStartvalue();

                        break;
                    case 2:
                        myKicksound.play(lisasoundID1, 1, 1, 1, 0, 1);
                        spaceholder1 = (int) Values.getStartvalue();

                        break;

                    case 3:
                        myKicksound.play(gunnarsoundID2, 1, 1, 1, 0, 1);
                        spaceholder1 = (int) Values.getStartvalue();

                        break;
                    case 4:
                        myKicksound.play(lisasoundID2, 1, 1, 1, 0, 1);
                        spaceholder1 = (int) Values.getStartvalue();

                        break;

                    case 5:
                        myKicksound.play(gunnarsoundID3, 1, 1, 1, 0, 1);
                        spaceholder1 = (int) Values.getStartvalue();

                        break;
                    case 6:
                        myKicksound.play(lisasoundID3, 1, 1, 1, 0, 1);
                        spaceholder1 = (int) Values.getStartvalue();

                        break;
                    //neu
                    case 7:
                        myKicksound.play(lisasoundID3, 1, 1, 1, 0, 1);
                        spaceholder1 = (int) Values.getStartvalue();

                        break;
                    case 8:
                        myKicksound.play(lisasoundID3, 1, 1, 1, 0, 1);
                        spaceholder1 = (int) Values.getStartvalue();

                        break;
                    case 9:
                        myKicksound.play(lisasoundID3, 1, 1, 1, 0, 1);
                        spaceholder1 = (int) Values.getStartvalue();

                        break;
                    case 10:
                        myKicksound.play(lisasoundID3, 1, 1, 1, 0, 1);
                        spaceholder1 = (int) Values.getStartvalue();

                        break;
                    case 11:
                        myKicksound.play(lisasoundID3, 1, 1, 1, 0, 1);
                        spaceholder1 = (int) Values.getStartvalue();

                        break;
                    case 12:
                        myKicksound.play(lisasoundID3, 1, 1, 1, 0, 1);
                        spaceholder1 = (int) Values.getStartvalue();

                        break;
                    case 13:
                        myKicksound.play(lisasoundID3, 1, 1, 1, 0, 1);
                        spaceholder1 = (int) Values.getStartvalue();

                        break;
                    case 14:
                        myKicksound.play(lisasoundID3, 1, 1, 1, 0, 1);
                        spaceholder1 = (int) Values.getStartvalue();

                        break;


            }
            }

            // sets display color to green if cast and sensor are working
            if( Values.getCastok() == true && Values.getBluetoothok() == true){
                //Log.d(TAG,"Launcherdebug ontick cast and bluetooth are ok");
                mRelativelayout.setBackgroundColor(Color.GREEN);

            }else{
                //sets displaycolor to red if one system is not connected
                if(!Values.getCastok() ){
                   // Toaster.makeText(getApplicationContext(), "Keine Verbindung zu Cast", Toast.LENGTH_SHORT).show();
                }
                mRelativelayout.setBackgroundColor(Color.RED);
            }

        }

        @Override
        public void onFinish() {
            countdownnr.setText("Time expired. restart!");
        }
    }

    //plus and minus rate are stetter metohds for the haert rate and refresh the display accordingly
    public void plusrate(){
        Values.setRate((Values.getRate() + 1));
        Heartrate.setText("" + Values.getRate());
    }

    public void minusrate(){
        if (Values.getRate() > 1) {
            Values.setRate((Values.getRate() - 1));
            Heartrate.setText("" + Values.getRate());
        }
    }

    // start() method initialize log stream and starts a countdown with the duration of 8hs
    private void start() {
        try {

            stream = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), "onedown-log-" + System.currentTimeMillis() + ".csv"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (countDownTimer != null) {
            return;
        }
        countDownTimer = new mycountDownTimer(28800 * 1000, 50);
        countDownTimer.start();

    }

    // stop() finishes log file and stops countdown
    private void stop() {
        if(stream != null){
        try {
            stream.flush();
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }



    }





}
