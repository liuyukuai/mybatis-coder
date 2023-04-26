/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.utils;

import com.cutefool.commons.core.util.Maps;
import com.cutefool.mybatis.coder.core.MyColumnOverride;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 增加execle注释（目前采用easyExcel工具类）
 *
 * @author 271007729@qq.com
 * @date 2022/9/21 9:42 AM
 */
@SuppressWarnings("ALL")
public final class SqlBulks {

    private static List<String> exculus = Arrays.asList("id", "tenantId", "sortId", "deleteStatus", "orgCode");

    private SqlBulks() {
    }

    public static void doBulked(Field field, TopLevelClass topLevelClass, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

        SqlServers.setRemarks(introspectedTable, introspectedColumn);
        // 生成属性注释
        String javaProperty = introspectedColumn.getJavaProperty();
        // 字段注释
        String primaryKey = SqlKeys.primaryKey(introspectedTable);
        doEasyExcel(topLevelClass, javaProperty, primaryKey, field, introspectedTable, introspectedColumn);


    }

    private static void doEasyExcel(TopLevelClass topLevelClass, String javaProperty, String primaryKey, Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        // 查询配置信息
        ColumnOverride columnOverride = introspectedTable.getTableConfiguration().getColumnOverride(introspectedColumn.getActualColumnName());

        if (Objects.isNull(columnOverride)) {
            return;
        }
        if (columnOverride instanceof MyColumnOverride) {
            String naming = ((MyColumnOverride) columnOverride).getNaming();
            boolean imported = ((MyColumnOverride) columnOverride).isImported();
            String fields = ((MyColumnOverride) columnOverride).getFields();
            String remarks = ((MyColumnOverride) columnOverride).getRemarks();
            Map<String, MyColumnOverride.F> fieldsMapping = ((MyColumnOverride) columnOverride).getFieldsMapping();

            if (imported == true && StringUtils.isNotBlank(naming)) {
                topLevelClass.addImportedType(new FullyQualifiedJavaType("com.cutefool.commons.bulk.Bulked"));

                String anno = StringUtils.isNotBlank(fields) ? Constants.BULKED + "(value = \"" + naming + "\", fields = \"" + fields + "\"" + ", remarks = \"" + remarks + "\"" : Constants.BULKED + "(value = \"" + naming + "\"";
                if (Maps.isEmpty(fieldsMapping)) {
                    // 如果不为空
                    field.addAnnotation(anno + ")");
                    return;
                }
                topLevelClass.addImportedType(new FullyQualifiedJavaType("com.cutefool.commons.bulk.BulkMapping"));
                field.addAnnotation(anno + doMapping(fieldsMapping) + ")");
            }
        }
    }

    private static String doMapping(Map<String, MyColumnOverride.F> fieldsMapping) {
        String tempate = "@BulkMapping(s = \"%s\", d = \"%s\", remarks = \"%s\")";
        String mapping = fieldsMapping.entrySet().stream().map(e -> String.format(tempate, e.getKey(), e.getValue().getName(), e.getValue().getRemarks())).collect(Collectors.joining(", "));
        String s = ", mapping = {%s}";
        return String.format(s, mapping);
    }

}
