package org.tommy.stationery.moracle.jdbc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tommy.stationery.moracle.core.domain.Config;
import org.tommy.stationery.moracle.core.domain.MoracleReturnData;
import org.tommy.stationery.moracle.core.optimizer.Optimizer;

/**
 * Created by kun7788 on 15. 5. 7..
 */
public class MoracleHttpClient  {

    private static String API_SUB_PATH = "/sql/run";
    private static String API_SQL_PARAM = "sql";
    private static String API_SESSION_PARAM = "sessionId";

    private static final Logger logger = LoggerFactory.getLogger(MoracleHttpClient.class);

    private String url;
    private Config config;
    public MoracleHttpClient(Config config) {
        this.config = config;
    }

    public void close() {

    }

    public MoracleReturnData send(String sql) {
        Optimizer optimizer = new Optimizer(config);
        MoracleReturnData returnUrl = null;
        try {
            returnUrl = optimizer.start(sql);
        } catch (Exception e) {
        }
        return returnUrl;
    }

    public String getUrl() {
        return url;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
