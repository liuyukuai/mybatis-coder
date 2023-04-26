/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.utils;

import com.cutefool.commons.core.ExcelIgnore;
import com.cutefool.commons.core.util.Lists;
import com.cutefool.commons.core.util.Maps;
import com.cutefool.mybatis.coder.core.MyColumnOverride;
import com.cutefool.mybatis.coder.plugin.coder.JavaGenerator;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.ColumnOverride;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.TableConfiguration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * mapper生成工具类
 *
 * @author 271007729@qq.com
 * @date 2022/9/20 11:34 PM
 */
public final class SqlSupports {

    private SqlSupports() {
    }

    public static void dynamicSqlSupportGenerated(Context context, TopLevelClass supportClass, IntrospectedTable introspectedTable) {
        List<Field> fields = SqlColumns.fields(supportClass, introspectedTable);
        addColumnsMap(fields, supportClass);
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        addStaticFields(allColumns, supportClass);
        addFieldsMap(allColumns, supportClass);
        addColumnsMapping(allColumns, supportClass);
        addExcelFields(context, supportClass, allColumns, introspectedTable);

    }

    public static void addColumnsMap(List<Field> fields, TopLevelClass supportClass) {
        // 所有的列
        // 添加获取所有列的方法
        Method method = new Method("columns");
        method.setReturnType(new FullyQualifiedJavaType("Map<String,SqlColumn<?>>"));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setStatic(true);
        method.addBodyLine("Map<String, SqlColumn<?>> maps = new LinkedHashMap(" + Lists.empty(fields).size() + ");");
        Lists.empty(fields)
                .forEach(e -> method.addBodyLine("maps.put(\"" + e.getName() + "\", " + e.getInitializationString()
                        .orElse("") + ");"));
        method.addBodyLine("return maps;");
        supportClass.addMethod(method);
        supportClass.addImportedType("java.util.Map");
        supportClass.addImportedType("java.util.LinkedHashMap");
    }

    public static void addColumnsMapping(List<IntrospectedColumn> allColumns, TopLevelClass supportClass) {
        // 所有的列
        // 添加获取所有列的方法
        Method method = new Method("mapping");
        method.setReturnType(new FullyQualifiedJavaType("Map<String, String>"));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setStatic(true);
        method.addBodyLine("Map<String, String> maps = new LinkedHashMap(" + Lists.empty(allColumns).size() + ");");
        Lists.empty(allColumns)
                .forEach(e -> method.addBodyLine("maps.put(\"" + e.getActualColumnName().toLowerCase() + "\", \"" + e.getJavaProperty()
                        + "\");"));
        method.addBodyLine("return maps;");
        supportClass.addMethod(method);
        supportClass.addImportedType("java.util.Map");
        supportClass.addImportedType("java.util.LinkedHashMap");
    }


    public static void addStaticFields(List<IntrospectedColumn> allColumns, TopLevelClass supportClass) {

        Lists.empty(allColumns).forEach(e -> {
            Field field = new Field("FIELD_" + e.getActualColumnName().toUpperCase(), new FullyQualifiedJavaType("String" +
                    ""));
            field.setStatic(true);
            field.setVisibility(JavaVisibility.PUBLIC);
            field.setFinal(true);
            field.setInitializationString("\"" + e.getJavaProperty() + "\"");
            supportClass.addField(field);
        });
        supportClass.addAnnotation("@SuppressWarnings(\"all\")");
    }


