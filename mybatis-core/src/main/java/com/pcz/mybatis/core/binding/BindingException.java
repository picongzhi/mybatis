package com.pcz.mybatis.core.binding;

import com.pcz.mybatis.core.exceptions.PersistenceException;

/**
 * 绑定异常
 *
 * @author picongzhi
 */
public class BindingException extends PersistenceException {
    public BindingException() {
        super();
    }

    public BindingException(String msg) {
        super(msg);
    }

    public BindingException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public BindingException(Throwable cause) {
        super(cause);
    }
}
