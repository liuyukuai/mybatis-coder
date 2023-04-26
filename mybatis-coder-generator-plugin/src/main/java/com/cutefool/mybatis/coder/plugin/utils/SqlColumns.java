/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.utils;

import com.cutefool.commons.core.util.Lists;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 271007729@qq.com
 * @date 2022/9/21 1:07 AM
 */
public final class SqlColumns {
    private SqlColumns() {
    }

    public static List<Field> fields(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String fieldName = JavaBeansUtil.getValidPropertyName(introspectedTable.getMyBatisDynamicSQLTableObjectName());
        return Lists.empty(topLevelClass.getFields())
                    .stream()
                    .filter(e -> !Objects.equals(e.getName(), fieldName))
                    .collect(Collectors.toList());
    }
}
