package com.cutefool.mybatis.coder.core;

import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.config.xml.ParserEntityResolver;
import org.xml.sax.InputSource;

import java.io.InputStream;

public class MyParserEntityResolver extends ParserEntityResolver {
    public MyParserEntityResolver() {
        super();
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        if (XmlConstants.MYBATIS_GENERATOR_CONFIG_PUBLIC_ID
                .equalsIgnoreCase(publicId)) {
            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream(
                            "mybatis-generator-config_2_0.dtd"); //$NON-NLS-1$
            return new InputSource(is);
        } else {
            return null;
        }
    }
}
