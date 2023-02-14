package com.pcz.mybatis.core.cursor;

import java.io.Closeable;
import java.util.Iterator;

/**
 * 游标
 * 通过迭代器实现懒加载
 *
 * @param <T> 泛型
 * @author picongzhi
 */
public interface Cursor<T> extends Closeable, Iterator<T> {
}
