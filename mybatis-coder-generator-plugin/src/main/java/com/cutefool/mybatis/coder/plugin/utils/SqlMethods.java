/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.utils;

import com.cutefool.commons.core.util.Lists;
import com.cutefool.mybatis.coder.plugin.coder.JavaGenerator;
import com.cutefool.mybatis.coder.plugin.constant.Constants;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.runtime.dynamic.sql.elements.AbstractMethodGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 271007729@qq.com
 * @date 2022/9/21 9:42 AM
 */
@SuppressWarnings("ALL")
public final class SqlMethods {
    private SqlMethods() {
    }

    public static void clearParameter(Method method) {
        List<Parameter> parameters = method.getParameters();
        if (Lists.iterable(parameters)) {
            parameters.clear();
        }
    }

    public static void clearBody(Method method) {
        List<String> bodyLines = method.getBodyLines();
        if (Lists.iterable(bodyLines)) {
            bodyLines.clear();
        }
    }


    public static void addSelectMultiplePrimaryKeyMethods(Interface interfaces, IntrospectedTable introspectedTable) {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        if (SqlKeys.newCreate(introspectedTable)) {
            // 添加获取所有列的方法
            Method multiple = new Method("selectByPrimaryKey");
            multiple.setReturnType(new FullyQualifiedJavaType("List<" + domainObjectName + ">"));
            multiple.addParameter(new Parameter(new FullyQualifiedJavaType("Collection<" + SqlKeys.primaryKey(introspectedTable) + ">"), "ids"));
            multiple.addAnnotation(Constants.OVERRIDE);
            multiple.setDefault(true);
            multiple.addBodyLine("  if (Lists.isEmpty(ids)) {");
            multiple.addBodyLine("  return Lists.newArrayList();");
            multiple.addBodyLine("  }");
            multiple.addBodyLine("  return select(this.newSelect((c, w) -> {");
            multiple.addBodyLine("    QueryExpressionDSL<org.mybatis.dynamic.sql.select.SelectModel>.QueryExpressionWhereBuilder where = c.where();");
            multiple.addBodyLine("    ids.forEach(e -> ");
            multiple.addBodyLine(SqlKeys.condition(introspectedTable));
            multiple.addBodyLine(" );");
            multiple.addBodyLine("  return c;");
            multiple.addBodyLine(" }));");
            interfaces.addMethod(multiple);
            // 增加引用
            interfaces.addImportedType(new FullyQualifiedJavaType("com.cutefool.commons.core.util.Lists"));
            interfaces.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.QueryExpressionDSL"));
            interfaces.addStaticImport("org.mybatis.dynamic.sql.SqlBuilder.and");
            interfaces.addStaticImport("org.mybatis.dynamic.sql.SqlBuilder.or");
            return;
        }

        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();

        if (Lists.iterable(primaryKeyColumns)) {
            IntrospectedColumn introspectedColumn = primaryKeyColumns.get(0);
            // 添加获取所有列的方法
            Method multiple = new Method("selectByPrimaryKey");
            multiple.setReturnType(new FullyQualifiedJavaType("List<" + domainObjectName + ">"));
            multiple.addParameter(new Parameter(new FullyQualifiedJavaType("Collection<" + SqlKeys.primaryKey(introspectedTable) + ">"), "ids"));
            multiple.addAnnotation(Constants.OVERRIDE);
            multiple.setDefault(true);
            multiple.addBodyLine("return this.select(this.newSelect((dsl, where) -> dsl.where(" + introspectedColumn.getJavaProperty() + ", isIn(ids))));");
            interfaces.addMethod(multiple);
            interfaces.addStaticImport("org.mybatis.dynamic.sql.SqlBuilder.isIn");
        }
    }

