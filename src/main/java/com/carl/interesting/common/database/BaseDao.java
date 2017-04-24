package com.carl.interesting.common.database;

import java.util.List;

import com.carl.interesting.common.exception.DataAccessException;

/**
 * database operation base class
 * 
 * @author Jingjing Liu
 * @version [version, 12 Jun 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public interface BaseDao<T> {
    /**
     * add data to database
     * 
     * @param t
     * @return
     * @throws DataAccessException [explain parameter]
     * @return int [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public long add(T t) throws DataAccessException;
    
    /**
     * get data by query condition
     * 
     * @param sql
     * @return
     * @throws DataAccessException [explain parameter]
     * @return List<T> [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public List<T> list(String sql) throws DataAccessException;
    
    /**
     * get data by ID
     * 
     * @param id
     * @return
     * @throws DataAccessException [explain parameter]
     * @return T [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public T getByID(String id) throws DataAccessException;
    
    /**
     * update data
     * 
     * @param t
     * @return
     * @throws DataAccessException [explain parameter]
     * @return int [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public int update(T t) throws DataAccessException;
    
    /**
     * delete data by ID
     * 
     * @param id
     * @return
     * @throws DataAccessException [explain parameter]
     * @return int [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public int deleteByID(String id) throws DataAccessException;
    
    /**
     * batch delete data
     * 
     * @param id id array,int
     * @return
     * @throws DataAccessException [explain parameter]
     * @return int [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public int delete(int id[]) throws DataAccessException;
    
    /**
     * get all data
     * 
     * @return
     * @throws DataAccessException [explain parameter]
     * @return List<T> [explain return type]
     * @exception throws [exception type] [explain exception]
     * @see [class,class#method,class#member]
     */
    public List<T> getAll() throws DataAccessException;
}
