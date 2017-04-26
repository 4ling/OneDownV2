package de.vierling.andreas.onedownv2;

import java.text.DecimalFormat;

/**
 * Created by Andi on 20.02.2017.
 * this class provides a method for converting a long number into a string with deciaml formtat.
 */

public class numberutil {

    /**
     * transforms long number to decimal string
     * uses DecimalFormat class
     * @param number is the number which gets processed
     * @return returns string with number in decimal format
     */
    public String pointify(long number){
        DecimalFormat newformat = new DecimalFormat();
        String output = newformat.format(number);
        return output;

    }

}
