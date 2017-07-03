package com.tzg.ex.mvc.web.exception.mysql;

import com.alibaba.druid.pool.ExceptionSorter;

import java.sql.SQLException;
import java.util.Properties;

/**
 * 
 * Filename:    MySqlExceptionSorter.java  
 * Description: 当一个连接产生不可恢复的异常时，例如session has been killed，必须立刻从连接池中逐出，否则会产生大量错误。目前只有Druid和JBoss DataSource实现了ExceptionSorter。   
 * Copyright:   Copyright (c) 2015-2018 All Rights Reserved.
 * Company:     tzg.cn Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2016年4月6日 下午4:39:26  
 *
 */
public class MySqlExceptionSorter implements ExceptionSorter {

    @Override
    public boolean isExceptionFatal(SQLException e) {
        final String sqlState = e.getSQLState();
        final int errorCode = e.getErrorCode();

        if (sqlState != null && sqlState.startsWith("08")) {
            return true;
        }
        
        switch (errorCode) {
        // Communications Errors
            case 1040: // ER_CON_COUNT_ERROR
            case 1042: // ER_BAD_HOST_ERROR
            case 1043: // ER_HANDSHAKE_ERROR
            case 1047: // ER_UNKNOWN_COM_ERROR
            case 1081: // ER_IPSOCK_ERROR
            case 1129: // ER_HOST_IS_BLOCKED
            case 1130: // ER_HOST_NOT_PRIVILEGED
                // Authentication Errors
            case 1045: // ER_ACCESS_DENIED_ERROR
                // Resource errors
            case 1004: // ER_CANT_CREATE_FILE
            case 1005: // ER_CANT_CREATE_TABLE
            case 1015: // ER_CANT_LOCK
            case 1021: // ER_DISK_FULL
            case 1041: // ER_OUT_OF_RESOURCES
                // Out-of-memory errors
            case 1037: // ER_OUTOFMEMORY
            case 1038: // ER_OUT_OF_SORTMEMORY
                return true;
            default:
                break;
        }
        
        // for oceanbase
        if (errorCode >= -10000 && errorCode <= -9000) {
            return true;
        }
        
        String className = e.getClass().getName();
        if ("com.mysql.jdbc.CommunicationsException".equals(className)) {
            return true;
        }

        String message = e.getMessage();
        if (message != null && message.length() > 0) {
            final String errorText = message.toUpperCase();

            if ((errorCode == 0 && (errorText.contains("COMMUNICATIONS LINK FAILURE")) //
            || errorText.contains("COULD NOT CREATE CONNECTION")) //
                || errorText.contains("NO DATASOURCE") //
                || errorText.contains("NO ALIVE DATASOURCE")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void configFromProperties(Properties properties) {

    }

}