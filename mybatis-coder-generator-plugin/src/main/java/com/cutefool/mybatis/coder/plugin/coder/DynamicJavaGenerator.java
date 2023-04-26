/*
 *  Copyright@2019 清云智通（北京）科技有限公司 保留所有权利
 */
package com.cutefool.mybatis.coder.plugin.coder;

import com.cutefool.commons.core.util.Lists;
import org.mybatis.generator.api.GeneratedJavaFile;

import java.util.List;

/**
 * 生成service
 *
 * @author 271007729@qq.com
 * @date 2022/9/21 11:18 AM
 */
public final class DynamicJavaGenerator {

    private DynamicJavaGenerator() {
    }

    public static List<GeneratedJavaFile> generator(JavaGeneratorContext javaGeneratorContext) {
        List<GeneratedJavaFile> objects = Lists.newArrayList();
        if (javaGeneratorContext.autoController()) {
            List<GeneratedJavaFile> controllers = DynamicJavaControllerGenerator.generator(javaGeneratorContext);
            objects.addAll(controllers);
        }

        if (javaGeneratorContext.autoService()) {
            List<GeneratedJavaFile> services = DynamicJavaServiceGenerator.generator(javaGeneratorContext);
            objects.addAll(services);
        }

        if (javaGeneratorContext.autoDto()) {
            List<GeneratedJavaFile> dtoList = DynamicJavaDtoGenerator.generator(javaGeneratorContext);
            objects.addAll(dtoList);
        }
        return objects;

    }
}
