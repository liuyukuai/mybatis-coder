/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin;

import com.cutefool.mybatis.coder.plugin.coder.DynamicJavaGenerator;
import com.cutefool.mybatis.coder.plugin.coder.JavaGeneratorContext;
import com.cutefool.mybatis.coder.plugin.utils.*;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;
import java.util.Set;

/**
 * @author liuyk@tsingyun.net
 * @date 2022/9/21 12:12 AM
 */
public class MyDynamicSqlPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String type = introspectedTable.getMyBatisDynamicSqlSupportType();
        introspectedTable.setMyBatisDynamicSqlSupportType(type.replace("DynamicSqlSupport", "Dsl"));
        super.initialized(introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaces, IntrospectedTable introspectedTable) {
        SqlMappers.init(interfaces, introspectedTable, properties);
        // 修改引用
        Set<FullyQualifiedJavaType> importedTypes = interfaces.getImportedTypes();
        importedTypes.remove(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.delete.DeleteDSLCompleter"));
        importedTypes.remove(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.CountDSLCompleter"));
        importedTypes.remove(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.SelectDSLCompleter"));
        importedTypes.remove(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.update.UpdateDSLCompleter"));
        return true;
    }

    @Override
    public boolean dynamicSqlSupportGenerated(TopLevelClass supportClass, IntrospectedTable introspectedTable) {
        SqlSupports.dynamicSqlSupportGenerated(this.context, supportClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        String validate = properties.getProperty("validate", "true");
        // 增加校验信息
        SqlValidates.addValidate(validate, field, topLevelClass, introspectedTable, introspectedColumn);
        // 增加excel导出信息
        SqlExcels.addExecle(field, topLevelClass, introspectedTable, introspectedColumn);
        // 增加数据填充
        SqlBulks.doBulked(field, topLevelClass, introspectedTable, introspectedColumn);
        // 增加数据转换信息
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }


    @Override
    public boolean clientGeneralDeleteMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // 修改参数类型
        SqlMethods.clearParameter(method);
        // 修改参数类型
        method.addParameter(new Parameter(new FullyQualifiedJavaType(this.dslPackage() + ".DeleteCompleter"), "completer"));
        return true;
    }

    @Override
    public boolean clientGeneralCountMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // 修改参数类型
        SqlMethods.clearParameter(method);
        // 修改参数类型
        method.addParameter(new Parameter(new FullyQualifiedJavaType(this.dslPackage() + ".CountCompleter"), "completer"));
        return true;
    }

    @Override
    public boolean clientGeneralUpdateMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // 修改参数类型
        SqlMethods.clearParameter(method);
        // 修改参数类型
        method.addParameter(new Parameter(new FullyQualifiedJavaType(this.dslPackage() + ".UpdateCompleter"), "completer"));
        return true;
    }

    @Override
    public boolean clientGeneralSelectMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // 修改参数类型
        SqlMethods.clearParameter(method);
        // 修改参数类型
        method.addParameter(new Parameter(new FullyQualifiedJavaType(this.dslPackage() + ".SelectCompleter"), "completer"));
        // 修改方法体
        List<String> bodyLines = method.getBodyLines();
        bodyLines.set(0, bodyLines.get(0).replace("selectList,", "selectList(),"));
        return true;
    }

    @Override
    public boolean clientSelectOneMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // 修改参数类型
        SqlMethods.clearParameter(method);
        // 修改参数类型
        method.addParameter(new Parameter(new FullyQualifiedJavaType(this.dslPackage() + ".SelectCompleter"), "completer"));
        // 修改方法体
        List<String> bodyLines = method.getBodyLines();
        bodyLines.set(0, bodyLines.get(0).replace("selectList,", "selectList(),"));
        return true;
    }

    @Override
    public boolean clientGeneralSelectDistinctMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        // 修改参数类型
        SqlMethods.clearParameter(method);
        // 修改参数类型
        method.addParameter(new Parameter(new FullyQualifiedJavaType(this.dslPackage() + ".SelectCompleter"), "completer"));
        // 修改方法体
        List<String> bodyLines = method.getBodyLines();
        bodyLines.set(0, bodyLines.get(0).replace("selectList,", "selectList(),"));
        return true;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        List<String> bodyLines = method.getBodyLines();
        bodyLines.set(0, "return selectOne(this.newSelect((c, where) -> ");
        bodyLines.set(bodyLines.size() - 1, "));");
        return true;
    }


    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        List<String> bodyLines = method.getBodyLines();
        bodyLines.set(0, " return delete(this.newDelete((c, where) ->");
        bodyLines.set(bodyLines.size() - 1, "));");
        return true;
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        List<String> bodyLines = method.getBodyLines();
        bodyLines.set(0, " return update(this.newUpdate((c, where) ->");
        bodyLines.set(bodyLines.size() - 1, "));");
        return true;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        List<String> bodyLines = method.getBodyLines();
        bodyLines.set(0, " return update(this.newUpdate((c, where) ->");
        bodyLines.set(bodyLines.size() - 1, "));");
        return true;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        List<String> bodyLines = method.getBodyLines();
        bodyLines.set(0, " return update(this.newUpdate((c, where) ->");
        bodyLines.set(bodyLines.size() - 1, "));");
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        return DynamicJavaGenerator.generator(new JavaGeneratorContext(introspectedTable, this.context, this.properties, this.serviceInterface(), this.serviceInterfaceImpl(), this.queryInterface()));
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String property = this.properties.getProperty("upper", "true");
        SqlScripts.doSqlScripts(introspectedTable, Boolean.parseBoolean(property));
        return true;
    }

    private String dslPackage() {
        return this.properties.getProperty("dslPackage", "com.cutefool.commons.mybatis.dsl");
    }

    private String serviceInterface() {
        return this.properties.getProperty("serviceInterface", "com.cutefool.commons.mybatis.dsl.MybatisOperations");
    }

    private String serviceInterfaceImpl() {
        return this.properties.getProperty("serviceImpl", "com.cutefool.commons.mybatis.dsl.MybatisIdWorkerTemplate");
    }

    private String queryInterface() {
        return this.properties.getProperty("queryInterface", "com.cutefool.commons.expression.Conditioning");
    }
}
