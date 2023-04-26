/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.coder;

import com.cutefool.commons.bulk.BulkMapping;
import com.cutefool.commons.bulk.Bulked;
import com.cutefool.commons.core.util.Reflects;
import com.cutefool.commons.core.util.Strings;
import com.cutefool.mybatis.coder.plugin.utils.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 生成service
 *
 * @author 271007729@qq.com
 * @date 2022/9/21 11:18 AM
 */
public interface JavaGenerator {
    DefaultShellCallback DefaultShellCallback = new DefaultShellCallback(false);

    static boolean exists(GeneratedJavaFile javaFile) {
        try {
            File directory = DefaultShellCallback.getDirectory(javaFile.getTargetProject(), javaFile.getTargetPackage());
            return new File(directory, javaFile.getFileName()).exists();
        } catch (ShellException e) {
            throw new RuntimeException(e);
        }
    }


    static String basePackage(IntrospectedTable introspectedTable) {
        String baseRecordType = introspectedTable.getBaseRecordType();
        baseRecordType = baseRecordType.substring(0, baseRecordType.lastIndexOf("."));
        return JavaGenerator.basePackage(baseRecordType);
    }

    static String basePackage(String baseRecordType) {
        return baseRecordType.substring(0, baseRecordType.lastIndexOf("."));
    }

    static String domain(String baseRecordType) {
        return baseRecordType.substring(baseRecordType.lastIndexOf(".") + 1);
    }

    static String domain(IntrospectedTable introspectedTable) {
        return introspectedTable.getFullyQualifiedTable().getDomainObjectName();
    }

    static String mapper(IntrospectedTable introspectedTable) {
        String myBatis3JavaMapperType = introspectedTable.getMyBatis3JavaMapperType();
        return myBatis3JavaMapperType.substring(myBatis3JavaMapperType.lastIndexOf(".") + 1);
    }

    static FullyQualifiedJavaType javaType(IntrospectedTable introspectedTable, String basePackage, String suffix) {
        return new FullyQualifiedJavaType(JavaGenerator.basePackage(introspectedTable) + "." + basePackage + "." + JavaGenerator.domain(introspectedTable) + suffix);
    }

    static GeneratedJavaFile javaFile(CompilationUnit compilationUnit, Context context) {
        return new GeneratedJavaFile(compilationUnit, JavaGenerator.targetProject(), context.getJavaFormatter());
    }

    static String firstLower(String name) {
        char c = name.charAt(0);
        String firstLetter = name.substring(0, 1);
        return name.replaceFirst(firstLetter, firstLetter.toLowerCase());
    }

    static String domainPaths(JavaGeneratorContext javaGeneratorContext) {
        String domain = javaGeneratorContext.getDomain();
        // 将驼峰转中划线
        String underscore = Strings.underscore(domain, Strings.LINE_THROUGH);
        return Strings.split(underscore).stream().map(String::toLowerCase).map(WordUtils::pluralize).collect(Collectors.joining(Strings.LINE_THROUGH));
    }

    static FullyQualifiedJavaType dtoType(JavaGeneratorContext javaGeneratorContext) {
        return new FullyQualifiedJavaType(javaGeneratorContext.getDtoInstance().getShortName());
    }

    static String remarks(String remarks) {
        // 前端注释|后端注释
        // 读取前端注释
        remarks = StringUtils.isBlank(remarks) ? "未知" : remarks;
        String[] split = remarks.split("\\|");
        return Stream.of(split).findFirst().orElse(remarks);
    }


    static <T extends Annotation> List<String> excludes(Context context, Class<T> annotation) {
        // 需要排除的属性
        try {
            String rootClass = context.getJavaModelGeneratorConfiguration().getProperty("rootClass");
            if (StringUtils.isNotBlank(rootClass)) {
                return Reflects.getAnnotation(Class.forName(rootClass), annotation, (maps, f, x) -> f.getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }


    static <T extends Annotation> Map<String, String> includes(Context context) {
        // 需要排除的属性
        Map<String, String> maps = new LinkedHashMap<>();
        try {
            String rootClass = context.getJavaModelGeneratorConfiguration().getProperty("rootClass");
            if (StringUtils.isNotBlank(rootClass)) {
                Reflects.getAnnotation(Class.forName(rootClass), Bulked.class, (fieldMap, f, x) -> {
                    String fields = x.fields();
                    String remarks = x.remarks();
                    if (StringUtils.isNotBlank(fields)) {
                        maps.put(fields, StringUtils.isNotBlank(remarks) ? remarks : fields);
                    } else {
                        BulkMapping[] mapping = x.mapping();
                        Stream.of(mapping).forEach(m -> {
                            String d = m.d();
                            String dRemarks = m.remarks();
                            maps.put(m.d(), StringUtils.isNotBlank(dRemarks) ? dRemarks : d);
                        });
                    }
                    return f.getName();
                });
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return maps;
    }


    static String targetProject() {
        return "src/main/java";
    }

}
