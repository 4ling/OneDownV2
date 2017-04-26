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

/**
 * SeetingsActivity offers the interfacte for the user to change the values of coundownstate, starttext, upcount and the soundpool id on the device.
 */
public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    //the settings activity has its own value dataframe, it copies the data from shared preferences in the oncreate method
    public static ValuesUtil Values = new ValuesUtil(0,0,0,0);

    //layout variables
    public Button setall;
    public Button reset;
    Toast Toaster;
    private EditText number;
    private EditText newText;
    private Spinner soundselect;
    private TextView batterylvl;
    private TextView startext;
    private TextView countdownnr;

    // onclick listerner for buttons
    public final ThreadLocal<View.OnClickListener> trigger = new ThreadLocal<View.OnClickListener>() {
        @Override
        protected View.OnClickListener initialValue() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View click) {
                    switch (click.getId()) {
                        /**
                         * setall collects all data entered by the user and inserts them in the Values dataframe and forward them to shared preferences
                         * retrieves data from textedits with retrieveCountdown() and retriveText();
                         * saves them in shared preferences with setshareableInfo() ;
                         * refreshes textviews;
                         *
                          */

                        case R.id.setall:

                            retrieveCountdown();
                            retriveText();

                            setshareableInfo();
                            startext.setText("Aktueller Starttext: \n" + Values.getStarttext());
                            if(Values.getStarttext().contains("NOSHADE")){
                                startext.setText("Aktueller Starttext: \nDefault");
                            }
                            countdownnr.setText("Aktueller Countdownstand: \n" + Values.getnewCountdownvalue());

                            Toaster.makeText(getApplicationContext(), "Neue Daten übernommen", Toast.LENGTH_LONG).show();
                            break;
                        /**
                         *  sets upcount to 0 and start text to the default message with reset();
                         */

                        case R.id.reset:

                            reset();

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
        // retrieves Values from launcher activity
        getshareableInfo();

        // sets display output
        number = (EditText)findViewById(R.id.newcountdown);
        newText = (EditText)findViewById(R.id.newtext);
        batterylvl = (TextView) findViewById(R.id.batterylvl);
        batterylvl.setTextSize(20);
        batterylvl.setText("Akkustand xCell: \n" + Values.getBatterylvl()+" %");
        startext = (TextView) findViewById(R.id.starttext);
        startext.setTextSize(20);
        startext.setText("Aktueller Starttext: \n" + Values.getStarttext());
        if(Values.getStarttext().contains("NOSHADE")){
            startext.setText("Aktueller Starttext: \nDefault");
        }

        countdownnr = (TextView) findViewById(R.id.countdownnr);
        countdownnr.setTextSize(20);
        countdownnr.setText("Aktueller Countdownstand: \n" + Values.getnewCountdownvalue());


        setall = (Button) findViewById(R.id.setall);
        setall.setOnClickListener(trigger.get());

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(trigger.get());

        // for selection of the required sound file
        soundselect = (Spinner) findViewById(R.id.soundselect);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.sounds,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        soundselect.setAdapter(adapter);
        soundselect.setOnItemSelectedListener(this);
        soundselect.setSelection(Values.getSoundselected());



    }

    /**
     * retieves Values from LAuncher activity
     */
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
        super.onPause();
    }


    /**
     * adds castbutton.
     * Inflate the menu; this adds items to the action bar if it is present.
      */

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_launcher, menu);
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.Castbutton);
        return true;
    }

    /**
     * sets upcount to 0 and starttext to the default message;
     */

    public void reset(){
        Values.setStarttext("<DIV id=\"message\">used heartbeats <HR NOSHADE SIZE=1> <td style=\"vertical-align:top\"> remaining heartbeats</td></DIV>");
        startext.setText("Aktueller Starttext: \nDefault");


        Values.setUpcount(0);
    }


    /**
     * retrieves new countdownvakue from textedit and saves it in Values dataframe
     * if no text is inserted the text is reset to default.
     * @exception NumberFormatException is thrown when text is forwarded instead of a number.
     */
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
    /**
     * retrieves new starttext from textedit and saves it in Values dataframe
     * if no text is inserted the text is reset to default.
     */

    public void retriveText(){
        String spaceholder = newText.getText().toString();
        Log.d(TAG,"Settingsdebug retrievetext param: " + spaceholder);
        if(spaceholder.length() > 0){
            Values.setStarttext(spaceholder);
            startext.setText("Aktueller Starttext: \n" + Values.getStarttext());

        }else{
            Values.setStarttext("<DIV id=\"message\">used heartbeats <HR NOSHADE SIZE=1> <td style=\"vertical-align:top\"> remaining heartbeats</td></DIV>");
            startext.setText("Aktueller Starttext: \nDefault");
        Log.d(TAG,"Settingsdebug retrievedtext string was empty");
        }
    }
    /**
     *    adds SharedPreferences called sharedValues.
     *    saves Values in sharedValues in order to transfer them to Launcher activity
     */
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
    /**
     *    retrieves sharedValues from SharedPreferences.
     *    saves sharedValues in Values from Launcheractivity.
     */
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

    /**
     *  shows soundselect array in spinner
     *  sets Value of selected sound ID to selected spinner position
     *  the spinnerposition is then forwarded to Launcher activity were the damanded sound is played
     *  according to spinner position.
     * @param parent parent clas
     * @param view view of spinner
     * @param position position of spinner
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected=parent.getItemAtPosition(position).toString();
        Log.d(TAG,"Settingsdebug ontidemselected item selected " + selected);
        Values.setSoundselected(soundselect.getSelectedItemPosition());
        Log.d(TAG,"Settingsdebug ontidemselected position selected " + soundselect.getSelectedItemPosition());
    }

    /**
     * sends a log when so item is selected in spinner
     * @param parent parent class
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(TAG,"Settingsdebug ontidemselected no item selected ");
    }
}
