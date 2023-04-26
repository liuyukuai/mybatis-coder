/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.utils;

import com.cutefool.commons.core.util.Lists;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.dom.java.AbstractJavaType;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;

/**
 * @author 271007729@qq.com
 * @date 2022/9/21 1:07 AM
 */
public final class SqlDocs {
    private SqlDocs() {
    }

    public static void doClassDocs(AbstractJavaType topLevelClass, String remarks) {
        // 添加注释
        topLevelClass.addJavaDocLine("/**");
        if (StringUtils.isNotBlank(remarks)) {
            topLevelClass.addJavaDocLine(" * " + remarks);
        }
        topLevelClass.addJavaDocLine(" *");
        topLevelClass.addJavaDocLine(" * @author : 271007729@qq.com");
        topLevelClass.addJavaDocLine(" */");
    }

    public static void doFieldDocs(Field field, String remarks) {
        // 添加注释
        field.addJavaDocLine("/**");
        field.addJavaDocLine(" * " + remarks);
        field.addJavaDocLine(" **/");
    }

    public static void doMethodDocs(Method method, String remarks, Parameter... parameters) {
        // 添加注释
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * " + remarks);
        method.addJavaDocLine(" * ");
        if (Lists.iterable(parameters)) {
            for (Parameter parameter : parameters) {
                method.addJavaDocLine(" * @param " + parameter.getName() + "\t " + parameter.getName());
            }
        }
        if (method.getReturnType().isPresent()) {
            method.addJavaDocLine(" * @return " + method.getReturnType().get());
        }
        method.addJavaDocLine(" **/");
    }

}
