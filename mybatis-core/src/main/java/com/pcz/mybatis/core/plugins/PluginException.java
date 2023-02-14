package com.pcz.mybatis.core.plugins;

import com.pcz.mybatis.core.exceptions.PersistenceException;

/**
 * 插件异常
 *
 * @author picongzhi
 */
public class PluginException extends PersistenceException {
    public PluginException() {
        super();
    }

    public PluginException(String msg) {
        super(msg);
    }

    public PluginException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public PluginException(Throwable cause) {
        super(cause);
    }
}
