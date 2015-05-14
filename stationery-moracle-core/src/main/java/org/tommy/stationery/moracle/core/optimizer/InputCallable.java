package org.tommy.stationery.moracle.core.optimizer;

import com.espertech.esper.client.EPRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tommy.stationery.moracle.core.domain.Config;
import org.tommy.stationery.moracle.core.domain.MStream;
import org.tommy.stationery.moracle.core.enums.ConfigEnum;
import uk.elementarysoftware.quickcsv.api.CSVParserBuilder;
import uk.elementarysoftware.quickcsv.api.CSVRecord;
import uk.elementarysoftware.quickcsv.api.Field;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

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

    public static void row(CSVRecord r) {

    }

    @Override
    public Boolean call() throws Exception {
        String filePath = config.getString(ConfigEnum.inputPath) + stream.getName() + config.getString(ConfigEnum.fileExtension);
        String fileEncoding = config.getString(ConfigEnum.fileEncoding);
        String seperator = config.getString(ConfigEnum.seperator);
        boolean isExistHeader = "Y".equals(config.getString(ConfigEnum.isHeader)) ? true : false;
        List<String> header = headers.get(stream.getName());

        String _filePath = filePath;
        System.out.println("file : " + _filePath);
        /////////////////////////
        Stream<CSVRecord> records = null;
        try {
            records = CSVParserBuilder.aParser().forRfc4180().usingSeparatorWithNoQuotes(",".charAt(0)).skipFirstRecord()
                    .build().parse(new File(_filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }


        records.parallel().unordered().forEach(rows -> {
                    Field field = null;
                    Map<String, Object> data = new HashMap<String, Object>();
                    for (int index = 0; index < header.size(); index++) {
                        String fieldName = header.get(index);
                        Object fieldValue = rows.getNextField().asString();
                        data.put(fieldName, fieldValue);
                    }

                    runtime.sendEvent(data, stream.getName());
                }
        );

        System.out.println("file read finish.");
        logger.info("file read finish.");
        return true;
    }
}
