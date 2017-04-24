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
 * @author Tianbao Liu
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
    
    /**
     * 该方法用于插入新的记录
     * 
     * @param tableName 表名
     * @param columns 列名以一个String[]传入
     * @param values 参数以Object[]传入. 参数传入前需用Integer.valueOf(int)将int转换为Integer，
     * 用new Timestamp(calendar.getTimeInMillis())将Calendar转换为Timestamp，
     * Date将会自动转换为Timestamp
     * @param autoKey 是否获取自动生成的键
     * @return 自动生成的键
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法用于插入新的记录
     * 
     * @param tableName 表名
     * @param columns 列名以一个String传入
     * @param values 参数以Object[]传入. 参数传入前需用Integer.valueOf(int)将int转换为Integer，
     * 用new Timestamp(calendar.getTimeInMillis())将Calendar转换为Timestamp，
     * Date将会自动转换为Timestamp
     * @param autoKey 是否获取自动生成的键
     * @return 自动生成的键
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法用于插入新的记录
     * 
     * @param sql 用户插入的插入语句
     * @param parameters 以Object[]方式插入数据库的值
     * @param autoKey 是否获取自动生成的键
     * @return 自动生成的键
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法用与条件删除数据表中的记录
     * 
     * @param tableName 数据表名
     * @param colName1 条件一，列名
     * @param colValue1 条件一，值
     * @param colName2 条件二，列名
     * @param colValue2 条件二，值
     * @return 被删除的记录个数
     * @throws Exception
     */
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
    
    /**
     * 该方法用单个条件删除数据表中的记录
     * 
     * @param tableName 数据表名
     * @param colName 列名
     * @param colValue 值
     * @return 被删除的记录个数
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法提供用户自定义删除操作
     * 
     * @param sql 用户自定义的删除语句
     * @param parameters 语句中各条件的值
     * @return 被删除的记录个数。
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法根据id更新记录的数据
     * 
     * @param tableName 表名
     * @param id 主键值
     * @param columns 列名以一个Object[]传入
     * @param values 参数以Object[]传入. 参数传入前需用Integer.valueOf(int)将int转换为Integer，
     * 用new Timestamp(calendar.getTimeInMillis())将Calendar转换为Timestamp，
     * Date将会自动转换为Timestamp
     * @throws Exception 数据库操作例外
     */
    public int update(String tableName, long id, String[] columns,
            Object[] values) throws SQLException, InstantiationException,
            IllegalAccessException {
        return update(tableName, "id", id, columns, values, null);
    }
    
    /**
     * 该方法根据id更新记录的数据
     * 
     * @param tableName 数据表名
     * @param id 主键值
     * @param columns 用于更新的列名
     * @param values 用于更新的列名值
     * @param excludeCol 用于忽略的列名，没有则为null
     * @return 被更新的记录数
     * @throws Exception 数据库操作例外
     */
    public int update(String tableName, long id, String[] columns,
            Object[] values, String excludeCol) throws Exception {
        return update(tableName, "id", id, columns, values, excludeCol);
    }
    
    /**
     * 该方法根据单个条件更新记录的数据
     * 
     * @param tableName 数据表名
     * @param keyName 用于检索的列名
     * @param keyValue 用于检索的列名值
     * @param columns 用于更新的列名
     * @param values 用于更新的列名值
     * @param excludeCol 用于忽略的列名，没有则为null
     * @return 被更新的记录数
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法提供用户自定义的更新操作
     * 
     * @param sql 增删改语句，where子句中用IS Null判断NULL 或使用安全比较<=>
     * @param parameters 参数以Object[]传入.
     * 参数传入前需用Integer.valueOf(int)将int转换为Integer， 用new
     * Timestamp(calendar.getTimeInMillis())将Calendar转换为Timestamp，
     * Date将会自动转换为Timestamp
     * @return 被更新的记录个数
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法提供用户自定义更新操作
     * 
     * @param sql 增删改语句，where子句中用IS Null判断NULL 或使用安全比较<=>
     * @param keyValue 用于更新的值
     * @return 被更新的记录个数
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法用于将一系列sql提交给数据库执行。
     * 
     * @param sqlArr 要执行的sql语句以String[]传入
     * @return 每条语句的受影响行数
     * 未知行数返回Statement.SUCCESS_NO_INFO，执行失败返回Statement.EXECUTE_FAILED
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法用于将一系列sql语句提交给数据库执行，并提供相应的参数。
     * 
     * @param sql 一条带参数的sql语句
     * @param parameters 不同的参数组，以Object[][]传入
     * @return 不同参数的受影响行数
     * 未知行数返回Statement.SUCCESS_NO_INFO，执行失败返回Statement.EXECUTE_FAILED
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法执行一系列相同的sql语句，这些语句具有相同的sameParameters参数，不同的参数为parametersList
     * 
     * @param sql 用户自定义的sql语句
     * @param parametersList 各个sql语句不通的参数集合
     * @param sameParameters 各个sql语句相同的参数集合
     * @return 数据库受影响的行数
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法执行数据库存储过程
     * 
     * @param procedureName 存储过程名
     * @param parameters 参数以Object[]传入.
     * 参数传入前需用Integer.valueOf(int)将int转换为Integer， 用new
     * Timestamp(calendar.getTimeInMillis())将Calendar转换为Timestamp，
     * Date将会自动转换为Timestamp 如果存储过程的第i个参数是OUT，则parameters[i]为null，将用于保存返回值
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 调用存储过程并返回结果集
     * 
     * @param procedureName 存储过程名
     * @param parameters 参数以Object[]传入.
     * 参数传入前需用Integer.valueOf(int)将int转换为Integer， 用new
     * Timestamp(calendar.getTimeInMillis())将Calendar转换为Timestamp，
     * Date将会自动转换为Timestamp 如果存储过程的第i个参数是OUT，则parameters[i]为null，将用于保存返回值
     * @return Object 经过extractorClass处理后的结果集
     * @throws Exception 数据库操作例外
     */
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
                    // 如果extractor.populate(rs)返回this，则每一个rs都创建新的extractor实例来转储；
                    // 如果extractor.populate(rs)返回新的对象，则总是用一个extractor为每一个rs作转储
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
    
    /**
     * 该方法提供根据id查找相应的记录
     * 
     * @param tableName 表名
     * @param id 主键值
     * @param columns 表的列名数组,不能为null
     * @param extractorClass 实现了ResultSetExtractable接口的数据提取操作类，
     * 用于将ResultSet转储到自身（其populate方法返回this ），或转储到其他类型的对象（如ResultSet2Array）
     * @return 数据存在则返回Object，否则返回null
     * @throws Exception 数据库操作例外
     */
    public Object find(String tableName, long id, String[] columns,
            Class<?> extractorClass) throws SQLException,
            InstantiationException, IllegalAccessException {
        return find(tableName, "id", Long.valueOf(id), columns, extractorClass);
    }
    
    /**
     * 该方法提供根据一个列名查找相应的记录
     * 
     * @param tableName 表名
     * @param keyName 列名
     * @param keyValue 列名值
     * @param columns 要查找的列
     * @param extractorClass 实现了ResultSetExtractable接口的数据提取操作类，
     * 用于将ResultSet转储到自身（其populate方法返回this ），或转储到其他类型的对象（如ResultSet2Array）
     * @return 返回结果对象，不存在返回null
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法提供根据id查找相应的记录
     * 
     * @param tableName 表名
     * @param id 主键值
     * @param commaSepColStr 表的列名数组,不能为null
     * @param extractorClass 实现了ResultSetExtractable接口的数据提取操作类，
     * 用于将ResultSet转储到自身（其populate方法返回this ），或转储到其他类型的对象（如ResultSet2Array）
     * @return 数据存在则返回Object，否则返回null
     * @throws Exception 数据库操作例外
     */
    public Object find(String tableName, long id, String commaSepColStr,
            Class<?> extractorClass) throws SQLException,
            InstantiationException, IllegalAccessException {
        return find(tableName,
                "id",
                Long.valueOf(id),
                commaSepColStr,
                extractorClass);
    }
    
    /**
     * 该方法提供根据一个列名查找相应的记录
     * 
     * @param tableName 表名
     * @param keyName 列名
     * @param keyValue 列名值
     * @param commaSepColStr 要查找的列
     * @param extractorClass 实现了ResultSetExtractable接口的数据提取操作类，
     * 用于将ResultSet转储到自身（其populate方法返回this ），或转储到其他类型的对象（如ResultSet2Array）
     * @return 返回结果对象，不存在返回null
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法提供用户自定义查询sql语句，多参数
     * 
     * @param sql 查询语句
     * @param parameters 参数以Object[]传入，每个参数都不能为null.
     * 参数传入前需用Integer.valueOf(int)将int转换为Integer， 用new Timestamp(calendar
     * .getTimeInMillis())将Calendar转换为Timestamp，Date将会自动转换为Timestamp
     * @param extractorClass 实现了ResultSetExtractable接口的数据提取操作类，
     * 用于将ResultSet转储到自身（其populate方法返回this ），或转储到其他类型的对象（如ResultSet2Array）
     * @return 返回结果用Object保存
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法提供用户自定义查询sql语句，单个参数
     * 
     * @param sql 查询语句
     * @param keyValue 参数以long类型传入， 用new
     * Timestamp(calendar.getTimeInMillis())将Calendar转换为Timestamp
     * ，Date将会自动转换为Timestamp
     * @param extractorClass 实现了ResultSetExtractable接口的数据提取操作类，
     * 用于将ResultSet转储到自身（其populate方法返回this ），或转储到其他类型的对象（如ResultSet2Array）
     * @return 返回结果用Object保存
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法提供具有便宜值和上界的自定义查询语句，并需要提供相应的参数。
     * 
     * @param sql 查询语句
     * @param parameters 参数以Object[]传入，每个参数都不能为null.
     * 参数传入前需用Integer.valueOf(int)将int转换为Integer， 用new Timestamp(calendar
     * .getTimeInMillis())将Calendar转换为Timestamp，Date将会自动转换为Timestamp
     * @param extractorClass 实现了ResultSetExtractable接口的数据提取操作类，
     * 用于将ResultSet转储到自身（其populate方法返回this ），或转储到其他类型的对象（如ResultSet2Array）
     * @param offset 数据查询的偏移值
     * @param limit 数据查询的上界值。
     * @return 返回查询结果,ArrayList中每条记录用Object保存
     * @throws Exception 数据库操作例外
     */
    public ArrayList<Object> query(String sql, Object[] parameters,
            Class<?> extractorClass, int offset, int limit) throws SQLException,
            InstantiationException, IllegalAccessException {
        if (limit < 0)
            return query(sql + " offset " + offset, parameters, extractorClass);
        return query(sql + " limit " + limit + " offset " + offset,
                parameters,
                extractorClass);
    }
    
    /**
     * 该方法提供自定义查询语句，并需要提供相应的参数
     * 
     * @param sql 查询语句
     * @param parameters 参数以Object[]传入，每个参数都不能为null.
     * 参数传入前需用Integer.valueOf(int)将int转换为Integer， 用new Timestamp(calendar
     * .getTimeInMillis())将Calendar转换为Timestamp，Date将会自动转换为Timestamp
     * @param extractorClass 实现了ResultSetExtractable接口的数据提取操作类，
     * 用于将ResultSet转储到自身（其populate方法返回this ），或转储到其他类型的对象（如ResultSet2Array）
     * @return 返回查询结果,ArrayList中每条记录用Object保存
     * @throws Exception 数据库操作例外
     */
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
                    // 如果extractor.populate(rs)返回this，则每一个rs都创建新的extractor实例来转储；
                    // 如果extractor.populate(rs)返回新的对象，则总是用一个extractor为每一个rs作转储
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
    
    /**
     * 该方法提供具有便宜值和上界的自定义查询语句，并需要提供单一参数。
     * 
     * @param sql 查询语句
     * @param fkeyValue 参数以long传入， 用new
     * Timestamp(calendar.getTimeInMillis())将Calendar转换为Timestamp
     * ，Date将会自动转换为Timestamp
     * @param extractorClass 实现了ResultSetExtractable接口的数据提取操作类，
     * 用于将ResultSet转储到自身（其populate方法返回this ），或转储到其他类型的对象（如ResultSet2Array）
     * @param offset 数据查询的偏移值
     * @param limit 数据查询的上界值。
     * @return 返回查询结果,ArrayList中每条记录用Object保存
     * @throws Exception 数据库操作例外
     */
    public ArrayList<Object> query(String sql, long fkeyValue,
            Class<?> extractorClass, int offset, int limit) throws SQLException,
            InstantiationException, IllegalAccessException {
        if (limit < 0)
            return query(sql + " offset " + offset, fkeyValue, extractorClass);
        return query(sql + " limit " + limit + " offset " + offset,
                fkeyValue,
                extractorClass);
    }
    
    /**
     * 该方法提供自定义查询语句，并需要提供单一参数
     * 
     * @param sql 查询语句
     * @param fkeyValue 参数以long传入 用new
     * Timestamp(calendar.getTimeInMillis())将Calendar转换为Timestamp
     * ，Date将会自动转换为Timestamp
     * @param extractorClass 实现了ResultSetExtractable接口的数据提取操作类，
     * 用于将ResultSet转储到自身（其populate方法返回this ），或转储到其他类型的对象（如ResultSet2Array）
     * @return 返回查询结果,ArrayList中每条记录用Object保存
     * @throws Exception 数据库操作例外
     */
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
                    // 如果extractor.populate(rs)返回this，则每一个rs都创建新的extractor实例来转储；
                    // 如果extractor.populate(rs)返回新的对象，则总是用一个extractor为每一个rs作转储
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
    
    /**
     * 该方法通过id的list结构查找数据记录。
     * 
     * @param tableName 数据表名
     * @param commaSepColStr 查找的列名
     * @param idList 提供的id值
     * @param extractorClass 实现了ResultSetExtractable接口的数据提取操作类，
     * 用于将ResultSet转储到自身（其populate方法返回this ），或转储到其他类型的对象（如ResultSet2Array）
     * @return 返回查询结果,ArrayList中每条记录用Object保存
     * @throws Exception 数据库操作例外
     */
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
                    // 如果extractor.populate(rs)返回this，则每一个rs都创建新的extractor实例来转储；
                    // 如果extractor.populate(rs)返回新的对象，则总是用一个extractor为每一个rs作转储
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
    
    /**
     * 该方法通过自定义id名的查找数据记录。
     * 
     * @param tableName 数据表名
     * @param commaSepColStr 查找的列名
     * @param idName id的名字
     * @param idList 提供的id值
     * @param extractorClass 实现了ResultSetExtractable接口的数据提取操作类，
     * 用于将ResultSet转储到自身（其populate方法返回this ），或转储到其他类型的对象（如ResultSet2Array）
     * @return 返回查询结果,ArrayList中每条记录用Object保存
     * @throws Exception 数据库操作例外
     */
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
                    // 如果extractor.populate(rs)返回this，则每一个rs都创建新的extractor实例来转储；
                    // 如果extractor.populate(rs)返回新的对象，则总是用一个extractor为每一个rs作转储
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
    
    /**
     * 该方法提供多个或条件进行查询
     * 
     * @param tableName 表名
     * @param commaSepColStr 查询的列名
     * @param colName 或条件列名
     * @param colValues 或条件列名值数组
     * @param extractorClass 实现了ResultSetExtractable接口的数据提取操作类，
     * 用于将ResultSet转储到自身（其populate方法返回this ），或转储到其他类型的对象（如ResultSet2Array）
     * @return 返回查询结果,ArrayList中每条记录用Object保存
     * @throws Exception 数据库操作例外
     */
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
                    // 如果extractor.populate(rs)返回this，则每一个rs都创建新的extractor实例来转储；
                    // 如果extractor.populate(rs)返回新的对象，则总是用一个extractor为每一个rs作转储
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
    
    /**
     * 数据库表中的总记录数
     * 
     * @param sql 要执行的sql语句
     * @return 表中的总记录数
     * @throws Exception [参数说明]
     */
    public int getRecordNumber(String sql) throws SQLException,
            InstantiationException, IllegalAccessException {
        return getRecordNumber(sql, null);
    }
    
    /**
     * 根据表名查询表中记录的总数
     * 
     * @param tableName 表名
     * @return 表中的总记录数
     * @throws Exception [参数说明]
     * @return int [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public int getRecordNumberByName(String tableName) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("select count(*) from ");
        sql.append(tableName);
        return getRecordNumber(sql.toString(), null);
    }
    
    /**
     * 数据库表中的总记录数
     * 
     * @param sql 要执行的sql语句
     * @param parameters 参数以Object[]传入，每个参数都不能为null.
     * 参数传入前需用Integer.valueOf(int)将int转换为Integer， 用new Timestamp(calendar
     * .getTimeInMillis())将Calendar转换为Timestamp，Date将会自动转换为Timestamp
     * @return 表中的总记录数
     * @throws Exception [参数说明]
     */
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
    
    /**
     * 该方法提供自定义查询特定值的数据
     * 
     * @param sql 查询语句
     * @param parameters 参数以Object[]传入，每个参数都不能为null.
     * 参数传入前需用Integer.valueOf(int)将int转换为Integer， 用new Timestamp(calendar
     * .getTimeInMillis())将Calendar转换为Timestamp，Date将会自动转换为Timestamp
     * @param keyIdx 指定key在ResultSet中的序号
     * @param extractorClass 实现了ResultSetExtractable接口的数据提取操作类，
     * 用于将ResultSet转储到自身（其populate方法返回this ），或转储到其他类型的对象（如ResultSet2Array）
     * @return 返回查询结果
     * @throws Exception 数据库操作例外
     */
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
    
    /**
     * 该方法提供自定义查询特定值的数据
     * 
     * @param sql 查询语句
     * @param parameters 参数以Object[]传入，每个参数都不能为null.
     * 参数传入前需用Integer.valueOf(int)将int转换为Integer，
     * @param keyIdx 指定key在ResultSet中的序号
     * @param extractorClass 实现了ResultSetExtractable接口的数据提取操作类，
     * 用于将ResultSet转储到自身（其populate方法返回this ），或转储到其他类型的对象（如ResultSet2Array）
     * @return 返回查询结果
     * @throws Exception 数据库操作例外
     */
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
                    // 如果extractor.populate(rs)返回this，则每一个rs都创建新的extractor实例来转储；
                    // 如果extractor.populate(rs)返回新的对象，则总是用一个extractor为每一个rs作转储
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
    
    /**
     * 该方法用于格式化信息
     * 
     * @param arrName 参数名
     * @param arrMsg 参数值
     * @return 格式化后的信息
     */
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