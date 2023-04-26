/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin;

import com.cutefool.mybatis.coder.plugin.coder.DynamicKeyJavaGenerator;
import com.cutefool.mybatis.coder.plugin.utils.SqlKeys;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.runtime.dynamic.sql.DynamicSqlMapperGenerator;

import java.util.List;

/**
 * 动态dsl生存器
 *
 * @author 271007729@qq.com
 * @date 2022/9/20 11:31 PM
 */
public class MyDynamicSqlMapperGenerator extends DynamicSqlMapperGenerator {

    public MyDynamicSqlMapperGenerator(String project) {
        super(project);
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        List<CompilationUnit> compilationUnits = super.getCompilationUnits();
        if (SqlKeys.newCreate(introspectedTable)) {
            compilationUnits.add(DynamicKeyJavaGenerator.generator(introspectedTable));
        }
        return compilationUnits;
    }
}
