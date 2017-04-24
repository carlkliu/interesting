package com.carl.interesting.common.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.carl.interesting.common.util.ConfigHelper;

/**
 * database opertion class
 * 
 * @author Carl Liu
 * @version [version, 22 Jul 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public class DBAccess {
    private static DataSource dataSource = null;
    
    private static DBAccess uniqueInstance = null;
    static {
        String driveClassName = ConfigHelper.get("db.driverClassName");
        String url = ConfigHelper.get("db.url");
        String username = ConfigHelper.get("db.username");
        String password = ConfigHelper.get("db.password");
        String maxActive = ConfigHelper.get("db.maxActive");
        String maxIdle = ConfigHelper.get("db.maxIdle");
        String maxWait = ConfigHelper.get("db.maxWait");
        boolean removeAbandoned = (Boolean
                .valueOf(ConfigHelper.get("db.removeAbandoned")))
                        .booleanValue();
        int removeAbandonedTimeout = Integer
                .parseInt(ConfigHelper.get("db.removeAbandonedTimeout"));
        boolean logAbandoned = (Boolean
                .valueOf(ConfigHelper.get("db.logAbandoned"))).booleanValue();
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(driveClassName);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setMaxActive(Integer.parseInt(maxActive));
        ds.setMaxIdle(Integer.parseInt(maxIdle));
        ds.setMaxWait(Long.parseLong(maxWait));
        ds.setRemoveAbandoned(removeAbandoned);
        ds.setRemoveAbandonedTimeout(removeAbandonedTimeout);
        ds.setLogAbandoned(logAbandoned);
        ds.setDefaultAutoCommit(false);
        dataSource = ds;
    }
    
    public static DBAccess getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new DBAccess();
        }
        return uniqueInstance;
    }
    
    public static DBAccess getInstance(boolean autoCommit) {
        if (uniqueInstance == null) {
            uniqueInstance = new DBAccess(autoCommit);
        }
        return uniqueInstance;
    }
    
    private static ResultSet2Map rs2Map = new ResultSet2Map();
    
    private static ResultSet2Object rs2Object = new ResultSet2Object();
    
    private Connection conn = null;
    
    private boolean autoCommit = false;
    
    public DBAccess() {
    }
    
    /**
     * constructor
     * 
     * @param atuoCommit default is false
     */
    public DBAccess(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }
    
    /**
     * get database connection
     * 
     * @return Connection [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public Connection getConnection() throws SQLException {
        if (conn == null) {
            conn = dataSource.getConnection();
            conn.setAutoCommit(autoCommit);
        }
        return conn;
    }
    
    /**
     * close database connetion
     * 
     * @throws SQLException [explain parameter]
     * @see [class,class#method,class#member]
     */
    public void close() throws SQLException {
        if (conn == null)
            return;
        if (!autoCommit)
            conn.commit();
        conn.close();
        conn = null;
    }
    
    public long insert(String tableName, String[] columns, Object[] values,
            boolean autoKey) throws SQLException, InstantiationException,
            IllegalAccessException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer sql = new StringBuffer();
        long result = 0;
        try {
            StringBuffer tmp = new StringBuffer();
            int ind = 0;
            sql.append("insert into ");
            sql.append(tableName);
            sql.append(" (");
            for (int i = 0; i < columns.length; i++) {
                if (i >= values.length)
                    break;
                if (values[i] == null)
                    continue;
                if (ind > 0) {
                    sql.append(",");
                    tmp.append(",");
                }
                sql.append(columns[i]);
                tmp.append("?");
                ind++;
            }
            sql.append(") values (");
            sql.append(tmp);
            sql.append(")");
            if (autoKey) {
                pstmt = getConnection().prepareStatement(sql.toString(),
                        Statement.RETURN_GENERATED_KEYS);
            }
            else {
                pstmt = getConnection().prepareStatement(sql.toString());
            }
            ind = 0;
            for (int i = 0; i < columns.length; i++) {
                if (i >= values.length)
                    break;
                if (values[i] == null)
                    continue;
                ind++;
                if (values[i].getClass() == java.util.Date.class)
                    pstmt.setTimestamp(ind,
                            new java.sql.Timestamp(
                                    ((java.util.Date) values[i]).getTime()));
                else
                    pstmt.setObject(ind, values[i]);
            }
            pstmt.executeUpdate();
            if (autoKey) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next())
                    result = rs.getLong(1);
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }
        return result;
    }
    
    public long insert(String tableName, String commaSepColStr, Object[] values,
            boolean autoKey) throws SQLException, InstantiationException,
            IllegalAccessException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer sql = new StringBuffer();
        long result = 0;
        try {
            sql.append("insert into ");
            sql.append(tableName);
            sql.append(" (");
            sql.append(commaSepColStr);
            sql.append(") values (");
            for (int i = 0; i < values.length; i++) {
                if (i > 0)
                    sql.append(",");
                if (values[i] == null)
                    sql.append("NULL");
                else
                    sql.append("?");
            }
            sql.append(")");
            if (autoKey) {
                pstmt = getConnection().prepareStatement(sql.toString(),
                        Statement.RETURN_GENERATED_KEYS);
            }
            else {
                pstmt = getConnection().prepareStatement(sql.toString());
            }
            int idx = 1;
            for (int i = 0; i < values.length; i++) {
                if (values[i] == null)
                    continue;
                if (values[i].getClass() == java.util.Date.class)
                    pstmt.setTimestamp(idx++,
                            new java.sql.Timestamp(
                                    ((java.util.Date) values[i]).getTime()));
                else
                    pstmt.setObject(idx++, values[i]);
            }
            pstmt.executeUpdate();
            if (autoKey) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next())
                    result = rs.getLong(1);
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }
        return result;
    }
    
    public long insert(String sql, Object[] parameters, boolean autoKey)
            throws SQLException, InstantiationException,
            IllegalAccessException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long result = 0;
        try {
            if (autoKey) {
                pstmt = getConnection().prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
            }
            else {
                pstmt = getConnection().prepareStatement(sql);
            }
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    Object parameter = parameters[i];
                    if (parameter == null)
                        pstmt.setNull(i + 1, java.sql.Types.NULL);
                    else if (parameter.getClass() == java.util.Date.class)
                        pstmt.setTimestamp(i + 1,
                                new java.sql.Timestamp(
                                        ((java.util.Date) parameter)
                                                .getTime()));
                    else
                        pstmt.setObject(i + 1, parameter);
                }
            }
            pstmt.executeUpdate();
            if (autoKey) {
                rs = pstmt.getGeneratedKeys();
                if (rs != null && rs.next())
                    result = rs.getLong(1);
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }
        return result;
    }
    
    public int delete(String tableName, String colName1, Object colValue1,
            String colName2, Object colValue2) throws SQLException,
            InstantiationException, IllegalAccessException {
        PreparedStatement pstmt = null;
        StringBuffer sql = new StringBuffer(30);
        int rowsAffected = 0;
        try {
            sql.append("delete from ");
            sql.append(tableName);
            sql.append(" where ");
            sql.append(colName1);
            sql.append("=?");
            sql.append(" and ");
            sql.append(colName2);
            sql.append("=?");
            pstmt = getConnection().prepareStatement(sql.toString());
            pstmt.setObject(1, colValue1);
            pstmt.setObject(2, colValue2);
            rowsAffected = pstmt.executeUpdate();
        }
        finally {
            if (pstmt != null)
                pstmt.close();
        }
        return rowsAffected;
    }
    
    public int delete(String tableName, String colName, Object colValue)
            throws SQLException, InstantiationException,
            IllegalAccessException {
        PreparedStatement pstmt = null;
        StringBuffer sql = new StringBuffer(30);
        int rowsAffected = 0;
        try {
            sql.append("delete from ");
            sql.append(tableName);
            sql.append(" where ");
            sql.append(colName);
            sql.append("=?");
            pstmt = getConnection().prepareStatement(sql.toString());
            pstmt.setObject(1, colValue);
            rowsAffected = pstmt.executeUpdate();
        }
        finally {
            if (pstmt != null)
                pstmt.close();
        }
        return rowsAffected;
    }
    
    public int delete(String sql, Object[] parameters) throws SQLException,
            InstantiationException, IllegalAccessException {
        PreparedStatement pstmt = null;
        int rowsAffected = 0;
        try {
            pstmt = getConnection().prepareStatement(sql);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    Object parameter = parameters[i];
                    if (parameter == null)
                        pstmt.setNull(i + 1, java.sql.Types.NULL);
                    else if (parameter.getClass() == java.util.Date.class)
                        pstmt.setTimestamp(i + 1,
                                new java.sql.Timestamp(
                                        ((java.util.Date) parameter)
                                                .getTime()));
                    else
                        pstmt.setObject(i + 1, parameter);
                }
            }
            rowsAffected = pstmt.executeUpdate();
        }
        finally {
            if (pstmt != null)
                pstmt.close();
        }
        return rowsAffected;
    }
    
    public int update(String tableName, long id, String[] columns,
            Object[] values) throws SQLException, InstantiationException,
            IllegalAccessException {
        return update(tableName, "id", id, columns, values, null);
    }
    
    public int update(String tableName, long id, String[] columns,
            Object[] values, String excludeCol) throws Exception {
        return update(tableName, "id", id, columns, values, excludeCol);
    }
    
    public int update(String tableName, String keyName, Object keyValue,
            String[] columns, Object[] values, String excludeCol)
            throws SQLException, InstantiationException,
            IllegalAccessException {
        PreparedStatement pstmt = null;
        StringBuffer sql = new StringBuffer();
        int rowsAffected = 0;
        try {
            sql.append("update ");
            sql.append(tableName);
            sql.append(" set ");
            for (int i = 0, j = 0; i < columns.length; i++) {
                if (excludeCol != null && excludeCol.equals(columns[i]))
                    continue;
                if (j > 0) {
                    sql.append(",");
                }
                j++;
                sql.append(columns[i]);
                sql.append("=?");
            }
            sql.append(" where ");
            sql.append(keyName);
            sql.append("=?");
            pstmt = getConnection().prepareStatement(sql.toString());
            int i = 0;
            for (i = 0; i < columns.length; i++) {
                if (excludeCol != null && excludeCol.equals(columns[i]))
                    continue;
                if (i < values.length) {
                    Object value = values[i];
                    if (value == null)
                        pstmt.setNull(i + 1, java.sql.Types.NULL);
                    else if (value.getClass() == java.util.Date.class)
                        pstmt.setTimestamp(i + 1,
                                new java.sql.Timestamp(
                                        ((java.util.Date) value).getTime()));
                    else
                        pstmt.setObject(i + 1, value);
                }
                else {
                    pstmt.setNull(i + 1, java.sql.Types.NULL);
                }
            }
            pstmt.setObject(i + 1, keyValue);
            rowsAffected = pstmt.executeUpdate();
        }
        finally {
            if (pstmt != null)
                pstmt.close();
        }
        return rowsAffected;
    }
    
    public int update(String sql, Object[] parameters) throws SQLException,
            InstantiationException, IllegalAccessException {
        PreparedStatement pstmt = null;
        int rowsAffected = 0;
        try {
            pstmt = getConnection().prepareStatement(sql);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    Object parameter = parameters[i];
                    if (parameter == null)
                        pstmt.setNull(i + 1, java.sql.Types.NULL);
                    else if (parameter.getClass() == java.util.Date.class)
                        pstmt.setTimestamp(i + 1,
                                new java.sql.Timestamp(
                                        ((java.util.Date) parameter)
                                                .getTime()));
                    else
                        pstmt.setObject(i + 1, parameter);
                }
            }
            rowsAffected = pstmt.executeUpdate();
        }
        finally {
            if (pstmt != null)
                pstmt.close();
        }
        return rowsAffected;
    }
    
    public int update(String sql, long keyValue) throws SQLException,
            InstantiationException, IllegalAccessException {
        PreparedStatement pstmt = null;
        int rowsAffected = 0;
        try {
            pstmt = getConnection().prepareStatement(sql);
            pstmt.setLong(1, keyValue);
            rowsAffected = pstmt.executeUpdate();
        }
        finally {
            if (pstmt != null)
                pstmt.close();
        }
        return rowsAffected;
    }
    
    public int[] batch(String[] sqlArr) throws SQLException,
            InstantiationException, IllegalAccessException {
        Statement stmt = null;
        int[] rowsAffected = null;
        int row = 0;
        try {
            stmt = getConnection().createStatement();
            for (row = 0; row < sqlArr.length; row++)
                stmt.addBatch(sqlArr[row]);
            rowsAffected = stmt.executeBatch();
        }
        finally {
            if (stmt != null)
                stmt.close();
        }
        return rowsAffected;
    }
    
    public int[] batch(String sql, Object[][] parametersArr)
            throws SQLException, InstantiationException,
            IllegalAccessException {
        if (parametersArr == null || parametersArr.length == 0)
            return new int[0];
        PreparedStatement pstmt = null;
        int[] rowsAffected = null;
        int row = 0;
        try {
            pstmt = getConnection().prepareStatement(sql);
            for (row = 0; row < parametersArr.length; row++) {
                Object[] parameters = parametersArr[row];
                for (int i = 0; i < parameters.length; i++) {
                    Object parameter = parameters[i];
                    if (parameter == null)
                        pstmt.setNull(i + 1, java.sql.Types.NULL);
                    else if (parameter.getClass() == java.util.Date.class)
                        pstmt.setTimestamp(i + 1,
                                new java.sql.Timestamp(
                                        ((java.util.Date) parameter)
                                                .getTime()));
                    else
                        pstmt.setObject(i + 1, parameter);
                }
                pstmt.addBatch();
            }
            rowsAffected = pstmt.executeBatch();
        }
        finally {
            if (pstmt != null)
                pstmt.close();
        }
        return rowsAffected;
    }
    
    public int[] batch(String sql, ArrayList<Object[]> parametersList,
            Object[] sameParameters) throws SQLException,
            InstantiationException, IllegalAccessException {
        if (parametersList == null || parametersList.isEmpty())
            return new int[0];
        PreparedStatement pstmt = null;
        int[] rowsAffected = null;
        try {
            pstmt = getConnection().prepareStatement(sql);
            Iterator<Object[]> iter = parametersList.iterator();
            while (iter.hasNext()) {
                Object[] parameters = (Object[]) iter.next();
                int len = parameters.length
                        + (sameParameters == null ? 0 : sameParameters.length);
                for (int i = 0; i < len; i++) {
                    Object parameter = null;
                    if (i < parameters.length)
                        parameter = parameters[i];
                    else
                        parameter = sameParameters[i - parameters.length];
                    if (parameter == null)
                        pstmt.setNull(i + 1, java.sql.Types.NULL);
                    else if (parameter.getClass() == java.util.Date.class)
                        pstmt.setTimestamp(i + 1,
                                new java.sql.Timestamp(
                                        ((java.util.Date) parameter)
                                                .getTime()));
                    else
                        pstmt.setObject(i + 1, parameter);
                }
                pstmt.addBatch();
            }
            rowsAffected = pstmt.executeBatch();
        }
        finally {
            if (pstmt != null)
                pstmt.close();
        }
        return rowsAffected;
    }
    
    public Object[] call(String procedureName, Object[] parameters)
            throws SQLException, InstantiationException,
            IllegalAccessException {
        CallableStatement cstmt = null;
        StringBuffer sql = new StringBuffer();
        try {
            sql.append("{call ");
            sql.append(procedureName);
            sql.append("(");
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    if (i != 0)
                        sql.append(",");
                    sql.append("?");
                }
            }
            sql.append(")}");
            cstmt = getConnection().prepareCall(sql.toString());
            int ind = 0;
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    Object parameter = parameters[i];
                    if (parameter == null) {
                        ind = i + 1;
                        cstmt.registerOutParameter(ind, java.sql.Types.NUMERIC);
                        cstmt.setInt(ind, 0);
                    }
                    else {
                        if (parameter.getClass() == java.util.Date.class)
                            cstmt.setTimestamp(i + 1,
                                    new java.sql.Timestamp(
                                            ((java.util.Date) parameter)
                                                    .getTime()));
                        else
                            cstmt.setObject(i + 1, parameter);
                    }
                }
            }
            cstmt.execute();
            for (int i = 0; i < ind; i++) {
                if (parameters[i] == null)
                    parameters[i] = cstmt.getLong(i + 1);
            }
            return parameters;
        }
        finally {
            if (cstmt != null)
                cstmt.close();
        }
    }
    
    public ArrayList<Object> call(String procedureName, Object[] parameters,
            Class<?> extractorClass) throws SQLException,
            InstantiationException, IllegalAccessException {
        ArrayList<Object> records = new ArrayList<Object>();
        CallableStatement cstmt = null;
        ResultSet rs = null;
        StringBuffer sql = new StringBuffer();
        try {
            sql.append("{call ");
            sql.append(procedureName);
            sql.append("(");
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    if (i != 0)
                        sql.append(",");
                    sql.append("?");
                }
            }
            sql.append(")}");
            cstmt = getConnection().prepareCall(sql.toString());
            int ind = 0;
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    Object parameter = parameters[i];
                    if (parameter == null) {
                        ind = i + 1;
                        cstmt.registerOutParameter(ind, java.sql.Types.NUMERIC);
                        cstmt.setInt(ind, 0);
                    }
                    else {
                        if (parameter.getClass() == java.util.Date.class)
                            cstmt.setTimestamp(i + 1,
                                    new java.sql.Timestamp(
                                            ((java.util.Date) parameter)
                                                    .getTime()));
                        else
                            cstmt.setObject(i + 1, parameter);
                    }
                }
            }
            cstmt.execute();
            for (int i = 0; i < ind; i++) {
                if (parameters[i] == null)
                    parameters[i] = cstmt.getLong(i + 1);
            }
            rs = cstmt.getResultSet();
            ResultSetExtractable extractor = null;
            Object obj = null;
            while (rs.next()) {
                if (extractorClass == ResultSet2Object.class)
                    obj = rs2Object.populate(rs);
                else if (extractorClass == ResultSet2Map.class)
                    obj = rs2Map.populate(rs);
                else {
                    if (extractor == null || obj == extractor)
                        extractor = (ResultSetExtractable) extractorClass
                                .newInstance();
                    obj = extractor.populate(rs);
                }
                records.add(obj);
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (cstmt != null)
                cstmt.close();
        }
        return records;
    }
    
    public Object find(String tableName, long id, String[] columns,
            Class<?> extractorClass) throws SQLException,
            InstantiationException, IllegalAccessException {
        return find(tableName, "id", Long.valueOf(id), columns, extractorClass);
    }
    
    public Object find(String tableName, String keyName, Object keyValue,
            String[] columns, Class<?> extractorClass) throws SQLException,
            InstantiationException, IllegalAccessException {
        Object result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer sql = new StringBuffer();
        try {
            sql.append("select ");
            for (int i = 0; i < columns.length; i++) {
                if (i != 0)
                    sql.append(",");
                sql.append(columns[i]);
            }
            sql.append(" from ");
            sql.append(tableName);
            sql.append(" where ");
            sql.append(keyName);
            sql.append("=?");
            pstmt = getConnection().prepareStatement(sql.toString());
            pstmt.setObject(1, keyValue);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (extractorClass == ResultSet2Object.class) {
                    if (columns.length == 1)
                        result = rs.getObject(1);
                    else
                        result = rs2Object.populate(rs);
                }
                else if (extractorClass == ResultSet2Map.class)
                    result = rs2Map.populate(rs);
                else {
                    ResultSetExtractable extractor = (ResultSetExtractable) extractorClass
                            .newInstance();
                    result = extractor.populate(rs);
                }
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }
        return result;
    }
    
    public Object find(String tableName, long id, String commaSepColStr,
            Class<?> extractorClass) throws SQLException,
            InstantiationException, IllegalAccessException {
        return find(tableName,
                "id",
                Long.valueOf(id),
                commaSepColStr,
                extractorClass);
    }
    
    public Object find(String tableName, String keyName, Object keyValue,
            String commaSepColStr, Class<?> extractorClass) throws SQLException,
            InstantiationException, IllegalAccessException {
        Object result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer sql = new StringBuffer();
        try {
            sql.append("select ");
            sql.append(commaSepColStr);
            sql.append(" from ");
            sql.append(tableName);
            sql.append(" where ");
            sql.append(keyName);
            sql.append("=?");
            pstmt = getConnection().prepareStatement(sql.toString());
            pstmt.setObject(1, keyValue);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (extractorClass == ResultSet2Object.class) {
                    result = rs2Object.populate(rs);
                }
                else if (extractorClass == ResultSet2Map.class)
                    result = rs2Map.populate(rs);
                else {
                    ResultSetExtractable extractor = (ResultSetExtractable) extractorClass
                            .newInstance();
                    result = extractor.populate(rs);
                }
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }
        return result;
    }
    
    public Object find(String sql, Object[] parameters, Class<?> extractorClass)
            throws SQLException, InstantiationException,
            IllegalAccessException {
        Object result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = getConnection().prepareStatement(sql);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    Object parameter = parameters[i];
                    if (parameter == null)
                        pstmt.setNull(i + 1, java.sql.Types.NULL);
                    else if (parameter.getClass() == java.util.Date.class)
                        pstmt.setTimestamp(i + 1,
                                new java.sql.Timestamp(
                                        ((java.util.Date) parameter)
                                                .getTime()));
                    else
                        pstmt.setObject(i + 1, parameter);
                }
            }
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (extractorClass == ResultSet2Object.class)
                    result = rs2Object.populate(rs);
                else if (extractorClass == ResultSet2Map.class)
                    result = rs2Map.populate(rs);
                else {
                    ResultSetExtractable extractor = (ResultSetExtractable) extractorClass
                            .newInstance();
                    result = extractor.populate(rs);
                }
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }
        return result;
    }
    
    public Object find(String sql, long keyValue, Class<?> extractorClass)
            throws SQLException, InstantiationException,
            IllegalAccessException {
        Object result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = getConnection().prepareStatement(sql);
            pstmt.setLong(1, keyValue);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                if (extractorClass == ResultSet2Object.class)
                    result = rs2Object.populate(rs);
                else if (extractorClass == ResultSet2Map.class)
                    result = rs2Map.populate(rs);
                else {
                    ResultSetExtractable extractor = (ResultSetExtractable) extractorClass
                            .newInstance();
                    result = extractor.populate(rs);
                }
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }
        return result;
    }
    
    public ArrayList<Object> query(String sql, Object[] parameters,
            Class<?> extractorClass, int offset, int limit) throws SQLException,
            InstantiationException, IllegalAccessException {
        if (limit < 0)
            return query(sql + " offset " + offset, parameters, extractorClass);
        return query(sql + " limit " + limit + " offset " + offset,
                parameters,
                extractorClass);
    }
    
    public ArrayList<Object> query(String sql, Object[] parameters,
            Class<?> extractorClass) throws SQLException,
            InstantiationException, IllegalAccessException {
        ArrayList<Object> records = new ArrayList<Object>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = getConnection().prepareStatement(sql);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    Object parameter = parameters[i];
                    if (parameter == null)
                        pstmt.setNull(i + 1, java.sql.Types.NULL);
                    else if (parameter.getClass() == java.util.Date.class)
                        pstmt.setTimestamp(i + 1,
                                new java.sql.Timestamp(
                                        ((java.util.Date) parameter)
                                                .getTime()));
                    else
                        pstmt.setObject(i + 1, parameter);
                }
            }
            rs = pstmt.executeQuery();
            ResultSetExtractable extractor = null;
            Object obj = null;
            while (rs.next()) {
                if (extractorClass == ResultSet2Object.class)
                    obj = rs2Object.populate(rs);
                // rs.getObject(n, Object.class);
                else if (extractorClass == ResultSet2Map.class)
                    obj = rs2Map.populate(rs);
                else {
                    if (extractor == null || obj == extractor) {
                        extractor = (ResultSetExtractable) extractorClass
                                .newInstance();
                    }
                    obj = extractor.populate(rs);
                }
                records.add(obj);
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }
        return records;
    }
    
    public ArrayList<Object> query(String sql, long fkeyValue,
            Class<?> extractorClass, int offset, int limit) throws SQLException,
            InstantiationException, IllegalAccessException {
        if (limit < 0)
            return query(sql + " offset " + offset, fkeyValue, extractorClass);
        return query(sql + " limit " + limit + " offset " + offset,
                fkeyValue,
                extractorClass);
    }
    
    public ArrayList<Object> query(String sql, long fkeyValue,
            Class<?> extractorClass) throws SQLException,
            InstantiationException, IllegalAccessException {
        ArrayList<Object> records = new ArrayList<Object>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = getConnection().prepareStatement(sql);
            pstmt.setLong(1, fkeyValue);
            rs = pstmt.executeQuery();
            ResultSetExtractable extractor = null;
            Object obj = null;
            while (rs.next()) {
                if (extractorClass == ResultSet2Object.class)
                    obj = rs2Object.populate(rs);
                else if (extractorClass == ResultSet2Map.class)
                    obj = rs2Map.populate(rs);
                else {
                    if (extractor == null || obj == extractor)
                        extractor = (ResultSetExtractable) extractorClass
                                .newInstance();
                    obj = extractor.populate(rs);
                }
                records.add(obj);
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }
        return records;
    }
    
    public ArrayList<Object> query(String tableName, String commaSepColStr,
            List<Long> idList, Class<?> extractorClass) throws SQLException,
            InstantiationException, IllegalAccessException {
        ArrayList<Object> records = new ArrayList<Object>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer sql = new StringBuffer();
        try {
            sql.append("select ");
            sql.append(commaSepColStr);
            sql.append(" from ");
            sql.append(tableName);
            if (idList != null && !idList.isEmpty()) {
                sql.append(" where ");
                Iterator<Long> iter = idList.iterator();
                int i = 0;
                while (iter.hasNext()) {
                    if (i > 0)
                        sql.append(" or id=");
                    else
                        sql.append("id=");
                    sql.append(iter.next());
                    i++;
                }
            }
            pstmt = getConnection().prepareStatement(sql.toString());
            rs = pstmt.executeQuery();
            ResultSetExtractable extractor = null;
            Object obj = null;
            while (rs.next()) {
                if (extractorClass == ResultSet2Object.class)
                    obj = rs2Object.populate(rs);
                else if (extractorClass == ResultSet2Map.class)
                    obj = rs2Map.populate(rs);
                else {
                    if (extractor == null || obj == extractor)
                        extractor = (ResultSetExtractable) extractorClass
                                .newInstance();
                    obj = extractor.populate(rs);
                }
                records.add(obj);
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }
        return records;
    }
    
    public List<Object> query(String tableName, String commaSepColStr,
            String idName, List<Long> idList, Class<?> extractorClass)
            throws SQLException, InstantiationException,
            IllegalAccessException {
        ArrayList<Object> records = new ArrayList<Object>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer sql = new StringBuffer();
        try {
            sql.append("select ");
            sql.append(commaSepColStr);
            sql.append(" from ");
            sql.append(tableName);
            if (idList != null && !idList.isEmpty()) {
                sql.append(" where ");
                Iterator<Long> iter = idList.iterator();
                int i = 0;
                while (iter.hasNext()) {
                    if (i > 0)
                        sql.append(" or " + idName + "=");
                    else
                        sql.append(idName + "=");
                    sql.append(iter.next());
                    i++;
                }
            }
            pstmt = getConnection().prepareStatement(sql.toString());
            rs = pstmt.executeQuery();
            ResultSetExtractable extractor = null;
            Object obj = null;
            while (rs.next()) {
                if (extractorClass == ResultSet2Object.class)
                    obj = rs2Object.populate(rs);
                else if (extractorClass == ResultSet2Map.class)
                    obj = rs2Map.populate(rs);
                else {
                    if (extractor == null || obj == extractor)
                        extractor = (ResultSetExtractable) extractorClass
                                .newInstance();
                    obj = extractor.populate(rs);
                }
                records.add(obj);
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }
        return records;
    }
    
    public ArrayList<Object> query(String tableName, String commaSepColStr,
            String colName, Object[] colValues, Class<?> extractorClass)
            throws SQLException, InstantiationException,
            IllegalAccessException {
        ArrayList<Object> records = new ArrayList<Object>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer sql = new StringBuffer();
        try {
            sql.append("select ");
            sql.append(commaSepColStr);
            sql.append(" from ");
            sql.append(tableName);
            if (colValues != null && colValues.length > 0) {
                sql.append(" where ");
                for (int i = 0; i < colValues.length; i++) {
                    if (i > 0)
                        sql.append(" or ");
                    sql.append(colName);
                    sql.append("=?");
                }
            }
            pstmt = getConnection().prepareStatement(sql.toString());
            for (int i = 0; i < colValues.length; i++) {
                Object parameter = colValues[i];
                if (parameter == null)
                    pstmt.setNull(i + 1, java.sql.Types.NULL);
                else if (parameter.getClass() == java.util.Date.class)
                    pstmt.setTimestamp(i + 1,
                            new java.sql.Timestamp(
                                    ((java.util.Date) parameter).getTime()));
                else
                    pstmt.setObject(i + 1, parameter);
            }
            rs = pstmt.executeQuery();
            ResultSetExtractable extractor = null;
            Object obj = null;
            while (rs.next()) {
                if (extractorClass == ResultSet2Object.class)
                    obj = rs2Object.populate(rs);
                else if (extractorClass == ResultSet2Map.class)
                    obj = rs2Map.populate(rs);
                else {
                    if (extractor == null || obj == extractor)
                        extractor = (ResultSetExtractable) extractorClass
                                .newInstance();
                    obj = extractor.populate(rs);
                }
                records.add(obj);
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }
        return records;
    }
    
    public int getRecordNumber(String sql) throws SQLException,
            InstantiationException, IllegalAccessException {
        return getRecordNumber(sql, null);
    }
    
    public int getRecordNumberByName(String tableName) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select count(*) from ");
        sql.append(tableName);
        return getRecordNumber(sql.toString(), null);
    }
    
    public int getRecordNumber(String sql, Object[] parameters)
            throws SQLException, InstantiationException,
            IllegalAccessException {
        int result = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = getConnection().prepareStatement(sql);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    Object parameter = parameters[i];
                    if (parameter == null)
                        pstmt.setNull(i + 1, java.sql.Types.NULL);
                    else if (parameter.getClass() == java.util.Date.class)
                        pstmt.setTimestamp(i + 1,
                                new java.sql.Timestamp(
                                        ((java.util.Date) parameter)
                                                .getTime()));
                    else
                        pstmt.setObject(i + 1, parameter);
                }
            }
            rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }
        return result;
    }
    
    public HashMap<Object, List<Object>> query2Map(String sql,
            Object[] parameters, int keyIdx, Class<?> extractorClass,
            int offset, int limit) throws SQLException, InstantiationException,
            IllegalAccessException {
        if (limit < 0)
            return query2Map(sql + " offset " + offset,
                    parameters,
                    keyIdx,
                    extractorClass);
        return query2Map(sql + " limit " + limit + " offset " + offset,
                parameters,
                keyIdx,
                extractorClass);
    }
    
    public HashMap<Object, List<Object>> query2Map(String sql,
            Object[] parameters, int keyIdx, Class<?> extractorClass)
            throws SQLException, InstantiationException,
            IllegalAccessException {
        HashMap<Object, List<Object>> keyRecord = new HashMap<Object, List<Object>>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = getConnection().prepareStatement(sql);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    Object parameter = parameters[i];
                    if (parameter == null)
                        pstmt.setNull(i + 1, java.sql.Types.NULL);
                    else if (parameter.getClass() == java.util.Date.class)
                        pstmt.setTimestamp(i + 1,
                                new java.sql.Timestamp(
                                        ((java.util.Date) parameter)
                                                .getTime()));
                    else
                        pstmt.setObject(i + 1, parameter);
                }
            }
            rs = pstmt.executeQuery();
            ResultSetExtractable extractor = null;
            Object obj = null;
            while (rs.next()) {
                if (extractorClass == ResultSet2Object.class)
                    obj = rs2Object.populate(rs);
                else if (extractorClass == ResultSet2Map.class)
                    obj = rs2Map.populate(rs);
                else {
                    if (extractor == null || obj == extractor)
                        extractor = (ResultSetExtractable) extractorClass
                                .newInstance();
                    obj = extractor.populate(rs);
                }
                Object key = rs.getObject(keyIdx);
                List<Object> list = (List<Object>) keyRecord.get(key);
                if (list != null) {
                    list.add(obj);
                }
                else {
                    list = new ArrayList<Object>();
                    list.add(obj);
                }
                keyRecord.put(key, list);
            }
        }
        finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
        }
        return keyRecord;
    }
    
    public static String formatMsg(String[] arrName, Object[] arrMsg) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < arrName.length; i++) {
            if (i != 0)
                buf.append("; ");
            buf.append(arrName[i]);
            buf.append(": ");
            if (arrMsg[i] == null) {
                buf.append("NULL");
            }
            else if (arrMsg[i] instanceof Object[]) {
                Object[] theMsg = (Object[]) arrMsg[i];
                for (int j = 0; j < theMsg.length; j++) {
                    if (j > 0)
                        buf.append(",");
                    if (theMsg[j] instanceof Object[]) {
                        Object[] theMsg0 = (Object[]) theMsg[j];
                        buf.append("[");
                        for (int k = 0; k < theMsg0.length; k++) {
                            if (k > 0)
                                buf.append(",");
                            buf.append(theMsg0[k]);
                        }
                        buf.append("]");
                    }
                    else
                        buf.append(theMsg[j]);
                }
            }
            else {
                buf.append(arrMsg[i].toString());
            }
        }
        return buf.toString();
    }
}