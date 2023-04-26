/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.coder;

import com.cutefool.commons.core.util.Lists;
import com.cutefool.commons.orm.DtoExclude;
import com.cutefool.mybatis.coder.plugin.utils.SqlDocs;
import com.cutefool.mybatis.coder.plugin.utils.SqlValidates;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.Collections;
import java.util.List;

/**
 * 生成service
 *
 * @author 271007729@qq.com
 * @date 2022/9/21 11:18 AM
 */
@Slf4j
public final class DynamicJavaDtoGenerator implements JavaGenerator {

    private DynamicJavaDtoGenerator() {
    }

    public static List<GeneratedJavaFile> generator(JavaGeneratorContext javaGeneratorContext) {
        FullyQualifiedJavaType dtoInstance = javaGeneratorContext.getDtoInstance();
        TopLevelClass doClass = new TopLevelClass(dtoInstance);
        doClass.addAnnotation("@Data");
        doClass.setSuperClass(new FullyQualifiedJavaType("Extensible"));
        doClass.setVisibility(JavaVisibility.PUBLIC);
        // 设置注释
        SqlDocs.doClassDocs(doClass, javaGeneratorContext.getDomainRemarks() + "数据对象");

        List<IntrospectedColumn> columns = javaGeneratorContext.getIntrospectedTable().getBaseColumns();

        // 需要排除的属性
        List<String> excludes = JavaGenerator.excludes(javaGeneratorContext.getContext(), DtoExclude.class);

        Lists.empty(columns)
                .stream()
                .filter(e -> Lists.isEmpty(excludes) || !excludes.contains(e.getJavaProperty()))
                .forEach(e -> {
                    Field field = new Field(e.getJavaProperty(), e.getFullyQualifiedJavaType());
                    field.setVisibility(JavaVisibility.PRIVATE);
                    SqlDocs.doFieldDocs(field, e.getRemarks());
                    // 设置校验信息
                    SqlValidates.addValidate("true", field, doClass, javaGeneratorContext.getIntrospectedTable(), e);
                    doClass.addField(field);
                });
        addDtoImports(doClass);

        GeneratedJavaFile javaFile = JavaGenerator.javaFile(doClass, javaGeneratorContext.getContext());

        if (!JavaGenerator.exists(javaFile)) {
            // 查询所有属性
            return Collections.singletonList(javaFile);
        }
        // 查询所有属性
        return Collections.emptyList();
    }

    public static void addDtoImports(TopLevelClass topLevelClass) {
        topLevelClass.addImportedType(new FullyQualifiedJavaType("lombok.Data"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.cutefool.commons.orm.Extensible"));
    }

}
