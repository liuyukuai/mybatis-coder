/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.coder;

import lombok.Data;
import com.cutefool.mybatis.coder.plugin.constant.Constants;
import com.cutefool.mybatis.coder.plugin.utils.SqlKeys;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.Context;

import java.util.Objects;
import java.util.Properties;

/**
 * 生成service
 *
 * @author 271007729@qq.com
 * @date 2022/9/21 11:18 AM
 */
@Data
public class JavaGeneratorContext {

    private Context context;

    private Properties properties;

    private Properties tableProperties;

    private IntrospectedTable introspectedTable;

    private FullyQualifiedJavaType serviceInterface;

    private FullyQualifiedJavaType serviceInstance;

    private String dto;

    private FullyQualifiedJavaType dtoInstance;

    private FullyQualifiedJavaType controllerInstance;

    private String domain;

    private String domainRemarks;
    private FullyQualifiedJavaType domainType;

    private String primaryKey;

    private String mapper;

    private String serviceInterfaceCls_;
    private String serviceImplCls_;

    private String queryInterface;

    public JavaGeneratorContext(IntrospectedTable introspectedTable, Context context, Properties properties, String serviceInterface, String serviceImpl, String queryInterface) {
        this.context = context;
        this.introspectedTable = introspectedTable;
        this.properties = properties;
        this.serviceInterfaceCls_ = serviceInterface;
        this.serviceImplCls_ = serviceImpl;
        this.queryInterface = queryInterface;
        this.init();
    }

    private void init() {
        this.serviceInterface = JavaGenerator.javaType(introspectedTable, "service", "Service");
        this.serviceInstance = JavaGenerator.javaType(introspectedTable, "service", "ServiceImpl");
        this.controllerInstance = JavaGenerator.javaType(introspectedTable, "web", "Controller");
        this.domain = JavaGenerator.domain(introspectedTable);
        this.domainType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        this.domainRemarks = introspectedTable.getRemarks();
        this.primaryKey = SqlKeys.primaryKey(introspectedTable);
        this.mapper = JavaGenerator.mapper(introspectedTable);
        this.dtoInstance = JavaGenerator.javaType(introspectedTable, "dto", "DTO");
        this.dto = dtoInstance.getShortName();
        // 获取的自定义配置项
        this.tableProperties = this.introspectedTable.getTableConfiguration().getProperties();
    }

    public boolean autoController() {
        String aTrue = this.tableProperties.getProperty(Constants.AUTO_CONTROLLER, "true");
        return Objects.equals(aTrue, "true");
    }

    public boolean autoService() {
        boolean b = this.autoController();
        if (b) {
            return true;
        }
        String aTrue = this.tableProperties.getProperty(Constants.AUTO_SERVICE, "true");
        return Objects.equals(aTrue, "true");
    }

    public boolean autoDto() {
        boolean b = this.autoService();
        if (b) {
            return true;
        }
        String aTrue = this.tableProperties.getProperty(Constants.AUTO_DTO, "true");
        return Objects.equals(aTrue, "true");
    }

}
