package com.carl.interesting.common.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * 该类继承于ResultSetExtractable，用不同的方式处理查询结果集
 * 
 * @author Yangbin Zhang
 * @version [version, 7 Apr 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class ResultSet2Object implements ResultSetExtractable {
    public Object populate(ResultSet rs) throws SQLException {
        ResultSetMetaData dbmd = rs.getMetaData();
        int columnCount = dbmd.getColumnCount();
        if (columnCount == 1)
            return rs.getObject(1);
        Object[] result = new Object[columnCount];
        for (int i = 0; i < columnCount; i++) {
            result[i] = rs.getObject(i + 1);
        }
        return result;
    }
}
