/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.sqlserver;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author 271007729@qq.com
 */
public class SqlServers {

    private static boolean isSqlServer(Connection connection) throws SQLException {
        return connection.getMetaData().getDriverName().toUpperCase().contains("SQL SERVER");
    }

    public static boolean isSqlServer(IntrospectedTable introspectedTable) {
        Connection connection = null;
        try {
            connection = introspectedTable.getContext().getConnection();
            return isSqlServer(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (Objects.nonNull(connection)) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void setRemarks(IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        Connection connection = null;
        try {
            connection = introspectedTable.getContext().getConnection();
            if (isSqlServer(connection)) {
                ResultSet tables = tables(connection, introspectedTable, introspectedColumn);
                boolean next = tables.next();
                if (next) {
                    String remarks = tables.getString("REMARKS");
                    introspectedColumn.setRemarks(remarks);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(connection)) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private static ResultSet tables(Connection connection, IntrospectedTable introspectedTable, IntrospectedColumn column) throws SQLException {
        String catalog = connection.getCatalog();
        String introspectedTableName = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        String sql = "use " + catalog + "; SELECT\n" + "\tconvert(varchar(1000), C.\n" + "VALUE)\n" + "\tAS REMARKS\n" + "FROM\n" + "\tsys.tables A\n" + "INNER JOIN sys.columns B ON B.object_id = A.object_id\n" + "LEFT JOIN sys.extended_properties C ON C.major_id = B.object_id\n" + "AND C.minor_id = B.column_id\n" + "WHERE\n" + "\tA.name = '" + introspectedTableName + "' and B.name = '" + column.getActualColumnName() + "'; ";
        PreparedStatement ps = connection.prepareStatement(sql);
        return ps.executeQuery();
    }
}
