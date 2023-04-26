/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.utils;

import com.cutefool.commons.core.util.Lists;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.runtime.dynamic.sql.elements.AbstractMethodGenerator;

import java.util.List;

/**
 * @author 271007729@qq.com
 * @date 2022/9/21 9:42 AM
 */
@SuppressWarnings("ALL")
public final class SqlDsls {
    private SqlDsls() {
    }


    public static void addInsertMultipleDsl(Interface interfaces, IntrospectedTable introspectedTable) {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        interfaces.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.insert.MultiRowInsertDSL"));
        Method method = new Method("doInsertMultipleDsl"); //$NON-NLS-1$
        method.setDefault(true);
        method.setReturnType(new FullyQualifiedJavaType(" MultiRowInsertDSL<" + domainObjectName + "> insertDsl"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("MultiRowInsertDSL<" + domainObjectName + ">"), "c"));
        String tableFieldName = JavaBeansUtil.getValidPropertyName(introspectedTable.getMyBatisDynamicSQLTableObjectName());
        initInsertBody(method, tableFieldName, "MultiRowInsertDSL", "toProperty", introspectedTable);
        interfaces.addMethod(method);
    }

    public static void addInsertDsl(Interface interfaces, IntrospectedTable introspectedTable) {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        interfaces.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.insert.InsertDSL"));
        Method method = new Method("doInsertDsl"); //$NON-NLS-1$
        method.setDefault(true);
        method.setReturnType(new FullyQualifiedJavaType(" InsertDSL<" + domainObjectName + "> insertDsl"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("InsertDSL<" + domainObjectName + ">"), "c"));
        String tableFieldName = JavaBeansUtil.getValidPropertyName(introspectedTable.getMyBatisDynamicSQLTableObjectName());
        initInsertBody(method, tableFieldName, "InsertDSL", "toProperty", introspectedTable);
        interfaces.addMethod(method);
    }


