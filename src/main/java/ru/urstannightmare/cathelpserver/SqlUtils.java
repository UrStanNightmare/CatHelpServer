package ru.urstannightmare.cathelpserver;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SqlUtils {
    public static void printRsFields(ResultSet rs){
        try {
            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.println(metadata.getColumnName(i));
            }
            System.out.println();
        }catch (SQLException ignored){
        }

    }
    private SqlUtils() {
    }
}
