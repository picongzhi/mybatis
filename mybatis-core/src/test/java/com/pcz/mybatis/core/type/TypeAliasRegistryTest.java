package com.pcz.mybatis.core.type;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TypeAliasRegistryTest {
    private TypeAliasRegistry typeAliasRegistry;

    @BeforeEach
    public void setUp() {
        typeAliasRegistry = new TypeAliasRegistry();
    }

    @Test
    public void should_resolve_alias() {
        String alias = TypeAliasRegistry.class.getName();
        Class<?> cls = typeAliasRegistry.resolveAlias(alias);
        Assertions.assertThat(cls).isEqualTo(TypeAliasRegistry.class);
    }
}
