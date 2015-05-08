package org.tommy.stationery.moracle.jdbc.exception;

import java.sql.SQLException;

/**
 * Created by kun7788 on 15. 5. 7..
 */
public class MoracleSQLException  extends SQLException {

    public MoracleSQLException(String msg) {
        super(msg);
    }

    public static class BadSQL extends MoracleSQLException {
        BadSQL( String sql ){
            super( "bad sql: " + sql );
        }
    }
}
