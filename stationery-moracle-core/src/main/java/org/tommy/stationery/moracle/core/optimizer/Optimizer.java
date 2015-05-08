package org.tommy.stationery.moracle.core.optimizer;

import com.espertech.esper.client.*;
import com.espertech.esper.client.soda.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tommy.stationery.moracle.core.domain.Config;
import org.tommy.stationery.moracle.core.domain.MColumn;
import org.tommy.stationery.moracle.core.domain.MStream;
import org.tommy.stationery.moracle.core.domain.MoracleReturnData;
import org.tommy.stationery.moracle.core.enums.ConfigEnum;

import java.io.*;
import java.util.*;

/**
 * Created by kun7788 on 15. 4. 28..
 */
public class Optimizer {
    private static final Logger logger = LoggerFactory.getLogger(Optimizer.class);

    private Config config;
    private List<MStream> streams;
    private List<MColumn> columns;
    private transient EPServiceProvider esperSink;
    private transient EPRuntime runtime;
    private transient EPAdministrator admin;
    private OutputExecutor outputExecutor;
    private InputExecutor inputExecutor;
    private Configuration configuration;
    private Map<String, List<String>> headers = new HashMap<String, List<String>>();
    private String returnUrl;

    public Optimizer(Config config) {
        this.config = config;
    }

    private List<MColumn> extractColumn(String sql) {
        Configuration config = new Configuration();
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(config);
        EPStatementObjectModel model = epService.getEPAdministrator().compileEPL(sql);

        StringWriter sw = null;
        List<MColumn> columns = new ArrayList<MColumn>();

        for (SelectClauseElement select : model.getSelectClause().getSelectList()) {
            if (select instanceof SelectClauseExpression) {
                sw=new StringWriter();
                SelectClauseExpression expression=(SelectClauseExpression)select;

                String name = "";
                if (expression.getAsName() == null) {
                    expression.getExpression().toEPL(sw, ExpressionPrecedenceEnum.AND);
                    name = sw.toString();
                } else {
                    name = expression.getAsName();
                }

                MColumn column = new MColumn();
                column.setName(name);
                columns.add(column);
            }
        }

        for (MColumn column : columns) {
            logger.info("column : " + column.getName());
        }

        return columns;
    }

    private List<MStream> extractStreams(String sql) {
        Configuration config = new Configuration();
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(config);

        EPStatementObjectModel model = epService.getEPAdministrator().compileEPL(sql);
        List<Stream> eplStreams = model.getFromClause().getStreams();
        if (eplStreams == null || eplStreams.size() == 0) {
            throw new IllegalArgumentException("no streams in esper statement");
        }

        List<MStream> streams = new ArrayList<MStream>();
        for (Stream eplStream : eplStreams) {
            FilterStream fstream = (FilterStream)eplStream;
            MStream mStream = new MStream();
            mStream.setName(fstream.getFilter().getEventTypeName());
            streams.add(mStream);
        }
        logger.info("table : " + streams.toString());
        return streams;
    }

    private List<String> setupEventTypeProperties(MStream stream) throws IOException {
        String filePath = config.getString(ConfigEnum.inputPath) + stream.getName() + config.getString(ConfigEnum.fileExtension);
        String seperator = config.getString(ConfigEnum.seperator);
        boolean isExistHeader = "Y".equals(config.getString(ConfigEnum.isHeader)) ? true : false;

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String rawRow = in.readLine().replace("\"", "");
        List<String> headers = Arrays.asList(rawRow.split(seperator));

        logger.info("setupEventTypeProperties : " + headers.toString());

        Map<String, Object> properties = new HashMap<String, Object>();

        List<String> _headers = new ArrayList<String>();
        int index = -1;
        for (String header : headers) {
            Class<?> clazz = String.class;
            index++;
            if (isExistHeader == false) {
                header = "c" + index;
            }
            _headers.add(header);
        }
        return _headers;
    }

    private Configuration setupEventTypes(List<MStream> streams, Configuration configuration) throws IOException {
        for (MStream stream : streams) {
            List<String> header = setupEventTypeProperties(stream);
            Map<String, Object> properties = new HashMap<String, Object>();
            for (String headerName : header) {
                properties.put(headerName, String.class);
            }
            headers.put(stream.getName(), header);
            configuration.addEventType(stream.getName(), properties);
        }
        return configuration;
    }

    private String addWindowQuery(String sql) throws IOException {
        for (MStream stream : streams) {
            String filePath = config.getString(ConfigEnum.inputPath) + stream.getName() + config.getString(ConfigEnum.fileExtension);
            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(new File(filePath)));
            lineNumberReader.skip(Long.MAX_VALUE);
            int lines = lineNumberReader.getLineNumber();
            sql = sql.replace(stream.getName(), stream.getName() + ".win:length_batch(" + (lines-1) + ")");
        }

        logger.info("sql " + sql);
        return sql;
    }

    public MoracleReturnData start(String sql) throws Exception {
        streams = extractStreams(sql);
        columns = extractColumn(sql);
        sql = addWindowQuery(sql);

        configuration = new Configuration();
        configuration.configure("/esper-configuration.xml");
        configuration = setupEventTypes(streams, configuration);


        this.esperSink = EPServiceProviderManager.getProvider(this.toString(), configuration);
        this.esperSink.initialize();
        this.runtime = esperSink.getEPRuntime();
        this.admin = esperSink.getEPAdministrator();
        inputExecutor = new InputExecutor(headers, config, runtime, streams);
        this.outputExecutor = new OutputExecutor(config, inputExecutor, streams, columns);

        EPStatement statement = admin.createEPL(sql);
        statement.addListener(outputExecutor);

        startInputExecution();

        //delay
        Thread.sleep(2000);
        close();

        MoracleReturnData moracleReturnData = new MoracleReturnData();
        moracleReturnData.setUrl(outputExecutor.getOutputFilePath());
        moracleReturnData.setColumns(columns);
        moracleReturnData.setConfig(config);

        return moracleReturnData;
    }

    private void close() {
        admin.destroyAllStatements();
        esperSink.destroy();
    }

    private void startInputExecution() throws Exception {
        inputExecutor.polling();
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
}
