package com.pcz.mybatis.core.executor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ErrorContextTest {
    @Test
    public void should_get_instance() {
        ErrorContext errorContext = ErrorContext.instance();
        Assertions.assertThat(errorContext).isNotNull();
    }

    @Test
    public void should_store() {
        ErrorContext errorContext = ErrorContext.instance();
        errorContext.store();
    }

    @Test
    public void should_reset() {
        ErrorContext errorContext = ErrorContext.instance();
        errorContext.reset();
    }

    @Test
    public void should_recall() {
        ErrorContext errorContext = ErrorContext.instance();
        errorContext.recall();
    }

    @Test
    public void should_to_string() {
        ErrorContext errorContext = ErrorContext.instance()
                .message("message")
                .resource("resource")
                .object("object")
                .sql("sql")
                .activity("activity")
                .cause(new RuntimeException());
        System.out.println(errorContext);
    }
}
