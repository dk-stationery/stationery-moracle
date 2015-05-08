package org.tommy.stationery.moracle.jdbc.client;

import org.tommy.stationery.moracle.core.domain.Config;
import org.tommy.stationery.moracle.core.domain.MoracleReturnData;
import org.tommy.stationery.moracle.core.enums.ConfigEnum;
import org.tommy.stationery.moracle.jdbc.exception.MoracleSQLException;

import java.util.List;
import java.util.Map;

/**
 * Created by kun7788 on 15. 5. 7..
 */
public class MoracleRestClient {
    static final boolean DEBUG = false;

    List _params;
    int pos;

    private Map<ConfigEnum, String> params;
    private MoracleHttpClient client;
    private String url;
    private String sql;

    public void close() {

    }

    public MoracleRestClient() {

    }

    public MoracleReturnData send(String sql) {
        return client.send(sql);
    }

    public MoracleReturnData send() {
        return client.send(sql);
    }

    public MoracleRestClient(String url) {
        client = new MoracleHttpClient(new Config(Config.decode(url)));
    }

    public MoracleRestClient(String url, String sql)
            throws MoracleSQLException, Exception {
        this.url = url;
        this.sql = sql;

        client = new MoracleHttpClient(new Config(Config.decode(url)));

        if ( DEBUG ) System.out.println( sql );
    }

    void setParams( List params ){
        pos = 1;
        this._params = params;
    }

    public MoracleReturnData query() throws MoracleSQLException {
        MoracleReturnData ret = client.send(sql);
        return ret;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}