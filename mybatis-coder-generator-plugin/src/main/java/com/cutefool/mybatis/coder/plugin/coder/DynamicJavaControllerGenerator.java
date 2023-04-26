/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.coder;

import com.cutefool.commons.core.util.Lists;
import com.cutefool.mybatis.coder.plugin.constant.Constants;
import com.cutefool.mybatis.coder.plugin.utils.SqlDocs;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * 生成service
 *
 * @author 271007729@qq.com
 * @date 2022/9/21 11:18 AM
 */
public final class DynamicJavaControllerGenerator implements JavaGenerator {

    private DynamicJavaControllerGenerator() {
    }

    public static List<GeneratedJavaFile> generator(JavaGeneratorContext javaGeneratorContext) {
        List<GeneratedJavaFile> objects = Lists.newArrayList();
        GeneratedJavaFile instance = generatorController(javaGeneratorContext);
        if (!JavaGenerator.exists(instance)) {
            objects.add(instance);
        }
        return objects;
    }

    public static GeneratedJavaFile generatorController(JavaGeneratorContext javaGeneratorContext) {
        TopLevelClass controllerInstance = new TopLevelClass(javaGeneratorContext.getControllerInstance());
        controllerInstance.setVisibility(JavaVisibility.PUBLIC);
        controllerInstance.addAnnotation(Constants.REST_CONTROLLER);
        SqlDocs.doClassDocs(controllerInstance, javaGeneratorContext.getDomainRemarks() + "相关接口");
        Field field = addFields(javaGeneratorContext, controllerInstance);
        addMethods(javaGeneratorContext, controllerInstance, field);
        addControllerImports(javaGeneratorContext, controllerInstance);
        return JavaGenerator.javaFile(controllerInstance, javaGeneratorContext.getContext());
    }


    public static Field addFields(JavaGeneratorContext javaGeneratorContext, TopLevelClass topLevelClass) {
        FullyQualifiedJavaType serviceInterface = javaGeneratorContext.getServiceInterface();
        Field field = new Field(JavaGenerator.firstLower(serviceInterface.getShortName()), new FullyQualifiedJavaType(serviceInterface.getShortName()));
        field.setVisibility(JavaVisibility.PRIVATE);
        field.addAnnotation(Constants.RESOURCE);
        topLevelClass.addField(field);
        return field;
    }

    public static void addMethods(JavaGeneratorContext javaGeneratorContext, TopLevelClass topLevelClass, Field service) {
        doCreateMethods(javaGeneratorContext, topLevelClass, service);
        doUpdateMethods(javaGeneratorContext, topLevelClass, service);
        doDeleteMethods(javaGeneratorContext, topLevelClass, service);
        doByIdMethods(javaGeneratorContext, topLevelClass, service);
        doListMethods(javaGeneratorContext, topLevelClass, service);
        doPageMethods(javaGeneratorContext, topLevelClass, service);
    }

    public static void doCreateMethods(JavaGeneratorContext javaGeneratorContext, TopLevelClass topLevelClass, Field service) {
        Method create = new Method("create");
        create.setVisibility(JavaVisibility.PUBLIC);
        create.setReturnType(new FullyQualifiedJavaType("Response<Long>"));
        create.addAnnotation(String.format(Constants.POST_MAPPING, "/add/" + JavaGenerator.domainPaths(javaGeneratorContext)));
        Parameter parameter = new Parameter(JavaGenerator.dtoType(javaGeneratorContext), "dto");
        parameter.addAnnotation(Constants.REQUEST_BODY);
        parameter.addAnnotation(Constants.VALIDATE);
        create.addParameter(parameter);
        create.addBodyLine(javaGeneratorContext.getDomain() + " e = this." + service.getName() + ".create(dto);");
        create.addBodyLine("return Response.ok(e.getId());");
        SqlDocs.doMethodDocs(create, "新增" + javaGeneratorContext.getDomainRemarks(), parameter);
        topLevelClass.addMethod(create);


    }

    public static void doUpdateMethods(JavaGeneratorContext javaGeneratorContext, TopLevelClass topLevelClass, Field service) {
        Method update = new Method("update");
        update.setVisibility(JavaVisibility.PUBLIC);
        update.setReturnType(new FullyQualifiedJavaType("Response<Long>"));
        update.addAnnotation(String.format(Constants.POST_MAPPING, "/update/" + JavaGenerator.domainPaths(javaGeneratorContext) + "/{id}"));
        Parameter parameter = new Parameter(new FullyQualifiedJavaType(javaGeneratorContext.getPrimaryKey()), "id");
        parameter.addAnnotation(Constants.PATH_VARIABLE);
        update.addParameter(parameter);
        Parameter dtoParameter = new Parameter(JavaGenerator.dtoType(javaGeneratorContext), "dto");
        dtoParameter.addAnnotation(Constants.REQUEST_BODY);
        dtoParameter.addAnnotation(Constants.VALIDATE);
        update.addParameter(dtoParameter);
        update.addBodyLine(javaGeneratorContext.getDomain() + " e = this." + service.getName() + ".update(id, dto);");
        update.addBodyLine("return Response.ok(e.getId());");
        SqlDocs.doMethodDocs(update, "更新" + javaGeneratorContext.getDomainRemarks(), parameter, dtoParameter);
        topLevelClass.addMethod(update);
    }

