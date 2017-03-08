package de.vierling.andreas.onedownv2;

import java.text.DecimalFormat;

/**
 * Created by Andi on 20.02.2017.
 */

public class numberutil {


    public String pointify(long number){
        DecimalFormat newformat = new DecimalFormat();
        String output = newformat.format(number);
        return output;

    }

}
