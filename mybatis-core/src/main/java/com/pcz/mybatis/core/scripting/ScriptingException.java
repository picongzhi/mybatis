package com.pcz.mybatis.core.scripting;

import com.pcz.mybatis.core.exceptions.PersistenceException;

/**
 * 脚本异常
 *
 * @author picongzhi
 */
public class ScriptingException extends PersistenceException {
    public ScriptingException() {
        super();
    }

    public ScriptingException(String msg) {
        super(msg);
    }

    public ScriptingException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ScriptingException(Throwable cause) {
        super(cause);
    }
}
