/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.utils;

import com.cutefool.commons.core.util.Lists;
import com.cutefool.commons.core.util.Strings;
import com.cutefool.mybatis.coder.plugin.constant.Constants;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * mapper生成工具类
 *
 * @author 271007729@qq.com
 * @date 2022/9/20 11:34 PM
 */
public final class SqlMappers {

    private SqlMappers() {
    }

    public static void init(Interface interfaces, IntrospectedTable introspectedTable, Properties properties) {
        interfaces.addAnnotation("@SuppressWarnings({\"all\"})");
        // 添加mapper接口
        addInterface(interfaces, introspectedTable, properties);
        // 添加获取所有列的方法
        addColumns(interfaces);
        addMapping(interfaces);
        addFields(interfaces);
        addExcels(interfaces);
        // 添加主键相关方法(联合主键）
        if (SqlKeys.newCreate(introspectedTable)) {
            addPrimaryKeyMethods(interfaces, introspectedTable);
        }
        // 添加批量操作方法
        SqlMethods.addSelectMultiplePrimaryKeyMethods(interfaces, introspectedTable);
        SqlMethods.addMultipleDeletePrimaryKeyMethods(interfaces, introspectedTable);
        SqlMethods.addTable(interfaces, introspectedTable);
        SqlMethods.addRemarks(interfaces, introspectedTable);
        SqlMethods.addSqlTabl(interfaces, introspectedTable);
        SqlMethods.addIdColumn(interfaces, introspectedTable);
        SqlMethods.addSelectList(interfaces, introspectedTable);

        SqlMethods.addInsertWithFields(interfaces, introspectedTable);
        SqlMethods.addInsertMultipleWithFields(interfaces, introspectedTable);
        SqlMethods.addInsertSelectiveWithFields(interfaces, introspectedTable);
        SqlMethods.addNewPrimaryKey(interfaces, introspectedTable);

        SqlMethods.addUpdateAllColumnsDsl(interfaces, introspectedTable);
        SqlMethods.addUpdateSelectiveColumnsDsl(interfaces, introspectedTable);

        SqlDsls.addInsertDsl(interfaces, introspectedTable);
        SqlDsls.addInsertSelectiveDsl(interfaces, introspectedTable);
        SqlDsls.addInsertMultipleDsl(interfaces, introspectedTable);
        SqlDsls.addUpdateDsl(interfaces, introspectedTable);
        SqlDsls.addUpdateSelectiveDsl(interfaces, introspectedTable);
        
        

    }

    public static void addPrimaryKeyMethods(Interface interfaces, IntrospectedTable introspectedTable) {
        // 添加联合主键删除数据方法
        Method delete = new Method("deleteByPrimaryKey");
        delete.setReturnType(new FullyQualifiedJavaType("int"));
        delete.addParameter(new Parameter(new FullyQualifiedJavaType(SqlKeys.primaryKey(introspectedTable)), "id"));
        delete.addAnnotation("@Override");
        delete.setDefault(true);
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        String params = Lists.empty(primaryKeyColumns)
                .stream()
                .map(e -> "id.get" + toUpperCaseFirstOne(e.getJavaProperty() + "() "))
                .collect(Collectors.joining(Strings.COMMA));

        delete.addBodyLine(" return deleteByPrimaryKey(" + params + ");");
        interfaces.addMethod(delete);

        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();

        // 添加联合主键删除数据方法
        Method selects = new Method("selectByPrimaryKey");
        selects.setReturnType(new FullyQualifiedJavaType("Optional<" + domainObjectName + ">"));
        selects.addParameter(new Parameter(new FullyQualifiedJavaType(SqlKeys.primaryKey(introspectedTable)), "id"));
        selects.addAnnotation(Constants.OVERRIDE);
        selects.setDefault(true);
        selects.addBodyLine(" return selectByPrimaryKey(" + params + ");");
        interfaces.addMethod(selects);

    }

    private static void addInterface(Interface interfaces, IntrospectedTable introspectedTable, Properties properties) {
        // interface
        String apiModelAnnotationPackage = properties.getProperty("interface");
        // domain name
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        // key
        String primaryName = SqlKeys.primaryKey(introspectedTable);
        // import
        if (apiModelAnnotationPackage != null) {
            interfaces.addImportedType(new FullyQualifiedJavaType(apiModelAnnotationPackage));
            int i = apiModelAnnotationPackage.lastIndexOf(".");
            String simpleName = apiModelAnnotationPackage.substring(i + 1);
            apiModelAnnotationPackage = simpleName + "<" + domainObjectName + "," + primaryName + ">";
            FullyQualifiedJavaType fullyQualifiedJavaType = new FullyQualifiedJavaType(apiModelAnnotationPackage);
            interfaces.addSuperInterface(fullyQualifiedJavaType);
        }
    }

    private static void addColumns(Interface interfaces) {
        // 添加获取所有列的方法
        Method method = new Method("columnsMap");
        method.setReturnType(new FullyQualifiedJavaType("Map<String, SqlColumn<?>>"));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setDefault(true);
        method.addAnnotation("@Override");
        method.addBodyLine("return columns();");
        interfaces.addMethod(method);
        interfaces.addImportedType(new FullyQualifiedJavaType("java.util.Map"));
        interfaces.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.SqlColumn"));
    }

    private static void addMapping(Interface interfaces) {
        // 添加获取所有列的方法
        Method method = new Method("columnMapping");
        method.setReturnType(new FullyQualifiedJavaType("Map<String, String>"));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setDefault(true);
        method.addAnnotation("@Override");
        method.addBodyLine("return mapping();");
        interfaces.addMethod(method);
    }

    private static void addFields(Interface interfaces) {
        // 添加获取所有列的方法
        Method method = new Method("fieldsMap");
        method.setReturnType(new FullyQualifiedJavaType("Map<String, String>"));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setDefault(true);
        method.addAnnotation("@Override");
        method.addBodyLine("return fields();");
        interfaces.addMethod(method);
        interfaces.addImportedType(new FullyQualifiedJavaType("java.util.Map"));
    }


    private static void addExcels(Interface interfaces) {
        // 添加获取所有列的方法
        Method method = new Method("excelsMap");
        method.setReturnType(new FullyQualifiedJavaType("Map<String, String>"));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setDefault(true);
        method.addAnnotation("@Override");
        method.addBodyLine("return excels();");
        interfaces.addMethod(method);
        interfaces.addImportedType(new FullyQualifiedJavaType("java.util.Map"));
    }

    /**
     * 首字母小写
     *
     * @param s 字符串
     * @return string
     */
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

}
