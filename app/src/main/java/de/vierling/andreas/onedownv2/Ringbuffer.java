package de.vierling.andreas.onedownv2;

import android.util.Log;

/**
 * Created by Andi on 06.03.2017.
 */

public class Ringbuffer {

    private static final String TAG = Ringbuffer.class.getSimpleName();

    int size = 20;
    int counter =0;
    double[] intervals = new double[size];
    double derivationtolerance = 0.3;

    Ringbuffer(int newsize, double newderivationtolerance){
        for(int i = counter; i< counter+size;i++ ){
            addvalue(i);
        }
        derivationtolerance = newderivationtolerance;
        size = newsize;
    }

    public void addvalue(double newvalue){
        intervals[counter%size] = newvalue;
        counter++;
    }

    public double readvalue(int position){
        if (position<size){
        return intervals[position];
        }
        else{
            Log.d(TAG,"Ringbufferdebug readvalue illegal position called");
        return 0.0;}

    }

    public boolean checkforvariance(){
        for(int i = counter; i< counter+size;i++ ){
            if(readvalue(i-1) != readvalue(i)){
                checkforirregularities();
            }
        }

        Log.d(TAG, "Ringbufferdebug checkforvariance no variance detected");
        return false;
    }

    public boolean checkforirregularities(){
        for(int i = counter; i< counter+size;i++ ){
            if(readvalue(i-1)/readvalue(i) < 1-derivationtolerance && readvalue(i-1)/readvalue(i) > 1+derivationtolerance ){

                Log.d(TAG, "Ringbufferdebug checkforirregularities all values are fine");
                return true;
            }
        }
        Log.d(TAG, "Ringbufferdebug checkforirregularities irregularities detected");
        return false;

    }
}
