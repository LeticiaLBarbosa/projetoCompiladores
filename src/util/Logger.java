package util;

/**
 * Created by Leticia on 31/08/2016.
 */
public class Logger {

    private static boolean activate_log = true;

    public static void print(String value) {
        if (activate_log)
            System.out.println(value);
    }

}