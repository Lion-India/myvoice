package app.com.worldofwealth.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by JASPER on 06-Apr-18.
 */

public class EncodeDecoder {

    public static String decodeURIComponent(String s)
    {
        if (s == null)
        {
            return null;
        }

        String result = null;

        try
        {
            result = URLDecoder.decode(s, "UTF-8");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e)
        {
            result = s;
        }

        return result;
    }

    public static String titledecodeURIComponent(String title)
    {
        if (title == null)
        {
            return null;
        }

        String result = null;

        try
        {
            result = URLDecoder.decode(title, "UTF-8");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e)
        {
            result = title;
        }

        return result;
    }

}
