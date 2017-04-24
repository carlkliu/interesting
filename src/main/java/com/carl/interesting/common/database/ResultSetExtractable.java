package com.carl.interesting.common.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * result set interface
 * 
 * @author Carl Liu
 * @version [version, 22 Jul 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public interface ResultSetExtractable {
    /**
     * do deal result set
     * 
     * @param rs
     * @return
     * @throws SQLException [explain parameter]
     * @return Object [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public Object populate(ResultSet rs) throws SQLException;
}