    public static void addMultipleDeletePrimaryKeyMethods(Interface interfaces, IntrospectedTable introspectedTable) {
        if (SqlKeys.newCreate(introspectedTable)) {
            // 添加获取所有列的方法
            Method multiple = new Method("deleteByPrimaryKey");
            multiple.setReturnType(new FullyQualifiedJavaType("int"));
            multiple.addParameter(new Parameter(new FullyQualifiedJavaType("Collection<" + SqlKeys.primaryKey(introspectedTable) + ">"), "ids"));
            multiple.addAnnotation(Constants.OVERRIDE);
            multiple.setDefault(true);
            multiple.addBodyLine("  if (Lists.isEmpty(ids)) {");
            multiple.addBodyLine("  return 0;");
            multiple.addBodyLine("  }");
            multiple.addBodyLine("  return delete(this.newDelete((c, w) -> {");
            multiple.addBodyLine("    DeleteDSL<org.mybatis.dynamic.sql.delete.DeleteModel>.DeleteWhereBuilder where = c.where();");
            multiple.addBodyLine("    ids.forEach(e -> ");
            multiple.addBodyLine(SqlKeys.condition(introspectedTable));
            multiple.addBodyLine(" );");
            multiple.addBodyLine("  return c;");
            multiple.addBodyLine(" }));");
            interfaces.addMethod(multiple);
            // 增加引用
            interfaces.addImportedType(new FullyQualifiedJavaType("com.cutefool.commons.core.util.Lists"));
            interfaces.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.delete.DeleteDSL"));
            interfaces.addStaticImport("org.mybatis.dynamic.sql.SqlBuilder.and");
            interfaces.addStaticImport("org.mybatis.dynamic.sql.SqlBuilder.or");
            return;
        }

        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();

        if (Lists.iterable(primaryKeyColumns)) {
            IntrospectedColumn introspectedColumn = primaryKeyColumns.get(0);
            // 添加获取所有列的方法
            Method multiple = new Method("deleteByPrimaryKey");
            multiple.setReturnType(new FullyQualifiedJavaType("int"));
            multiple.addParameter(new Parameter(new FullyQualifiedJavaType("Collection<" + SqlKeys.primaryKey(introspectedTable) + ">"), "ids"));
            multiple.addAnnotation(Constants.OVERRIDE);
            multiple.setDefault(true);
            multiple.addBodyLine("return this.delete(this.newDelete((c, where) -> c.where(" + introspectedColumn.getJavaProperty() + ", isIn(ids))));");
            interfaces.addMethod(multiple);
            interfaces.addStaticImport("org.mybatis.dynamic.sql.SqlBuilder.isIn");
        }
    }

    public static void addTable(Interface interfaces, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getTableConfiguration().getTableName();
        Method table = new Method("table");
        table.addBodyLine("return \"" + tableName + "\";");
        table.addAnnotation("@Override");
        table.setReturnType(new FullyQualifiedJavaType("String"));
        table.setDefault(true);
        interfaces.addMethod(table);
        interfaces.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.AliasableSqlTable"));
    }


    public static void addSqlTabl(Interface interfaces, IntrospectedTable introspectedTable) {
        String fieldName = JavaBeansUtil.getValidPropertyName(introspectedTable.getMyBatisDynamicSQLTableObjectName());
        Method sqlTable = new Method("sqlTable");
        sqlTable.addBodyLine("return " + fieldName + ";");
        sqlTable.addAnnotation("@Override");
        sqlTable.setReturnType(new FullyQualifiedJavaType("AliasableSqlTable<?>"));
        sqlTable.setDefault(true);
        interfaces.addMethod(sqlTable);

    }

    public static void addIdColumn(Interface interfaces, IntrospectedTable introspectedTable) {
        Method sqlTable = new Method("idColumn");
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        String s = SqlKeys.primaryKey(introspectedTable);
        sqlTable.addAnnotation("@Override");
        sqlTable.setDefault(true);
        sqlTable.setReturnType(new FullyQualifiedJavaType("SqlColumn<" + s + ">"));
        if (Lists.isEmpty(primaryKeyColumns) || primaryKeyColumns.size() > 1) {
            interfaces.addImportedType(new FullyQualifiedJavaType("com.cutefool.commons.core.BizException"));
            sqlTable.addBodyLine("throw new BizException(\"Multiple Primary Key is not support\");");
        } else {
            sqlTable.addBodyLine("return " + primaryKeyColumns.get(0).getJavaProperty() + ";");
        }
        interfaces.addMethod(sqlTable);
    }

    public static void addRemarks(Interface interfaces, IntrospectedTable introspectedTable) {
        String remarks = JavaGenerator.remarks(introspectedTable.getRemarks());
        Method table = new Method("remarks");
        table.addBodyLine("return \"" + remarks + "\";");
        table.addAnnotation("@Override");
        table.setReturnType(new FullyQualifiedJavaType("String"));
        table.setDefault(true);
        interfaces.addMethod(table);
    }

