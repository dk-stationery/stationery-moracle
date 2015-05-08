package org.tommy.stationery.moracle.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by kun7788 on 15. 4. 30..
 */
public class ShellExecutor {
    public static Logger logger = LoggerFactory.getLogger(ShellExecutor.class);

    public static String executeCommand(String command) throws Exception {

        String CharEOF = "\r\n";
        StringBuffer output = new StringBuffer();

        Process p = null;
        p = Runtime.getRuntime().exec(new String[]{"bash", "-c", command});
        p.waitFor();
        BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line = "";
        while ((line = reader.readLine())!= null) {
            output.append(line + CharEOF);
        }

        int code = p.exitValue();
        if (code != 0) {
            throw new Exception(output.toString());
        }
        return output.toString();

    }

    public static void executeSimpleCommand(String command) {

        StringBuffer output = new StringBuffer();

        try {
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
