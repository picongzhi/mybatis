package com.pcz.mybatis.core.parsing;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class XPathParserTest {
    private static final String INPUT = "xpath-parser.xml";

    @Test
    public void should_eval_node() {
        try (Reader reader = new InputStreamReader(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(INPUT))) {
            XPathParser parser = new XPathParser(reader, false, null, null);
            XNode xnode = parser.evalNode("/employee");
            Assertions.assertThat(xnode).isNotNull();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
