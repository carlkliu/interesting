package com.carl.interesting.common.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * result set convert to map
 * 
 * @author Carl Liu
 * @version [version, 7 Apr 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class ResultSet2Map implements ResultSetExtractable {
    /**
     * result set convert to map
     * 
     * @param rs
     * @return
     * @throws SQLException
     */
    public HashMap<String, Object> populate(ResultSet rs) throws SQLException {
        ResultSetMetaData dbmd = rs.getMetaData();
        int columnCount = dbmd.getColumnCount();
        HashMap<String, Object> item = new HashMap<String, Object>(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            item.put(dbmd.getColumnLabel(i), rs.getObject(i));
        }
        return item;
    }
}