    public static void addFieldsMap(List<IntrospectedColumn> allColumns, TopLevelClass supportClass) {
        Method method = new Method("fields");
        method.setStatic(true);
        method.setReturnType(new FullyQualifiedJavaType("Map<String,String>"));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addBodyLine("Map<String, String> maps = new LinkedHashMap(" + Lists.empty(allColumns).size() + ");");
        Lists.empty(allColumns)
                .stream()
                .filter(e -> !Objects.equals("tenantId", e.getJavaProperty()))
                .filter(e -> !Objects.equals("deleteStatus", e.getJavaProperty()))
                .forEach(e -> {
                    String remarks = e.getRemarks();
                    if (StringUtils.isBlank(remarks)) {
                        remarks = e.getJavaProperty();
                    }
                    method.addBodyLine("maps.put(\"" + e.getJavaProperty() + "\", \"" + JavaGenerator.remarks(remarks) + "\");");
                });
        method.addBodyLine("return maps;");
        supportClass.addMethod(method);
        supportClass.addImportedType("java.util.Map");
        supportClass.addImportedType("java.util.LinkedHashMap");
    }

    public static void addExcelFields(Context context, TopLevelClass supportClass, List<IntrospectedColumn> allColumns, IntrospectedTable introspectedTable) {

        TableConfiguration tableConfiguration = introspectedTable.getTableConfiguration();
        // 排除
        List<IntrospectedColumn> baseColumns = introspectedTable.getBaseColumns();
        Map<String, String> excelsMap = excelsMap(context, baseColumns, tableConfiguration);
        // 需要补充填充的属性

        Method method = new Method("excels");
        method.setStatic(true);
        method.setReturnType(new FullyQualifiedJavaType("Map<String,String>"));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addBodyLine("Map<String, String> maps = new LinkedHashMap<>(" + Lists.empty(allColumns).size() + ");");
        Maps.empty(excelsMap)
                .entrySet()
                .stream()
                .filter(e -> !Objects.equals("tenantId", e.getKey()))
                .filter(e -> !Objects.equals("deleteStatus", e.getKey()))
                .forEach((entry) -> {
                    String remarks = entry.getValue();
                    remarks = StringUtils.isBlank(remarks) ? entry.getKey() : remarks;
                    method.addBodyLine("maps.put(\"" + entry.getKey() + "\", \"" + JavaGenerator.remarks(remarks) + "\");");
                });
        method.addBodyLine("return maps;");
        supportClass.addMethod(method);
    }


    private static boolean isExclude(ColumnOverride columnOverride) {
        return Objects.nonNull(columnOverride) && columnOverride instanceof MyColumnOverride && !((MyColumnOverride) columnOverride).isImported();
    }

    private static Map<String, String> excelsMap(Context context, List<IntrospectedColumn> baseColumns, TableConfiguration tableConfiguration) {
        // 导出excel需要排除的属性
        List<String> excludes = Lists.empty(JavaGenerator.excludes(context, ExcelIgnore.class));

        Map<String, String> includes = JavaGenerator.includes(context);

        Map<String, String> excelsMap = new LinkedHashMap<>();

        for (IntrospectedColumn baseColumn : baseColumns) {
            if (excludes.contains(baseColumn.getJavaProperty())) {
                continue;
            }
            ColumnOverride columnOverride = tableConfiguration.getColumnOverride(baseColumn.getActualColumnName());
            // 如果为空，默认需要导出
            if (Objects.isNull(columnOverride)) {
                excelsMap.put(baseColumn.getJavaProperty(), JavaGenerator.remarks(baseColumn.getRemarks()));
                continue;
            }
            if (isExclude(columnOverride)) {
                continue;
            }

            if (columnOverride instanceof MyColumnOverride) {
                String fields = ((MyColumnOverride) columnOverride).getFields();
                String remarks = ((MyColumnOverride) columnOverride).getRemarks();
                if (StringUtils.isNotBlank(fields)) {
                    excelsMap.put(fields, remarks);
                    continue;
                }
                Map<String, MyColumnOverride.F> destMapping = ((MyColumnOverride) columnOverride).getFieldsMapping();
                Maps.empty(destMapping).forEach((k, v) -> {
                    if (Objects.nonNull(v)) {
                        excelsMap.put(v.getName(), v.getRemarks());
                    }
                });
            }
        }
        excelsMap.putAll(includes);
        return excelsMap;
    }

}
