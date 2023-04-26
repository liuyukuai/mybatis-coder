/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.utils;

import com.cutefool.mybatis.coder.core.MyColumnOverride;
import com.cutefool.mybatis.coder.plugin.coder.JavaGenerator;
import com.cutefool.mybatis.coder.plugin.constant.Constants;
import com.cutefool.mybatis.coder.plugin.sqlserver.SqlServers;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.ColumnOverride;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 增加execle注释（目前采用easyExcel工具类）
 *
 * @author 271007729@qq.com
 * @date 2022/9/21 9:42 AM
 */
@SuppressWarnings("ALL")
public final class SqlExcels {

    private static List<String> exculus = Arrays.asList("id", "tenantId", "sortId", "deleteStatus", "orgCode");

    private SqlExcels() {
    }

    public static void addExecle(Field field, TopLevelClass topLevelClass, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

        SqlServers.setRemarks(introspectedTable, introspectedColumn);
        // 生成属性注释
        String javaProperty = introspectedColumn.getJavaProperty();
        // 字段注释
        String remarks = JavaGenerator.remarks(introspectedColumn.getRemarks());
        String primaryKey = SqlKeys.primaryKey(introspectedTable);
        doEasyExcel(topLevelClass, javaProperty, primaryKey, remarks, field, introspectedTable, introspectedColumn);


    }

    private static void doEasyExcel(TopLevelClass topLevelClass, String javaProperty, String primaryKey, String remarks, Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {


        // 查询配置信息
        ColumnOverride columnOverride = introspectedTable.getTableConfiguration().getColumnOverride(introspectedColumn.getActualColumnName());

        if (Objects.isNull(columnOverride)) {
            return;
        }
        if (columnOverride instanceof MyColumnOverride) {
            String naming = ((MyColumnOverride) columnOverride).getNaming();
            boolean imported = ((MyColumnOverride) columnOverride).isImported();
            if (imported == false || StringUtils.isNotBlank(naming)) {
                // 如果不为空
                field.addAnnotation(Constants.EXCEL_IGNORE);
                // import
                topLevelClass.addImportedType(new FullyQualifiedJavaType("com.cutefool.commons.core.ExcelIgnore"));
                return;
            }
            // import
            topLevelClass.addImportedType(new FullyQualifiedJavaType("com.cutefool.commons.core.Excel"));
            // 增加easyExcel注释
            field.addAnnotation(String.format(Constants.EXCEL_PROPERTY, remarks));
            return;
        }
    }
}
