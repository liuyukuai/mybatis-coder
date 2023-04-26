/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.coder;

import com.cutefool.commons.core.util.Lists;
import com.cutefool.mybatis.coder.plugin.constant.Constants;
import com.cutefool.mybatis.coder.plugin.utils.SqlDocs;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * 生成service
 *
 * @author 271007729@qq.com
 * @date 2022/9/21 11:18 AM
 */
public final class DynamicJavaServiceGenerator implements JavaGenerator {

    private DynamicJavaServiceGenerator() {
    }

    public static List<GeneratedJavaFile> generator(JavaGeneratorContext javaGeneratorContext) {
        List<GeneratedJavaFile> objects = Lists.newArrayList();
        GeneratedJavaFile e = generatorInterface(javaGeneratorContext);
        if (!JavaGenerator.exists(e)) {
            objects.add(e);
        }
        GeneratedJavaFile instance = generatorService(javaGeneratorContext);
        if (!JavaGenerator.exists(instance)) {
            objects.add(instance);
        }
        return objects;
    }

    public static GeneratedJavaFile generatorInterface(JavaGeneratorContext javaGeneratorContext) {
        String domain = javaGeneratorContext.getDomain();
        String primaryKey = javaGeneratorContext.getPrimaryKey();
        String serviceInterfaceCls_ = javaGeneratorContext.getServiceInterfaceCls_();
        String interfaceDomain = JavaGenerator.domain(serviceInterfaceCls_);
        String queryDomain = JavaGenerator.domain(javaGeneratorContext.getQueryInterface());

        // 名称
        FullyQualifiedJavaType service = javaGeneratorContext.getServiceInterface();
        Interface serviceInterface = new Interface(service);
        SqlDocs.doClassDocs(serviceInterface, javaGeneratorContext.getDomainRemarks() + "业务层接口");
        serviceInterface.setVisibility(JavaVisibility.PUBLIC);
        serviceInterface.addSuperInterface(new FullyQualifiedJavaType(interfaceDomain + "<" + javaGeneratorContext.getDto() + ", " + domain + ", " + primaryKey + ", " + javaGeneratorContext.getMapper() + ", " + queryDomain + ">"));
        addInterfaceImports(javaGeneratorContext, serviceInterface);
        return JavaGenerator.javaFile(serviceInterface, javaGeneratorContext.getContext());
    }

    public static GeneratedJavaFile generatorService(JavaGeneratorContext javaGeneratorContext) {
        String domain = javaGeneratorContext.getDomain();
        String primaryKey = javaGeneratorContext.getPrimaryKey();
        String serviceImplCls_ = javaGeneratorContext.getServiceImplCls_();
        String interfaceDomain = JavaGenerator.domain(serviceImplCls_);
        String queryDomain = JavaGenerator.domain(javaGeneratorContext.getQueryInterface());
        // 名称
        FullyQualifiedJavaType service = javaGeneratorContext.getServiceInstance();
        TopLevelClass serviceInstance = new TopLevelClass(service);
        SqlDocs.doClassDocs(serviceInstance, javaGeneratorContext.getDomainRemarks() + "业务层");
        serviceInstance.setVisibility(JavaVisibility.PUBLIC);
        // 设置注解
        serviceInstance.addAnnotation(Constants.SERVICE);
        serviceInstance.setSuperClass(new FullyQualifiedJavaType(interfaceDomain + "<" + javaGeneratorContext.getDto() + ", " + domain + ", " + primaryKey + ", " + javaGeneratorContext.getMapper() + ", " + queryDomain + ">"));
        serviceInstance.addSuperInterface(javaGeneratorContext.getServiceInterface());
        addInterfaceImports(javaGeneratorContext, serviceInstance);
        return JavaGenerator.javaFile(serviceInstance, javaGeneratorContext.getContext());
    }


    public static void addInterfaceImports(JavaGeneratorContext javaGeneratorContext, Interface interfaceCls) {
        IntrospectedTable introspectedTable = javaGeneratorContext.getIntrospectedTable();
        String serviceInterfaceCls_ = javaGeneratorContext.getServiceInterfaceCls_();
        String queryInterface = javaGeneratorContext.getQueryInterface();
        interfaceCls.addImportedType(new FullyQualifiedJavaType(serviceInterfaceCls_));
        interfaceCls.addImportedType(new FullyQualifiedJavaType(queryInterface));
        interfaceCls.addImportedType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        interfaceCls.addImportedType(javaGeneratorContext.getDtoInstance());
        interfaceCls.addImportedType(new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType()));
    }

    public static void addInterfaceImports(JavaGeneratorContext javaGeneratorContext, TopLevelClass topLevelClass) {
        IntrospectedTable introspectedTable = javaGeneratorContext.getIntrospectedTable();
        String serviceImplCls_ = javaGeneratorContext.getServiceImplCls_();
        String queryInterface = javaGeneratorContext.getQueryInterface();
        topLevelClass.addImportedType(new FullyQualifiedJavaType(serviceImplCls_));
        topLevelClass.addImportedType(new FullyQualifiedJavaType(queryInterface));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
        topLevelClass.addImportedType(javaGeneratorContext.getDtoInstance());
        topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType()));
    }

}
