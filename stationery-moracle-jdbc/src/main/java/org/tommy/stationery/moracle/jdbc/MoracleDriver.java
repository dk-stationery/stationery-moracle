package org.tommy.stationery.moracle.jdbc;

import org.tommy.stationery.moracle.jdbc.client.MoracleRestClient;

import java.sql.*;
import java.util.Properties;

/**
 * Created by kun7788 on 15. 5. 7..
 */
public class MoracleDriver implements Driver {

    static final String PREFIX = "moracle://";

    static {
        try {
            DriverManager.registerDriver(new MoracleDriver());
        }
        catch ( SQLException e ){
            throw new RuntimeException( e );
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if ( url.startsWith( PREFIX ) ) {
            url = url.substring(PREFIX.length());
        }
        //inputPath=/Users/kun7788/Desktop/input/&seperator=,&fileExtension=.csv&fileEncoding=MS949&isHeader=Y
        MoracleRestClient moracleRestClient = new MoracleRestClient(url);
        return new MoracleConnection(moracleRestClient);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith( PREFIX );
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String s, Properties properties) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 1;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }
}
