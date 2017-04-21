package de.vierling.andreas.onedownv2;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by Andi on 01.03.2017.
 */


//this class serves as dataframe and stores the systems parameters
public class ValuesUtil{

    private static final String TAG = ValuesUtil.class.getSimpleName();

    private int rate = 80;
    private long largenumber = 2147000000;
    private double startvalue = 367499;
    private double upcount = 0;
    private String Starttext = "";
    int batterylvl = 0;
    private Toast Toaster;
    Boolean Bluetoothok = false;
    Boolean Castok = false;
    int Soundselected = 0;

    private long spaceholder;

    ValuesUtil(int newrate, long newlargenumber, double newstartvalue, double newupcount ){
        rate = newrate;
        largenumber = newlargenumber;
        startvalue = newstartvalue;
        upcount = newupcount;
    }

// getter and setter methods

    public int getSoundselected(){
        return Soundselected;
    }
    public void setSoundselected(int id){
        Soundselected = id;
    }
    public int getBatterylvl(){
        return batterylvl;
    }
    public void setBatterylvl(int lvl){
        batterylvl =lvl;
    }
    public Boolean getBluetoothok() {
        return Bluetoothok;
    }
    public Boolean getCastok(){
        return Castok;
    }
    public void setBluetoothok(Boolean bool){
        Bluetoothok = bool;
    }
    public void setCastok(Boolean bool){
        Castok = bool;
    }
    public double getUpcount(){
        return upcount;
    }
    public void setUpcount(double input){
        upcount=input;
    }
    public int getRate(){
        return rate;
    }
    public void setRate(int input){
        if(input != 0) {
            if(input > 39 && input < 211 ) {
                rate = input;
            }
        }else{
            Toaster.setText("Bitte Sensor neu positionieren");
            Toaster.setDuration(Toaster.LENGTH_LONG);
            Toaster.show();
        }
    }
    public long getLargenumber(){
        return largenumber;
    }
    public void setLargenumber(long input){
        if(input != 0){
            largenumber = input;
        }
    }
    public double getStartvalue(){
        return startvalue;
    }
    public void setStartvalue(double input){
        if(input != 0) {
            startvalue = input;
        }
    }
    public String getStarttext() {
        return Starttext;
    }

    public void setStarttext(String starttext) {
        if(starttext != "") {
            Starttext = starttext;
        }
    }
    public void setnewCountdownvalue(long number){
        Log.d(TAG,"valuesdebug setnewnumber called");
        if(number>100000) {
            spaceholder = (number / 100000);
            setLargenumber(spaceholder*100000);
        }else{
            setLargenumber(0);
        }

        setStartvalue((double) (number - getLargenumber()));

        Log.d(TAG,"valuesdebug setnewnumber sv"+ getStartvalue() + " ln"+getLargenumber());
    }
    public long getnewCountdownvalue(){
        return (long)(getLargenumber()+getStartvalue());
    }
}


