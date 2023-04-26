/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.utils;

import com.cutefool.commons.core.util.Lists;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;

import java.util.List;

import static com.cutefool.mybatis.coder.plugin.utils.SqlMappers.toUpperCaseFirstOne;

/**
 * @author 271007729@qq.com
 * @date 2022/9/21 9:42 AM
 */
public final class SqlKeys {
    private SqlKeys() {
    }

    public static String primaryKey(IntrospectedTable introspectedTable) {
        // key
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        // 如果没有主键
        if (Lists.isEmpty(primaryKeyColumns)) {
            return "Long";
        }

        int size = primaryKeyColumns.size();
        if (size == 1) {
            return primaryKeyColumns.get(0).getFullyQualifiedJavaType().getShortName();
        }
        return introspectedTable.getPrimaryKeyType();
    }

    public static boolean newCreate(IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        return Lists.empty(primaryKeyColumns).size() > 1;
    }

    public static String condition(IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        StringBuilder sb = new StringBuilder();
        sb.append(" where.or(");
        for (int i = 0; i < primaryKeyColumns.size(); i++) {
            IntrospectedColumn primaryKeyColumn = primaryKeyColumns.get(0);
            String javaProperty = primaryKeyColumn.getJavaProperty();
            if (i == 0) {
                sb.append(javaProperty)
                  .append(", ")
                  .append("isEqualTo(e.get")
                  .append(toUpperCaseFirstOne(javaProperty))
                  .append("()),");
                continue;
            }
            sb.append(" and(")
              .append(javaProperty)
              .append(", isEqualTo(e.get")
              .append(toUpperCaseFirstOne(javaProperty))
              .append("())))");
        }
        return sb.toString();
    }

}
