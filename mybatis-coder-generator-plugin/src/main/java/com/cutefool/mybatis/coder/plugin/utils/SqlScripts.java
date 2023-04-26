package com.cutefool.mybatis.coder.plugin.utils;

import com.cutefool.commons.core.util.FilesUtils;
import com.cutefool.commons.core.util.Lists;
import com.cutefool.commons.core.util.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.sql.Types;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 脚本工具类
 */
@Slf4j
public class SqlScripts {

    private static final DefaultShellCallback defaultShellCallback = new DefaultShellCallback(false);

    public static void doSqlScripts(IntrospectedTable introspectedTable, boolean isUpper) {
        String tableName = introspectedTable.getTableConfiguration().getTableName();
        File file = getFile(tableName);
        FilesUtils.delete(file);

        if (!Objects.equals("TABLE", introspectedTable.getTableType())) {
            log.info("this table type = {} ignore", introspectedTable.getTableType());
            return;
        }

        try (FileOutputStream os = new FileOutputStream(file)) {
            // 查询所有的列
            List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
            String s = doCreateTable(introspectedTable, allColumns, isUpper);
            IOUtils.write(s, os, Charset.defaultCharset());
            String tableComments = doTableComments(introspectedTable, isUpper);
            IOUtils.write(tableComments, os, Charset.defaultCharset());
            String columns = doColumns(introspectedTable, allColumns, isUpper);
            IOUtils.write(columns, os, Charset.defaultCharset());
            String comments = doComments(introspectedTable, allColumns, isUpper);
            IOUtils.write(comments, os, Charset.defaultCharset());
            String doDefault = doDefault(introspectedTable, allColumns, isUpper);
            IOUtils.write(doDefault, os, Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String doCreateTable(IntrospectedTable introspectedTable, List<IntrospectedColumn> allColumns, boolean isUpper) {
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        // 查询所有的列
        StringBuilder sb = new StringBuilder();
        sb.append("-- 创建");
        sb.append(introspectedTable.getRemarks());
        sb.append("\n");
        sb.append("CALL CREATE_TABLES('");
        sb.append(tableNameNot(introspectedTable, isUpper));
        sb.append("','CREATE TABLE ");
        sb.append(tableName(introspectedTable, isUpper));
        sb.append(" (\n");

        String columns = allColumns.stream().map(e -> {
            StringBuffer columnsSb = new StringBuffer();
            columnsSb.append("\t");
            columnsSb.append(columnName(e, isUpper));
            columnsSb.append(" ");
            columnsSb.append(columnType(e));
            columnsSb.append(e.isNullable() ? " NULL" : " NOT NULL");
            return columnsSb.toString();
        }).collect(Collectors.joining(Strings.COMMA + "\n"));
        sb.append(columns);
        String keys = Lists.empty(primaryKeyColumns)
                .stream()
                .map(c -> columnName(c, isUpper))
                .collect(Collectors.joining(Strings.COMMA));
        if (StringUtils.isNotBlank(keys)) {
            sb.append(",\n");
            sb.append(String.format("\tPRIMARY KEY (%s)\n", keys));
        }
        sb.append("\t);'\n");
        sb.append(");\n");
        return sb.toString();
    }


    private static String doTableComments(IntrospectedTable introspectedTable, boolean isUpper) {
        // 查询所有的列
        StringBuilder sb = new StringBuilder();
        sb.append("-- 设置注释");
        sb.append(introspectedTable.getRemarks());
        sb.append("\n");
        sb.append("CALL MODIFY_TABLE_COMMENTS('");
        sb.append(tableName(introspectedTable, isUpper));
        sb.append("', '");
        String remarks = introspectedTable.getRemarks();
        if (remarks.contains("|")) {
            sb.append(remarks);
        } else {
            sb.append(remarks);
            sb.append("|");
            sb.append(remarks);
        }
        sb.append("');\n");
        return sb.toString();
    }

    private static String doColumns(IntrospectedTable introspectedTable, List<IntrospectedColumn> allColumns, boolean isUpper) {
        // 查询所有的列
        StringBuilder sb = new StringBuilder();
        sb.append("-- 设置修改字段");
        sb.append(introspectedTable.getRemarks());
        sb.append("\n");
        allColumns.forEach(e -> {
            sb.append("CALL MODIFY_COLUMNS('");
            sb.append(tableName(introspectedTable, isUpper));
            sb.append("', '");
            sb.append(columnName(e, isUpper));
            sb.append("', ");
            sb.append("'");
            sb.append(columnType(e));
            sb.append(e.isNullable() ? " NULL" : " NOT NULL");
            sb.append("');\n");
        });
        return sb.toString();
    }


    private static String doComments(IntrospectedTable introspectedTable, List<IntrospectedColumn> allColumns, boolean isUpper) {
        // 查询所有的列
        StringBuilder sb = new StringBuilder();
        sb.append("-- 设置注释");
        sb.append(introspectedTable.getRemarks());
        sb.append("\n");
        allColumns.forEach(e -> {
            String remarks = e.getRemarks();
            sb.append("CALL MODIFY_COLUMNS_COMMENTS('");
            sb.append(tableName(introspectedTable, isUpper));
            sb.append("', '");
            sb.append(columnName(e, isUpper));
            sb.append("', ");
            sb.append("'");
            if (remarks.contains("|")) {
                sb.append(remarks);
            } else {
                sb.append(remarks);
                sb.append("|");
                sb.append(remarks);
            }
            sb.append("');\n");
        });
        return sb.toString();
    }


    private static String doDefault(IntrospectedTable introspectedTable, List<IntrospectedColumn> allColumns, boolean isUpper) {
        // 查询所有的列
        //CALL MODIFY_COLUMNS_DEFAULT('PATROL_POINT_RESULT', 'TENANT_ID',0);
        StringBuilder sb = new StringBuilder();
        sb.append("-- 设置默认值");
        sb.append(introspectedTable.getRemarks());
        sb.append("\n");
        allColumns.stream()
                .filter(e -> StringUtils.isNotBlank(e.getDefaultValue()) && isCreate(e.getDefaultValue()))
                .forEach(e -> {
                    sb.append("CALL MODIFY_COLUMNS_DEFAULT('");
                    sb.append(tableName(introspectedTable, isUpper));
                    sb.append("', '");
                    sb.append(columnName(e, isUpper));
                    sb.append("', ");
                    sb.append("'");
                    sb.append(getDefaultValue(e));
                    sb.append("');\n");
                });
        return sb.toString();
    }

    private static boolean isCreate(String v) {
        return !Objects.equals(v, "CURRENT_TIMESTAMP");
    }

    private static String columnName(IntrospectedColumn column, boolean isUpper) {
        if (isUpper) {
            return "`" + column.getActualColumnName().toUpperCase() + "`";
        }
        return "`" + column.getActualColumnName() + "`";
    }

    private static String tableNameNot(IntrospectedTable introspectedTable, boolean isUpper) {
        String s = tableName(introspectedTable, isUpper);
        return s.replace("`", "");
    }

    private static String tableName(IntrospectedTable introspectedTable, boolean isUpper) {
        if (isUpper) {
            return "`" + introspectedTable.getTableConfiguration().getTableName().toUpperCase() + "`";
        }
        return "`" + introspectedTable.getTableConfiguration().getTableName() + "`";
    }


    private static String columnType(IntrospectedColumn column) {
        String jdbcTypeName = column.getJdbcTypeName();
        if (Objects.equals(jdbcTypeName, "TIMESTAMP")) {
            return "DATETIME";
        }

        if (Objects.equals(jdbcTypeName, "BIT")) {
            return "TINYINT";
        }

        if (isActual(column)) {
            return column.getActualTypeName();
        }

        if (isNumber(column)) {
            return jdbcTypeName;
        }

        int length = column.getLength();
        int scale = column.getScale();
        if (length <= 0) {
            return jdbcTypeName;
        }
        if (scale <= 0 || isDouble(column)) {
            return jdbcTypeName + "(" + length + ")";
        }
        return jdbcTypeName + "(" + length + "," + scale + ")";
    }

    static boolean isActual(IntrospectedColumn column) {
        return Objects.equals(column.getJdbcTypeName(), "DATE")
                || Objects.equals(column.getActualTypeName(), "TIME")
                || Objects.equals(column.getActualTypeName(), "DATETIME")
                || Objects.equals(column.getJdbcTypeName(), "TIMESTAMP")
                || column.getJdbcType() == Types.LONGVARCHAR
                || column.getJdbcType() == Types.LONGVARBINARY
                || column.getJdbcType() == Types.VARBINARY
                || column.getJdbcType() == Types.BINARY;
    }

    static boolean isNumber(IntrospectedColumn column) {
        return Objects.equals(column.getJdbcTypeName(), "BIGINT")
                || Objects.equals(column.getJdbcTypeName(), "TINYINT")
                || Objects.equals(column.getJdbcTypeName(), "INT")
                || Objects.equals(column.getJdbcTypeName(), "SMALLINT")
                || Objects.equals(column.getJdbcTypeName(), "BIT")
                || Objects.equals(column.getJdbcTypeName(), "INTEGER");
    }

    static String getDefaultValue(IntrospectedColumn column) {
        if (Objects.equals(column.getJdbcTypeName(), "BIT")) {
            return column.getDefaultValue().replace("b", "").replace("'", "");
        }
        return column.getDefaultValue();
    }

    static boolean isDouble(IntrospectedColumn column) {
        return Objects.equals(column.getJdbcTypeName(), "DOUBLE");
    }


    static File directory() {
        try {
            return defaultShellCallback.getDirectory("src/main/resources", "db/migration");
        } catch (ShellException e) {
            throw new RuntimeException(e);
        }
    }

    static File getFile(String name) {
        return new File(directory(), "R__init.news." + name.toLowerCase() + ".sql");
    }
}
