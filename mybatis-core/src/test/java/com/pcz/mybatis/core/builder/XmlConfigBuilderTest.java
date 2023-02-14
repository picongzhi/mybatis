package com.pcz.mybatis.core.builder;

import com.pcz.mybatis.core.builder.xml.XMLConfigBuilder;
import com.pcz.mybatis.core.io.Resources;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class XmlConfigBuilderTest {
    private XMLConfigBuilder xmlConfigBuilder;

    @Test
    public void should_parse() throws IOException {
        String location = "xml-config-builder.xml";
        try (Reader reader = new InputStreamReader(Resources.getResourceAsStream(location))) {
            xmlConfigBuilder = new XMLConfigBuilder(reader);
            xmlConfigBuilder.parse();
        }
    }
}
