package com.carl.interesting.common.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 新定义了一个接口，该接口提供了一个对数据查询结果进行操作的方法，所有继承此接口的类都得实现该方法， 并且抛出SQL Server
 * 返回警告或错误时引发的异常
 * 
 * @author Carl Liu
 * @version [version, 22 Jul 2016]
 * @see [about class/method]
 * @since [product/module version]
 */
public interface ResultSetExtractable {
    /**
     * 重载方法，用于处理数据库查询的数据集
     * 
     * @param rs 数据集
     * @return 处理后的数据
     * @throws SQLException 数据库操作例外
     */
    public Object populate(ResultSet rs) throws SQLException;
}
