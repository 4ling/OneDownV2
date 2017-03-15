package de.vierling.andreas.onedownv2;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
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

    SoundPool myKicksound;
    int kicksoundID;
    int gunnarsoundID1;
    int lisasoundID1;
    int gunnarsoundID2;
    int lisasoundID2;
    int gunnarsoundID3;
    int lisasoundID3;

    int spaceholder1;


    private static final String TAG = Launcher.class.getSimpleName();

    private CastContext NewContext;
    private CastSession NewCastSession;
    private CastChannel NewCastChannel;
    private String Transfertext;


    //Logmich
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/onedownlog";
    Loggingactivity loggingactivity;
    int logcount = 0;



    public static ValuesUtil Values = new ValuesUtil(80,2147000000,367499,0);



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


    DsBleSensor mSensor;
    private Context getThis() {
        return this;
    }
    private SensorDataProcessor mDataHandler = new SensorDataProcessor() {
        @Override
        public void onNewData(SensorDataFrame sensorDataFrame) {
            Log.d(TAG, "new data: " + sensorDataFrame);
            if (sensorDataFrame instanceof HeartRateDataFrame) {
                HeartRateDataFrame hr = (HeartRateDataFrame)sensorDataFrame;
                Log.d(TAG, "HR: " + hr.getHeartRate());

                Values.setRate((int) hr.getHeartRate());

                //ringbuffer.addvalue(hr.getInterbeatInterval());
                Heartrate.setText("" + Values.getRate());
            }
        }
    };



    //
    //private Ringbuffer ringbuffer = new Ringbuffer(25,0.3);
    private numberutil inttostirng = new numberutil();
    private TextView countdownnr;
    private TextView Heartrate;
    private Button start;
    private Button settings;
    private Button stop;
    private Button rateplus;
    private Button rateminus;
    private Button connect;
    private Button send;
    private double interval;
    private RelativeLayout mRelativelayout;


    private CountDownTimer countDownTimer;
    Toast Toaster;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();




    public final ThreadLocal<View.OnClickListener> trigger = new ThreadLocal<View.OnClickListener>() {
        @Override
        protected View.OnClickListener initialValue() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View click) {
                    switch (click.getId()) {
                        case R.id.start:

                            start();

                            break;

                        case R.id.stop:

                            stop();

                            break;
                        case R.id.settings:
                            Intent intent = new Intent(Launcher.this, SettingsActivity.class);
                            startActivity(intent);
                            break;

                        case R.id.rateplus:
                            //Log.d(TAG,"button rate pushed");
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



                        case R.id.rateminus:
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
                        case R.id.send:


                            Log.d(TAG,"valuesdebug setnew countdownstate" + (int)Values.getStartvalue() +"  ln" + Values.getLargenumber());


                            myKicksound.play(kicksoundID,1,1,1,0,1);
                            setTransfertext(Values.getStarttext());
                            sendCountdownstate(Transfertext);

                            break;
                        case R.id.connect:
                            if(bluetoothAdapter.isEnabled() != true){
                                Toaster.makeText(getApplicationContext(), "Bitte Bluetooth aktivieren!", Toast.LENGTH_LONG).show();
                                break;
                            }
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
                                                Log.d(TAG, "Sleeping for 5");
                                                Thread.sleep(5000);
                                                // ...and start streaming data.
                                                // New data will now appear in the callback above.  DE:2E:82:E1:65:CD
                                                mSensor.startStreaming();
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
        kicksoundID = myKicksound.load(this,R.raw.kick,1);
        gunnarsoundID1 = myKicksound.load(this,R.raw.heartbeatgunnar,1);
        lisasoundID1 = myKicksound.load(this,R.raw.heartbeatlisa,1);
        gunnarsoundID2 = myKicksound.load(this,R.raw.heartbeatgunnar1,1);
        lisasoundID2 = myKicksound.load(this,R.raw.heartbeatlisa1,1);
        gunnarsoundID3 = myKicksound.load(this,R.raw.heartbeatgunnar2,1);
        lisasoundID3 = myKicksound.load(this,R.raw.heartbeatlisa2,1);

        //create directory for log
        //Logmich
        File dir = new File(path);
        dir.mkdir();
        Log.d(TAG,"" + dir.mkdir());

        loggingactivity = new Loggingactivity(dir,path);




        // For Android 6+ we have to make sure that we have the BLE permissions
        try {
            DsSensorManager.checkBtLePermissions(this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //Initialize Values
        Heartrate = (TextView) findViewById(R.id.Heartrate);
        Heartrate.setTextSize(25);
        Heartrate.setText("" + Values.getRate());
        countdownnr = (TextView) findViewById(R.id.countdownnr);
        countdownnr.setTextSize(25);
        countdownnr.setText("" + ((long) Values.getStartvalue() + Values.getLargenumber()));
        Values.setStarttext("Person A");
        setshareableInfo();




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

        NewContext = CastContext.getSharedInstance(this);
        NewContext.registerLifecycleCallbacksBeforeIceCreamSandwich(this, savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher, menu);
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.Castbutton);
        return true;
    }




    @Override
    protected void onResume(){

        getshareableInfo();
        Log.d(TAG,"settingsdebug onresumecalled" + (int)Values.getStartvalue());
        super.onResume();
        countdownnr.setText("" + ((long) Values.getStartvalue() + Values.getLargenumber()));

        NewContext.getSessionManager().addSessionManagerListener(NewCastSessionManagerListener,CastSession.class);
        if(NewCastSession == null){
            NewCastSession = NewContext.getSessionManager().getCurrentCastSession();
        }
    }

    @Override
    protected void onPause(){
        setshareableInfo();
        Toaster.makeText(getApplicationContext(), "App pausiert.", Toast.LENGTH_SHORT).show();
        super.onPause();
        NewContext.getSessionManager().removeSessionManagerListener(NewCastSessionManagerListener,CastSession.class);
    }

    @Override
    protected void onDestroy(){
        if(mSensor != null) {
            mSensor.stopStreaming();
            mSensor.disconnect();
        }

        stop();
        closeSession();
        super.onDestroy();

    }


    //set shared info for (int newrate, long newlargenumber, double newstartvalue, double newupcount

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
        //castnamespace eintragen
    }




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

    private void sendCountdownstate(String Countdownstate){
        if (NewCastChannel != null) {
            NewCastSession.sendMessage(NewCastChannel.getCastname(),Countdownstate);
        }
        else {
           // if(Toaster != null){
             //   Toaster.cancel();
            //}

            Values.setCastok(false);
        }

    }

    public void setTransfertext(String text){
        Transfertext = text;
    }

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


 public class mycountDownTimer extends CountDownTimer {
        /**
         */
        public void errortoast(){
            if(Toaster == null) {
                Toaster.makeText(getApplicationContext(), "Keine Verbindung zu Cast", Toast.LENGTH_SHORT).show();
        }
        else {
            Log.d(TAG,"Toasterdebug cancel called");
            Toaster.cancel();
        }

        }


        public mycountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Values.setStartvalue((double) ((Values.getStartvalue() - ((double) Values.getRate() / 600))));
            Heartrate.setText("" + Values.getRate());
            countdownnr.setText("" + (long) (Values.getStartvalue() + Values.getLargenumber()));
            Values.setUpcount(Values.getUpcount()+((double) Values.getRate() / 600));
            setTransfertext(inttostirng.pointify((long)Values.getUpcount()) + " <HR NOSHADE SIZE=1>" +  inttostirng.pointify((long) (Values.getStartvalue() + Values.getLargenumber())));
            sendCountdownstate(Transfertext);
            if(logcount%20 == 0) { //20 deswegen damit nur alle 2 s geloggt wird das sollte reichen
                //loggingactivity.save("" + Values.getRate());
                //TODO hallo stefan  hier löst es in meiner load funktion klasse loggingactivity immer den nullpointer aus seltsamer weise erstellt es mir auch nicht das verzeichniss
                //obwohl ich das in der oncreate aufrufe unter //Logmich finest du alle dazu angelegten elemente

            }
            logcount++;
            if(mSensor != null){
                Values.setBluetoothok(mSensor.isConnected());
            }else{
                Values.setBluetoothok(false);
            }


            if((int)Values.getStartvalue() != spaceholder1) {
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

            }
            }


            if( Values.getCastok() == true && Values.getBluetoothok() == true){
                //Log.d(TAG,"Launcherdebug ontick cast and bluetooth are ok");
                mRelativelayout.setBackgroundColor(Color.GREEN);

            }else{
                if(!Values.getCastok() ){
                   errortoast();
                }
                mRelativelayout.setBackgroundColor(Color.RED);
            }

        }

        @Override
        public void onFinish() {
            countdownnr.setText("Time expired. restart!");
        }
    }

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


    private void start() {
        if (countDownTimer != null) {
            return;
        }
        countDownTimer = new mycountDownTimer(28800 * 1000, 100);
        countDownTimer.start();

    }

    private void stop() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }



    }

}