    public static void doDeleteMethods(JavaGeneratorContext javaGeneratorContext, TopLevelClass topLevelClass, Field service) {
        Method delete = new Method("delete");
        delete.setVisibility(JavaVisibility.PUBLIC);
        delete.setReturnType(new FullyQualifiedJavaType("Response<Boolean>"));
        delete.addAnnotation(String.format(Constants.POST_MAPPING, "/delete/" + JavaGenerator.domainPaths(javaGeneratorContext) + "/{id}"));
        Parameter parameter = new Parameter(new FullyQualifiedJavaType(javaGeneratorContext.getPrimaryKey()), "id");
        parameter.addAnnotation(Constants.PATH_VARIABLE);
        delete.addParameter(parameter);
        delete.addBodyLine("this." + service.getName() + ".delete(id);");
        delete.addBodyLine("return Response.ok();");
        SqlDocs.doMethodDocs(delete, "删除" + javaGeneratorContext.getDomainRemarks(), parameter);
        topLevelClass.addMethod(delete);
    }


    public static void doByIdMethods(JavaGeneratorContext javaGeneratorContext, TopLevelClass topLevelClass, Field service) {
        Method byId = new Method("byId");
        byId.setVisibility(JavaVisibility.PUBLIC);
        byId.setReturnType(new FullyQualifiedJavaType("Response<" + javaGeneratorContext.getDomain() + ">"));
        byId.addAnnotation(String.format(Constants.GET_MAPPING, "/info/" + JavaGenerator.domainPaths(javaGeneratorContext) + "/{id}"));
        Parameter parameter = new Parameter(new FullyQualifiedJavaType(javaGeneratorContext.getPrimaryKey()), "id");
        parameter.addAnnotation(Constants.PATH_VARIABLE);
        byId.addParameter(parameter);
        byId.addBodyLine("Optional<" + javaGeneratorContext.getDomain() + "> optional = this." + service.getName() + ".getById(id);");
        byId.addBodyLine("return Response.ok(optional.orElse(null));");
        SqlDocs.doMethodDocs(byId, "查询" + javaGeneratorContext.getDomainRemarks() + "（通过ID）", parameter);
        topLevelClass.addMethod(byId);
    }

    public static void doListMethods(JavaGeneratorContext javaGeneratorContext, TopLevelClass topLevelClass, Field service) {
        String queryInterface = javaGeneratorContext.getQueryInterface();
        String domain = JavaGenerator.domain(queryInterface);
        Method list = new Method("list");
        list.setVisibility(JavaVisibility.PUBLIC);
        list.setReturnType(new FullyQualifiedJavaType("Response<List<" + javaGeneratorContext.getDomain() + ">>"));
        list.addAnnotation(String.format(Constants.GET_MAPPING, "/list/" + JavaGenerator.domainPaths(javaGeneratorContext)));
        Parameter parameter = new Parameter(new FullyQualifiedJavaType(domain), "query");
        list.addParameter(parameter);
        list.addBodyLine("List<" + javaGeneratorContext.getDomain() + "> entities = this." + service.getName() + ".listByWhere(query);");
        list.addBodyLine("return Response.ok(entities);");
        SqlDocs.doMethodDocs(list, "查询" + javaGeneratorContext.getDomainRemarks() + "（列表）", parameter);
        topLevelClass.addMethod(list);
    }

    public static void doPageMethods(JavaGeneratorContext javaGeneratorContext, TopLevelClass topLevelClass, Field service) {
        String queryInterface = javaGeneratorContext.getQueryInterface();
        String domain = JavaGenerator.domain(queryInterface);
        Method page = new Method("page");
        page.setVisibility(JavaVisibility.PUBLIC);
        page.setReturnType(new FullyQualifiedJavaType("Response<PageResponse<" + javaGeneratorContext.getDomain() + ">>"));
        page.addAnnotation(String.format(Constants.GET_MAPPING, "/page/" + JavaGenerator.domainPaths(javaGeneratorContext)));
        Parameter parameter = new Parameter(new FullyQualifiedJavaType(domain), "query");
        page.addParameter(parameter);
        Parameter paging = new Parameter(new FullyQualifiedJavaType("Paging"), "paging");
        page.addParameter(paging);
        page.addBodyLine("PageResponse<" + javaGeneratorContext.getDomain() + "> response = this." + service.getName() + ".listByWhere(query, paging);");
        page.addBodyLine("return Response.ok(response);");
        SqlDocs.doMethodDocs(page, "查询" + javaGeneratorContext.getDomainRemarks() + "（分页）", parameter, paging);
        topLevelClass.addMethod(page);
    }


    public static void addControllerImports(JavaGeneratorContext javaGeneratorContext, TopLevelClass topLevelClass) {
        String queryInterface = javaGeneratorContext.getQueryInterface();
        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.cutefool.commons.core.page.PageResponse"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.cutefool.commons.core.page.Response"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.cutefool.commons.core.page.Paging"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("javax.annotation.Resource"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.*"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.validation.annotation.Validated"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType(queryInterface));
        topLevelClass.addImportedType(javaGeneratorContext.getServiceInterface());
        topLevelClass.addImportedType(javaGeneratorContext.getDtoInstance());
        topLevelClass.addImportedType(new FullyQualifiedJavaType(javaGeneratorContext.getIntrospectedTable().getBaseRecordType()));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.List"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.Optional"));
    }

}
