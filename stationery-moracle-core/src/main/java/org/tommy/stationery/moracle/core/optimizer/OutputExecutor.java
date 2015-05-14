package org.tommy.stationery.moracle.core.optimizer;

import com.espertech.esper.client.*;
import org.tommy.stationery.moracle.core.domain.Config;
import org.tommy.stationery.moracle.core.domain.MColumn;
import org.tommy.stationery.moracle.core.domain.MStream;
import org.tommy.stationery.moracle.core.enums.ConfigEnum;

import java.io.*;
import java.util.List;

/**
 * Created by kun7788 on 15. 4. 28..
 */
public class OutputExecutor implements UpdateListener {

    private Config config;
    private List<MStream> streams;
    private List<MColumn> columns;
    private int rowCnt = 0;
    private InputExecutor inputExecutor;
    private String outputFilePath;

    public int getRowCnt() {
        return rowCnt;
    }

    public OutputExecutor(Config config, InputExecutor inputExecutor, List<MStream> streams, List<MColumn> columns) {
        this.config = config;
        this.inputExecutor = inputExecutor;
        this.streams = streams;
        this.columns = columns;
    }

    private void generateOutputFilePath() {
        if (outputFilePath == null) {
            String filePath = config.getString(ConfigEnum.inputPath);
            File file = new File(filePath + "tmp/");
            if (!file.exists()) {
                file.mkdir();
            }
            outputFilePath = filePath + "tmp/" + "result_" + System.currentTimeMillis() + ".csv";
        }
    }

    public String getOutputFilePath() {
        if (outputFilePath == null) {
            generateOutputFilePath();
        }
        return outputFilePath;
    }

    public void close() {

    }

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            System.out.println("file size : " + newEvents.length);
            generateOutputFilePath();

            BufferedWriter out = null;
            try {
                String fileEncoding = config.getString(ConfigEnum.fileEncoding);
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getOutputFilePath(), false), fileEncoding));
            } catch (UnsupportedEncodingException e) {
                System.out.println(e.getMessage());
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }

            //append header.
            String header = "";
            for (MColumn column : columns) {
                header +=column.getName() + ",";
            }
            try {
                out.write(header.substring(0, header.length() - 1));
                out.newLine();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            //append contents.
            for (EventBean newEvent : newEvents) {
                StringBuilder sw = new StringBuilder();
                for (MColumn column : columns) {
                    if (newEvent.get(column.getName()) == null) return;
                    sw.append(newEvent.get(column.getName()) + ",");
                }
                rowCnt++;
                //System.out.println(sw.toString().substring(0, sw.toString().length()-1));

                if (out != null) {
                    try {
                        out.write(sw.toString().substring(0, sw.toString().length() - 1));
                        out.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            inputExecutor.close();
        }
    }
}
