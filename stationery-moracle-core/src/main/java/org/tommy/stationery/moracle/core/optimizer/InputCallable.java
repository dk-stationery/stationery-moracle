package org.tommy.stationery.moracle.core.optimizer;

import com.espertech.esper.client.EPRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tommy.stationery.moracle.core.domain.Config;
import org.tommy.stationery.moracle.core.domain.MStream;
import org.tommy.stationery.moracle.core.enums.ConfigEnum;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by kun7788 on 15. 4. 29..
 */
public class InputCallable implements Callable {
    private static final Logger logger = LoggerFactory.getLogger(InputCallable.class);

    private Config config;
    private EPRuntime runtime;
    private MStream stream;
    private int headerSize;
    private Map<String, List<String>> headers;

    public InputCallable(Map<String, List<String>> headers, Config config, EPRuntime runtime, MStream stream) {
        this.config = config;
        this.headers = headers;
        this.runtime = runtime;
        this.stream = stream;
    }

    @Override
    public Boolean call() throws Exception {
        String filePath = config.getString(ConfigEnum.inputPath) + stream.getName() + config.getString(ConfigEnum.fileExtension);
        String fileEncoding = config.getString(ConfigEnum.fileEncoding);
        String seperator = config.getString(ConfigEnum.seperator);
        boolean isExistHeader = "Y".equals(config.getString(ConfigEnum.isHeader)) ? true : false;

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), fileEncoding));
        if (isExistHeader == true) {
            in.readLine();
        }
        List<String> header = headers.get(stream.getName());

        logger.info("headers : " + header.toString());
        String rawRow = null;
        while ((rawRow = in.readLine()) != null) {
            rawRow = rawRow.replace("\"", "");
            String[] rows = rawRow.split(seperator);
            if (rows.length == header.size()) {
                Map<String, Object> data = new HashMap<String, Object>();
                for (int index = 0;index<header.size();index++) {
                    String fieldName = header.get(index);
                    Object fieldValue = rows[index];
                    data.put(fieldName, fieldValue);
                }

                runtime.sendEvent(data, stream.getName());
            } else {System.out.println("file read finish." + rows.length+ "   " + header.size());
                logger.error("header, rows size differenct : " + rows.length + "," + headerSize);
                System.exit(1);
            }
        }
        in.close();
        logger.info("file read finish.");
        return true;
    }
}
