package com.pcz.mybatis.core.parsing;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class GenericTokenParserTest {
    private static final String OPEN_TOKEN = "${";

    private static final String CLOSE_TOKEN = "}";

    private GenericTokenParser genericTokenParser;

    @BeforeEach
    public void setUp() {
        genericTokenParser = new GenericTokenParser(OPEN_TOKEN, CLOSE_TOKEN,
                new VariableTokenHandler(new HashMap<String, String>() {
                    {
                        put("firstName", "pi");
                        put("secondName", "congzhi");
                    }
                }));
    }

    @Test
    public void should_parse() {
        String text = "Hello, ${firstName} ${secondName}, world";
        String result = genericTokenParser.parse(text);
        Assertions.assertThat(result).isEqualTo("Hello, pi congzhi, world");

        text = "hello world";
        result = genericTokenParser.parse(text);
        Assertions.assertThat(result).isEqualTo("hello world");

        text = "hello ${firstName world";
        result = genericTokenParser.parse(text);
        Assertions.assertThat(result).isEqualTo("hello ${firstName world");
    }

    @Test
    public void should_return_empty_to_parse_when_text_is_null() {
        String text = null;
        String result = genericTokenParser.parse(text);
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void should_return_empty_to_parse_when_text_is_empty() {
        String text = "";
        String result = genericTokenParser.parse(text);
        Assertions.assertThat(result).isEmpty();
    }

    private static class VariableTokenHandler implements TokenHandler {
        private Map<String, String> variables;

        VariableTokenHandler(Map<String, String> variables) {
            this.variables = variables;
        }

        @Override
        public String handleToken(String content) {
            return variables.get(content);
        }
    }
}
