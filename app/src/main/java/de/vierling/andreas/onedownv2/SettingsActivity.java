package de.vierling.andreas.onedownv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.cast.framework.CastButtonFactory;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    public static ValuesUtil Values = new ValuesUtil(0,0,0,0);

    public Button setall;
    Toast Toaster;
    private EditText number;
    private EditText newText;
    private Spinner soundselect;
    private TextView batterylvl;
    private TextView startext;
    private TextView countdownnr;

    public final ThreadLocal<View.OnClickListener> trigger = new ThreadLocal<View.OnClickListener>() {
        @Override
        protected View.OnClickListener initialValue() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View click) {
                    switch (click.getId()) {
                        case R.id.setall:


                            retrieveCountdown();
                            retriveText();


                            setshareableInfo();
                            startext.setText("Aktueller Starttext: \n" + Values.getStarttext());
                            countdownnr.setText("Aktueller Counddownstand: \n" + Values.getnewCountdownvalue());

                            Toaster.makeText(getApplicationContext(), "Neue Daten übernommen", Toast.LENGTH_LONG).show();
                            break;


                    }

                }
            };
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getshareableInfo();

        number = (EditText)findViewById(R.id.newcountdown);
        newText = (EditText)findViewById(R.id.newtext);
        batterylvl = (TextView) findViewById(R.id.batterylvl);
        batterylvl.setTextSize(20);
        batterylvl.setText("Akkustand xCell: \n" + Values.getBatterylvl()+" %");
        startext = (TextView) findViewById(R.id.starttext);
        startext.setTextSize(20);
        startext.setText("Aktueller Starttext: \n" + Values.getStarttext());

        countdownnr = (TextView) findViewById(R.id.countdownnr);
        countdownnr.setTextSize(20);
        countdownnr.setText("Aktueller Countdownstand: \n" + Values.getnewCountdownvalue());

        setall = (Button) findViewById(R.id.setall);
        setall.setOnClickListener(trigger.get());

        soundselect = (Spinner) findViewById(R.id.soundselect);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.sounds,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        soundselect.setAdapter(adapter);
        soundselect.setOnItemSelectedListener(this);
        soundselect.setSelection(Values.getSoundselected());



    }

    @Override
    protected void onResume() {
        Log.d(TAG,"settingsbug onresume called");
        Log.d(TAG,"settingsbug oldinfo" + (int) Values.getStartvalue());
        getshareableInfo();
        Log.d(TAG,"settingsbug newinfo" + (int) Values.getStartvalue());
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG,"settingsbug onpause called");
        //setshareableInfo();
        super.onPause();
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launcher, menu);
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.Castbutton);
        return true;
    }





    public void retrieveCountdown() {
        String spaceholder = number.getText().toString();
        long output;
        try {
            output = Long.parseLong(spaceholder);
            Log.d(TAG, "Settingsdebug retrievecountdown param: " + output);
            Values.setnewCountdownvalue(output);
        } catch (NumberFormatException nfe) {
            Log.d(TAG, "Settingsdebug retrievecountdown wrong numberformat");
        }
    }

    public void retriveText(){
        String spaceholder = newText.getText().toString();
        Log.d(TAG,"Settingsdebug retrievetext param: " + spaceholder);
        if(spaceholder != ""){
            Values.setStarttext(spaceholder);
        }else{
        Log.d(TAG,"Settingsdebug retrievedtext string was empty");
        }
    }

    public void setshareableInfo(){

        Log.d(TAG,"shared preferences saved in settings");
        SharedPreferences sharedValues = getSharedPreferences("Values", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedValues.edit();
        editor.putInt("heartrate", Values.getRate());
        editor.putLong("largenumber", Values.getLargenumber());
        editor.putInt("startvalue", (int)Values.getStartvalue());
        editor.putInt("upcount", (int) Values.getUpcount());
        editor.putString("starttext", Values.getStarttext());
        editor.putInt("selectedsound", Values.getSoundselected());
        editor.apply();
        Log.d(TAG,"shared preferences saved in settings");
    }

    public void getshareableInfo(){
        Log.d(TAG,"shared preferences called in settings");
        SharedPreferences sharedValues = getSharedPreferences("Values", Context.MODE_PRIVATE);
        Values.setRate(sharedValues.getInt("heartrate", Values.getRate()));
        Values.setLargenumber(sharedValues.getLong("largenumber",Values.getLargenumber()));
        Values.setStartvalue((double) sharedValues.getInt("startvalue",(int)Values.getStartvalue()));
        Values.setUpcount((double) sharedValues.getInt("upcount",(int)Values.getUpcount()));
        Values.setSoundselected(sharedValues.getInt("selectedsound",Values.getSoundselected()));
        Values.setBatterylvl(sharedValues.getInt("batterylvl",Values.getBatterylvl()));
        Values.setStarttext(sharedValues.getString("starttext",Values.getStarttext()));
        Log.d(TAG,"shared preferences completed in settings");
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected=parent.getItemAtPosition(position).toString();
        Log.d(TAG,"Settingsdebug ontidemselected item selected " + selected);
        Values.setSoundselected(soundselect.getSelectedItemPosition());
        Log.d(TAG,"Settingsdebug ontidemselected position selected " + soundselect.getSelectedItemPosition());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(TAG,"Settingsdebug ontidemselected no item selected ");
    }
}
