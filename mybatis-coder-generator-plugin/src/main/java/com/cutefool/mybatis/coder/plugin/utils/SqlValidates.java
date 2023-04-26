/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.utils;

import com.cutefool.mybatis.coder.plugin.coder.JavaGenerator;
import com.cutefool.mybatis.coder.plugin.sqlserver.SqlServers;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.Objects;

/**
 * @author 271007729@qq.com
 * @date 2022/9/21 9:42 AM
 */
@SuppressWarnings("ALL")
public final class SqlValidates {
    private SqlValidates() {
    }

    public static void addValidate(String validate, Field field, TopLevelClass topLevelClass, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        SqlServers.setRemarks(introspectedTable, introspectedColumn);

        if (!Objects.isNull(validate) && !validate.isEmpty()) {

            // 生成校验规则
            String javaProperty = introspectedColumn.getJavaProperty();
            boolean nullable = introspectedColumn.isNullable();
            int length = introspectedColumn.getLength();
            int scale = introspectedColumn.getScale();
            String remarks = JavaGenerator.remarks(introspectedColumn.getRemarks());

            if (!Objects.equals("id", javaProperty)) {
                // 不能为空

                String jdbcTypeName = introspectedColumn.getJdbcTypeName();


                if (Objects.equals(jdbcTypeName, "BIGINT") || Objects.equals(jdbcTypeName, "INTEGER")) {
                    if (!nullable) {
                        field.addAnnotation("@NotNull(message = \"" + remarks + "不能为空\")");
                        topLevelClass.addImportedType("javax.validation.constraints.NotNull");
                    }
                    field.addAnnotation("@Digits(integer = " + length + ", fraction = " + scale + ", message = \"" + remarks + "只能为数字\")");
                    topLevelClass.addImportedType("javax.validation.constraints.Digits");
                }

                if (Objects.equals("DECIMAL", jdbcTypeName)) {
                    if (!nullable) {
                        field.addAnnotation("@NotEmpty(message = \"" + remarks + "不能为空\")");
                        topLevelClass.addImportedType("javax.validation.constraints.NotEmpty");
                    }
                    field.addAnnotation("@Digits(integer = " + length + ", fraction = " + scale + ", message = \"" + remarks + "只能为数字\")");
                    topLevelClass.addImportedType("javax.validation.constraints.Digits");

                }

                if (Objects.equals("VARCHAR", jdbcTypeName)) {
                    if (!nullable) {
                        field.addAnnotation("@NotBlank(message = \"" + remarks + "不能为空\")");
                        topLevelClass.addImportedType("javax.validation.constraints.NotBlank");
                    }
                    field.addAnnotation("@Size(max = " + length + ", message = \"" + remarks + "不能超过" + length + "位\")");
                    topLevelClass.addImportedType("javax.validation.constraints.Size");

                }
                if (Objects.equals("DATE", jdbcTypeName)) {
                    if (!nullable) {
                        field.addAnnotation("@NotNull(message = \"" + remarks + "不能为空\")");
                        topLevelClass.addImportedType("javax.validation.constraints.NotNull");
                    }
                    field.addAnnotation("@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_PATTERN, timezone = \"GMT+8\")");
                    field.addAnnotation("@DateTimeFormat(pattern = Constants.DATE_PATTERN)");
                    topLevelClass.addImportedType("org.springframework.format.annotation.DateTimeFormat");
                    topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonFormat");
                    topLevelClass.addImportedType("com.cutefool.commons.core.Constants");
                }

                if (Objects.equals("DATETIME", jdbcTypeName) || Objects.equals("TIMESTAMP", jdbcTypeName)) {
                    if (!nullable) {
                        field.addAnnotation("@NotNull(message = \"" + remarks + "不能为空\")");
                        topLevelClass.addImportedType("javax.validation.constraints.NotNull");
                    }
                    field.addAnnotation("@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_PATTERN, timezone = \"GMT+8\")");
                    field.addAnnotation("@DateTimeFormat(pattern = Constants.DATE_TIME_PATTERN)");
                    topLevelClass.addImportedType("com.fasterxml.jackson.annotation.JsonFormat");
                    topLevelClass.addImportedType("org.springframework.format.annotation.DateTimeFormat");
                    topLevelClass.addImportedType("com.cutefool.commons.core.Constants");

                }
            }
        }
    }

}
