package com.cutefool.mybatis.coder.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mybatis.generator.config.ColumnOverride;

import java.util.Map;

public class MyColumnOverride extends ColumnOverride {

    public MyColumnOverride(String columnName) {
        super(columnName);
        this.imported = true;
    }

    /**
     * 需要导出（默认为true）
     */
    private boolean imported;

    /**
     * 查询属性的对象
     *
     * @return 返回需要查询名称实现类
     */
    private String naming;
    /**
     * 关联的属性值
     *
     * @return 返回需要查询名称实现类
     */
    private String fields;

    /**
     * 字段注释
     */
    private String remarks;

    /**
     * 是否是多属性查询
     *
     * @return true or false
     */
    private boolean multiple;

    /**
     * 字段映射
     */
    private Map<String, F> fieldsMapping;


    public String getNaming() {
        return naming;
    }

    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public void setNaming(String naming) {
        this.naming = naming;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public Map<String, F> getFieldsMapping() {
        return fieldsMapping;
    }

    public void setFieldsMapping(Map<String, F> fieldsMapping) {
        this.fieldsMapping = fieldsMapping;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class F {

        private String name;

        private String remarks;

    }
}