    public static void addInsertSelectiveDsl(Interface interfaces, IntrospectedTable introspectedTable) {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        interfaces.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.insert.InsertDSL"));
        Method method = new Method("doInsertSelectiveDsl"); //$NON-NLS-1$
        method.setDefault(true);
        method.setReturnType(new FullyQualifiedJavaType(" InsertDSL<" + domainObjectName + "> insertDsl"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName), "row"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("InsertDSL<" + domainObjectName + ">"), "c"));
        String tableFieldName = JavaBeansUtil.getValidPropertyName(introspectedTable.getMyBatisDynamicSQLTableObjectName());
        initInsertSelectiveBody(method, tableFieldName, "InsertDSL", "toPropertyWhenPresent", introspectedTable);
        interfaces.addMethod(method);
    }

    public static void addUpdateDsl(Interface interfaces, IntrospectedTable introspectedTable) {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        Method method = new Method("newUpdateDsl"); //$NON-NLS-1$
        method.setDefault(true);
        method.addAnnotation("@Override");
        method.setReturnType(new FullyQualifiedJavaType("UpdateDSL<UpdateModel>"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName), "row"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("UpdateDSL<UpdateModel>"), "c"));
        String tableFieldName = JavaBeansUtil.getValidPropertyName(introspectedTable.getMyBatisDynamicSQLTableObjectName());
        initUpdateBody(method, tableFieldName, "equalTo", introspectedTable);
        interfaces.addMethod(method);
    }

    public static void addUpdateSelectiveDsl(Interface interfaces, IntrospectedTable introspectedTable) {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        Method method = new Method("newUpdateSelectiveDsl"); //$NON-NLS-1$
        method.setDefault(true);
        method.addAnnotation("@Override");
        method.setReturnType(new FullyQualifiedJavaType("UpdateDSL<UpdateModel>"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName), "row"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("UpdateDSL<UpdateModel>"), "c"));
        String tableFieldName = JavaBeansUtil.getValidPropertyName(introspectedTable.getMyBatisDynamicSQLTableObjectName());
        initUpdateBody(method, tableFieldName, "equalToWhenPresent", introspectedTable);
        interfaces.addMethod(method);
    }


    private static void initInsertBody(Method method, String tableFieldName, String dslType, String propertyMethod, IntrospectedTable introspectedTable) {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        List<IntrospectedColumn> columns =
                ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());

        // 设置第一行元素
        if (Lists.iterable(columns)) {
            IntrospectedColumn first = columns.get(0);
            int size = columns.size();
            String fieldName = AbstractMethodGenerator.calculateFieldName(tableFieldName, first);
            method.addBodyLine("return c.map(" + fieldName //$NON-NLS-1$
                    + ").toProperty(\"" + first.getJavaProperty() //$NON-NLS-1$
                    + "\")" + (size == 1 ? ";" : "")); //$NON-NLS-1$
            // 其他属性
            for (int i = 1; i < columns.size(); i++) {
                IntrospectedColumn introspectedColumn = columns.get(i);
                if (i < columns.size() - 1) {
                    method.addBodyLine("        .map(" + AbstractMethodGenerator.calculateFieldName(tableFieldName, introspectedColumn) //$NON-NLS-1$
                            + ")." + propertyMethod + "(\"" + introspectedColumn.getJavaProperty() //$NON-NLS-1$
                            + "\")"); //$NON-NLS-1$
                } else {
                    method.addBodyLine("        .map(" + AbstractMethodGenerator.calculateFieldName(tableFieldName, introspectedColumn) //$NON-NLS-1$
                            + ")." + propertyMethod + "(\"" + introspectedColumn.getJavaProperty() //$NON-NLS-1$
                            + "\");"); //$NON-NLS-1$
                }
            }
        }
    }

    private static void initInsertSelectiveBody(Method method, String tableFieldName, String dslType, String propertyMethod, IntrospectedTable introspectedTable) {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        List<IntrospectedColumn> columns =
                ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());

        // 设置第一行元素
        if (Lists.iterable(columns)) {
            int size = columns.size();
            IntrospectedColumn first = columns.get(0);
            String fieldName = AbstractMethodGenerator.calculateFieldName(tableFieldName, first);
            boolean sequenceColumn = first.isSequenceColumn();
            propertyMethod = sequenceColumn ? "toProperty" : propertyMethod;

            if (first.isSequenceColumn()) {
                method.addBodyLine("return c.map(" + fieldName //$NON-NLS-1$
                        + ")." + propertyMethod + "(\"" + first.getJavaProperty() //$NON-NLS-1$
                        + "\")" + (size == 1 ? ";" : "")); //$NON-NLS-1$
            } else {
                String methodName =
                        JavaBeansUtil.getGetterMethodName(first.getJavaProperty(),
                                first.getFullyQualifiedJavaType());
                method.addBodyLine("return c.map(" + fieldName //$NON-NLS-1$
                        + ")." + propertyMethod + "(\"" + first.getJavaProperty() //$NON-NLS-1$
                        + "\", row::" + methodName //$NON-NLS-1$
                        + ")" + (size == 1 ? ";" : "")); //$NON-NLS-1$
            }


            // 其他属性
            for (int i = 1; i < columns.size(); i++) {
                IntrospectedColumn introspectedColumn = columns.get(i);
                fieldName = AbstractMethodGenerator.calculateFieldName(tableFieldName, introspectedColumn);
                propertyMethod = introspectedColumn.isSequenceColumn() ? "toProperty" : propertyMethod;
                if (i < columns.size() - 1) {
                    String methodName =
                            JavaBeansUtil.getGetterMethodName(introspectedColumn.getJavaProperty(),
                                    introspectedColumn.getFullyQualifiedJavaType());
                    method.addBodyLine("        .map(" + fieldName //$NON-NLS-1$
                            + ")." + propertyMethod + "(\"" + introspectedColumn.getJavaProperty() //$NON-NLS-1$
                            + "\", row::" + methodName //$NON-NLS-1$
                            + ")"); //$NON-NLS-1$
                } else {
                    String methodName =
                            JavaBeansUtil.getGetterMethodName(introspectedColumn.getJavaProperty(),
                                    introspectedColumn.getFullyQualifiedJavaType());
                    method.addBodyLine("        .map(" + fieldName //$NON-NLS-1$
                            + ")." + propertyMethod + "(\"" + introspectedColumn.getJavaProperty() //$NON-NLS-1$
                            + "\", row::" + methodName //$NON-NLS-1$
                            + ");"); //$NON-NLS-1$
                }
            }
        }
    }


    private static void initUpdateBody(Method method, String tableFieldName, String propertyMethod, IntrospectedTable introspectedTable) {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        // 设置第一行元素
        if (Lists.iterable(columns)) {
            int size = columns.size();
            IntrospectedColumn first = columns.get(0);
            String fieldName = AbstractMethodGenerator.calculateFieldName(tableFieldName, first);
            String methodName = JavaBeansUtil.getGetterMethodName(first.getJavaProperty(), first.getFullyQualifiedJavaType());
            method.addBodyLine("return c.set(" + fieldName //$NON-NLS-1$
                    + ")." + propertyMethod + "(row::" + methodName //$NON-NLS-1$
                    + ")" + (size == 1 ? ";" : "")); //$NON-NLS-1$

            // 其他属性
            for (int i = 1; i < columns.size(); i++) {
                IntrospectedColumn introspectedColumn = columns.get(i);
                methodName = JavaBeansUtil.getGetterMethodName(introspectedColumn.getJavaProperty(), introspectedColumn.getFullyQualifiedJavaType());
                if (i < columns.size() - 1) {
                    method.addBodyLine("        .set(" + AbstractMethodGenerator.calculateFieldName(tableFieldName, introspectedColumn) //$NON-NLS-1$
                            + ")." + propertyMethod + "(row::" + methodName //$NON-NLS-1$
                            + ")"); //$NON-NLS-1$
                } else {
                    method.addBodyLine("        .set(" + AbstractMethodGenerator.calculateFieldName(tableFieldName, introspectedColumn) //$NON-NLS-1$
                            + ")." + propertyMethod + "(row::" + methodName //$NON-NLS-1$
                            + ");"); //$NON-NLS-1$
                }
            }
        } else {
            method.addBodyLine("return c;");
        }
    }

}
