package com.icbt.pahanaedubookshopjavaee.util;

import com.icbt.pahanaedubookshopjavaee.util.constants.CommonConstants;
import com.icbt.pahanaedubookshopjavaee.util.constants.DBConstants;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

public class DBUtil {
    private static final BasicDataSource dataSource;

    static {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(DBConstants.DB_DRIVER);
        dataSource.setUrl(DBConstants.DB_URL + DBConstants.DEPLOYMENT_PORT + CommonConstants.SLASH_STRING + DBConstants.DB_NAME);
        dataSource.setUsername(DBConstants.DB_USERNAME);
        dataSource.setPassword(DBConstants.DB_PASSWORD);
        dataSource.setInitialSize(2);
        dataSource.setMaxTotal(2);
    }

    private DBUtil() {} // prevent instantiation

    public static DataSource getInstance() {
        return dataSource;
    }
}
