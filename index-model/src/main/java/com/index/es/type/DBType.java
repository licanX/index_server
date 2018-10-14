package com.index.es.type;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 数据库字段类型
 * 
 * @author lican
 * @date 2018年8月28日
 * @since v1.0.0
 */
public class DBType {
	
	 /** date type */
    private final static String       DATE             = "DATE";
    /** time type */
    private final static String       TIME             = "TIME";
    /** float type */
    private final static String       FLOAT            = "FLOAT";

    /** int type */
    private final static String       TINYINT          = "TINYINT";
    private final static String       SMALLINT         = "SMALLINT";
    private final static String       MEDIUMINT        = "MEDIUMINT";
    private final static String       INT              = "INT";
    private final static String       INTEGER          = "INTEGER";
    private static final List<String> INT_TYPES        = Lists.newArrayList(TINYINT, SMALLINT, MEDIUMINT, INT, INTEGER);

    /** double type */
    private final static String       DOUBLE_PRECISION = "DOUBLE PRECISION";
    private final static String       DOUBLE           = "DOUBLE";
    private static final List<String> DOUBLE_TYPES     = Lists.newArrayList(DOUBLE_PRECISION, DOUBLE);

    /** bigDecimal type */
    private final static String       DECIMAL          = "DECIMAL";
    private final static String       BIG_DECIMAL      = "BIGINT";
    private static final List<String> BIGDECIMAL_TYPES = Lists.newArrayList(DECIMAL, BIG_DECIMAL);

    /** byte type */
    private final static String       BIT              = "BIT";
    private final static String       BINARY           = "BINARY";
    private final static String       VARBINARY        = "VARBINARY";
    private final static String       TINYBLOB         = "TINYBLOB";
    private final static String       BLOB             = "BLOB";
    private final static String       MEDIUMBLOB       = "MEDIUMBLOB";
    private final static String       LONGBLOB         = "LONGBLOB";
    private static final List<String> BYTE_TYPES       = Lists.newArrayList(BIT, BINARY, VARBINARY, TINYBLOB, BLOB,
                                                                            MEDIUMBLOB, LONGBLOB);

    /** timestamp type */
    private final static String       DATETIME         = "DATETIME";
    private final static String       TIMESTAMP        = "TIMESTAMP";
    private static final List<String> TIMESTAMP_TYPES  = Lists.newArrayList(DATETIME, TIMESTAMP);

    /** string type */
    private final static String       CHAR             = "CHAR";
    private final static String       VARCHAR          = "VARCHAR";
    private final static String       TINYTEXT         = "TINYTEXT";
    private final static String       TEXT             = "TEXT";
    private final static String       MEDIUMTEXT       = "MEDIUMTEXT";
    private final static String       LONGTEXT         = "LONGTEXT";
    private static final List<String> STRING_TYPES     = Lists.newArrayList(CHAR, VARCHAR, TINYTEXT, TEXT, MEDIUMTEXT,
                                                                            LONGTEXT);

    public static boolean typeOfDate(String type) {
        return DATE.equals(type);
    }

    public static boolean typeOfTime(String type) {
        return TIME.equals(type);
    }

    public static boolean typeOfFloat(String type) {
        return FLOAT.equals(type);
    }

    public static boolean typeOfInt(String type) {
        return INT_TYPES.contains(type);
    }

    public static boolean typeOfDouble(String type) {
        return DOUBLE_TYPES.contains(type);
    }

    public static boolean typeOfBigDecimal(String type) {
        return BIGDECIMAL_TYPES.contains(type);
    }

    public static boolean typeOfTByte(String type) {
        return BYTE_TYPES.contains(type);
    }

    public static boolean typeOfTimestamp(String type) {
        return TIMESTAMP_TYPES.contains(type);
    }

    public static boolean typeOfString(String type) {
        return STRING_TYPES.contains(type);
    }
}
