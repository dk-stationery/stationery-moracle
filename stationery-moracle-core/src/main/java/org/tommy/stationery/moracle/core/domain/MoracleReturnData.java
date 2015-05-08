package org.tommy.stationery.moracle.core.domain;

import java.util.List;

/**
 * Created by kun7788 on 15. 5. 8..
 */
public class MoracleReturnData {
    private List<MColumn> columns;
    private String url;
    private Config config;

    public List<MColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<MColumn> columns) {
        this.columns = columns;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
