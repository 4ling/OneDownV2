package de.vierling.andreas.onedownv2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Andi on 15.03.2017.
 */

public class Loggingactivity {
    public String path;
   // File dir; // = new File(path);
    String date;
    File dir;

    Loggingactivity(File newdir, String newpath){
        dir = newdir;
        date =  String.valueOf(System.currentTimeMillis());
        path = newpath;
    }

    public void save(String text)
    {
        File file = new File (path + "/logdate" + date + ".txt");
        if(file != null) {
            String[] loadText = Load(file);
            String[] saveText = String.valueOf(text).split(System.getProperty("line.separator"));
            String[] both = mergeArrays(loadText, saveText);
            Save (file, both);
        }else{
            String[] saveText = String.valueOf(text).split(System.getProperty("line.separator"));
            Save (file, saveText);
        }

        //Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();


    }
    public static String[] Load(File file)
    {
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);

        String test;
        int anzahl=0;
        try
        {
            while ((test=br.readLine()) != null)
            {
                anzahl++;
            }
        }
        catch (IOException e) {e.printStackTrace();}

        try
        {
            fis.getChannel().position(0);
        }
        catch (IOException e) {e.printStackTrace();}

        String[] array = new String[anzahl];

        String line;
        int i = 0;
        try
        {
            while((line=br.readLine())!=null)
            {
                array[i] = line;
                i++;
            }
        }
        catch (IOException e) {e.printStackTrace();}
        return array;
    }

    public static void Save(File file, String[] data)
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        try
        {
            try
            {
                for (int i = 0; i<data.length; i++)
                {
                    fos.write(data[i].getBytes());
                    if (i < data.length-1)
                    {
                        fos.write("\n".getBytes());
                    }
                }
            }
            catch (IOException e) {e.printStackTrace();}
        }
        finally
        {
            try
            {
                fos.close();
            }
            catch (IOException e) {e.printStackTrace();}
        }
    }




    public String[] mergeArrays(String[] mainArray, String[] addArray) {
        String[] finalArray = new String[mainArray.length + addArray.length];
        System.arraycopy(mainArray, 0, finalArray, 0, mainArray.length);
        System.arraycopy(addArray, 0, finalArray, mainArray.length, addArray.length);

        return finalArray;
    }

}
