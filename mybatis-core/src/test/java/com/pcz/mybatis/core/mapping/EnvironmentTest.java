package com.pcz.mybatis.core.mapping;

import com.pcz.mybatis.core.transaction.TransactionFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;

public class EnvironmentTest {
    @Test
    public void should_build() {
        String id = "test";
        TransactionFactory transactionFactory = Mockito.mock(TransactionFactory.class);
        DataSource dataSource = Mockito.mock(DataSource.class);

        Environment environment = new Environment.Builder(id)
                .datasource(dataSource)
                .transactionFactory(transactionFactory)
                .build();
        Assertions.assertThat(environment).isNotNull();
    }
}