    public static void addInsertWithFields(Interface interfaces, IntrospectedTable introspectedTable) {
        interfaces.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.insert.InsertDSL"));
        interfaces.addImportedType(new FullyQualifiedJavaType("java.util.*"));
        interfaces.addImportedType(new FullyQualifiedJavaType("java.util.function.Consumer"));
        Method method = new Method("insert"); //$NON-NLS-1$
        method.setDefault(true);
        method.addAnnotation("@Override");
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        method.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName), "row"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Consumer<InsertDSL<" + domainObjectName + ">>"), "consumer"));
        String tableFieldName = JavaBeansUtil.getValidPropertyName(introspectedTable.getMyBatisDynamicSQLTableObjectName());
        method.addBodyLine("return MyBatis3Utils.insert(this::insert, row, " + tableFieldName //$NON-NLS-1$
                + ", c -> {"); //$NON-NLS-1$
        method.addBodyLine("InsertDSL<" + domainObjectName + "> insertDsl = this.doInsertDsl(c);");
        method.addBodyLine("if (Objects.nonNull(consumer)) {");
        method.addBodyLine("consumer.accept(insertDsl);");
        method.addBodyLine("}");
        method.addBodyLine("return insertDsl;");
        method.addBodyLine("});");

        interfaces.addMethod(method);
    }


    public static void addInsertMultipleWithFields(Interface interfaces, IntrospectedTable introspectedTable) {
        interfaces.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.insert.MultiRowInsertDSL"));
        interfaces.addImportedType(new FullyQualifiedJavaType("java.util.*"));
        interfaces.addImportedType(new FullyQualifiedJavaType("java.util.function.Consumer"));
        Method method = new Method("insertMultiple"); //$NON-NLS-1$
        method.setDefault(true);
        method.addAnnotation("@Override");
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Collection<" + domainObjectName + ">"), "records"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Consumer<MultiRowInsertDSL<" + domainObjectName + ">>"), "consumer"));
        String tableFieldName = JavaBeansUtil.getValidPropertyName(introspectedTable.getMyBatisDynamicSQLTableObjectName());
        method.addBodyLine("return MyBatis3Utils.insertMultiple(this::insertMultiple, records, " + tableFieldName //$NON-NLS-1$
                + ", c -> {"); //$NON-NLS-1$
        method.addBodyLine("MultiRowInsertDSL<" + domainObjectName + "> insertDsl = this.doInsertMultipleDsl(c);");
        method.addBodyLine("if (Objects.nonNull(consumer)) {");
        method.addBodyLine("consumer.accept(insertDsl);");
        method.addBodyLine("}");
        method.addBodyLine("return insertDsl;");
        method.addBodyLine("});");
        interfaces.addMethod(method);
    }


    public static void addInsertSelectiveWithFields(Interface interfaces, IntrospectedTable introspectedTable) {
        interfaces.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.insert.InsertDSL"));
        interfaces.addImportedType(new FullyQualifiedJavaType("java.util.*"));
        interfaces.addImportedType(new FullyQualifiedJavaType("java.util.function.Consumer"));
        Method method = new Method("insertSelective"); //$NON-NLS-1$
        method.setDefault(true);
        method.addAnnotation("@Override");
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        method.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName), "row"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Consumer<InsertDSL<" + domainObjectName + ">>"), "consumer"));
        String tableFieldName = JavaBeansUtil.getValidPropertyName(introspectedTable.getMyBatisDynamicSQLTableObjectName());
        method.addBodyLine("return MyBatis3Utils.insert(this::insert, row, " + tableFieldName //$NON-NLS-1$
                + ", c -> {"); //$NON-NLS-1$

        method.addBodyLine("InsertDSL<" + domainObjectName + "> insertDsl = this.doInsertSelectiveDsl(row, c);");
        method.addBodyLine("if (Objects.nonNull(consumer)) {");
        method.addBodyLine("consumer.accept(insertDsl);");
        method.addBodyLine("}");
        method.addBodyLine("return insertDsl;");
        method.addBodyLine("});");
        interfaces.addMethod(method);
    }

    public static void addNewPrimaryKey(Interface interfaces, IntrospectedTable introspectedTable) {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        interfaces.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.where.AbstractWhereDSL"));
        Method method = new Method("newPrimaryKey"); //$NON-NLS-1$
        method.setDefault(true);
        method.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName), "row"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType(" AbstractWhereDSL<?>"), "where"));
        method.addBodyLines(doKeyWhere(introspectedTable));
        interfaces.addMethod(method);
    }


    public static List<String> doKeyWhere(IntrospectedTable introspectedTable) {
        List<String> lines = new ArrayList<>();
        String tableFieldName = JavaBeansUtil.getValidPropertyName(introspectedTable.getMyBatisDynamicSQLTableObjectName());
        boolean first = true;
        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            String fieldName = AbstractMethodGenerator.calculateFieldName(tableFieldName, column);
            String methodName = JavaBeansUtil.getGetterMethodName(
                    column.getJavaProperty(), column.getFullyQualifiedJavaType());
            if (first) {
                lines.add("where.and(" + fieldName //$NON-NLS-1$
                        + ", isEqualTo(row::" + methodName //$NON-NLS-1$
                        + "));"); //$NON-NLS-1$
                first = false;
            } else {
                lines.add("where.and(" + fieldName //$NON-NLS-1$
                        + ", isEqualTo(row::" + methodName //$NON-NLS-1$
                        + "));"); //$NON-NLS-1$
            }
        }

        return lines;
    }


    public static void addSelectList(Interface interfaces, IntrospectedTable introspectedTable) {
        interfaces.addImportedType(new FullyQualifiedJavaType("com.cutefool.commons.core.util.Maps"));
        interfaces.addImportedType(new FullyQualifiedJavaType("java.util.concurrent.atomic.AtomicInteger"));
        String fieldName = JavaBeansUtil.getValidPropertyName(introspectedTable.getMyBatisDynamicSQLTableObjectName());
        Method sqlTable = new Method("selectList");
        sqlTable.addBodyLine("Map<String, String> extensibleFields = this.extensibleFields();");
        sqlTable.addBodyLine("if (Maps.isEmpty(extensibleFields)) {");
        sqlTable.addBodyLine("return selectList;");
        sqlTable.addBodyLine("}");
        sqlTable.addBodyLine("BasicColumn[] columns = new BasicColumn[extensibleFields.size() + selectList.length];");
        sqlTable.addBodyLine("AtomicInteger index = new AtomicInteger(0);");
        sqlTable.addBodyLine("for (BasicColumn basicColumn : selectList) {");
        sqlTable.addBodyLine("columns[index.getAndIncrement()] = basicColumn;");
        sqlTable.addBodyLine("}");
        sqlTable.addBodyLine("Set<Map.Entry<String, String>> entries = extensibleFields.entrySet();");
        sqlTable.addBodyLine("for (Map.Entry<String, String> entry : entries) {");
        sqlTable.addBodyLine("columns[index.getAndIncrement()] = SqlColumn.of(entry.getValue(), this.sqlTable());");
        sqlTable.addBodyLine("}");
        sqlTable.addBodyLine("return columns;");
        sqlTable.addAnnotation("@Override");
        sqlTable.setReturnType(new FullyQualifiedJavaType(" BasicColumn[]"));
        sqlTable.setDefault(true);
        interfaces.addMethod(sqlTable);
    }

    public static void addUpdateAllColumnsDsl(Interface interfaces, IntrospectedTable introspectedTable) {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        Method sqlTable = new Method("updateAllColumnsDsl");
        sqlTable.setDefault(true);
        sqlTable.addAnnotation("@Override");
        sqlTable.setReturnType(new FullyQualifiedJavaType("UpdateDSL<UpdateModel>"));
        sqlTable.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName), "row"));
        sqlTable.addParameter(new Parameter(new FullyQualifiedJavaType("UpdateDSL<UpdateModel>"), "dsl"));
        sqlTable.setReturnType(new FullyQualifiedJavaType("UpdateDSL<UpdateModel>"));
        sqlTable.addBodyLine("return updateAllColumns(row, dsl);");
        interfaces.addMethod(sqlTable);
    }

    public static void addUpdateSelectiveColumnsDsl(Interface interfaces, IntrospectedTable introspectedTable) {
        String domainObjectName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        Method sqlTable = new Method("updateSelectiveColumnsDsl");
        sqlTable.setDefault(true);
        sqlTable.addAnnotation("@Override");
        sqlTable.setReturnType(new FullyQualifiedJavaType("UpdateDSL<UpdateModel>"));
        sqlTable.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName), "row"));
        sqlTable.addParameter(new Parameter(new FullyQualifiedJavaType("UpdateDSL<UpdateModel>"), "dsl"));
        sqlTable.addBodyLine("return updateSelectiveColumns(row, dsl);");
        interfaces.addMethod(sqlTable);
    }

}
