package com.pcz.mybatis.core.builder;

import com.pcz.mybatis.core.builder.xml.XMLMapperBuilder;
import com.pcz.mybatis.core.io.Resources;
import com.pcz.mybatis.core.session.Configuration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class XMLMapperBuilderTest {
    @Test
    public void should_parse() throws IOException {
        String resource = "xml-mapper-builder.xml";
        Configuration configuration = new Configuration();
        try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
            XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(
                    inputStream, configuration, resource, configuration.getSqlFragments());
            mapperBuilder.parse();
        }
    }
}
