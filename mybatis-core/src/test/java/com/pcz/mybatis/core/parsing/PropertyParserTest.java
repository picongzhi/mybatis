package com.pcz.mybatis.core.parsing;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

public class PropertyParserTest {
    @Test
    public void should_parse() {
        Properties properties = new Properties();
        properties.setProperty("firstName", "pi");
        properties.setProperty("secondName", "congzhi");
        properties.setProperty(PropertyParser.KEY_ENABLE_DEFAULT_VALUE, "true");

        String value = "Hello, ${firstName:default} ${secondName:default}, world";
        String result = PropertyParser.parse(value, properties);
        Assertions.assertThat(result).isEqualTo("Hello, pi congzhi, world");
    }
}
