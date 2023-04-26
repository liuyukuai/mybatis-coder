/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.coder;

import com.cutefool.mybatis.coder.plugin.utils.SqlDocs;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * @author 271007729@qq.com
 * @date 2022/9/21 11:18 AM
 */
public final class DynamicKeyJavaGenerator {

    private DynamicKeyJavaGenerator() {
    }

    public static TopLevelClass generator(IntrospectedTable introspectedTable) {

        // 创建类文件
        TopLevelClass topLevelClass = new TopLevelClass(introspectedTable.getPrimaryKeyType());
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addImportedType("com.cutefool.commons.orm.Keyable");
        topLevelClass.addAnnotation("@Data");

        SqlDocs.doClassDocs(topLevelClass, introspectedTable.getRemarks() + "主键");
        topLevelClass.addSuperInterface(new FullyQualifiedJavaType("com.cutefool.commons.orm.Keyable"));

        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();

        primaryKeyColumns.forEach(e -> {
            Field field = new Field(e.getJavaProperty(), e.getFullyQualifiedJavaType());
            field.setVisibility(JavaVisibility.PRIVATE);
            topLevelClass.addField(field);
        });
        return topLevelClass;

    }
}
