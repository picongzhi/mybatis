package com.pcz.mybatis.core.cache.decorators;

import com.pcz.mybatis.core.cache.Cache;
import com.pcz.mybatis.core.cache.CacheException;
import com.pcz.mybatis.core.io.Resources;
import com.pcz.mybatis.core.io.SerialFilterChecker;

import java.io.*;

/**
 * 序列化缓存
 *
 * @author picongzhi
 */
public class SerializedCache implements Cache {
    /**
     * 委托
     */
    private final Cache delegate;

    public SerializedCache(Cache delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public void putObject(Object key, Object value) {
        if (value == null || value instanceof Serializable) {
            delegate.putObject(key, serialize((Serializable) value));
        } else {
            throw new CacheException("SharedCache failed to make a copy of a non-serializable object: " + value);
        }
    }

    @Override
    public Object getObject(Object key) {
        Object value = delegate.getObject(key);
        return value == null
                ? null
                : deserialize((byte[]) value);
    }

    @Override
    public Object removeObject(Object key) {
        return delegate.removeObject(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    /**
     * 序列化
     *
     * @param value 值
     * @return 序列化后的字节数组
     */
    private byte[] serialize(Serializable value) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(value);
            objectOutputStream.flush();

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new CacheException("Error serailizaing object, Cause: " + e, e);
        }
    }

    /**
     * 反序列化
     *
     * @param value 值
     * @return 反序列化后的对象
     */
    private Serializable deserialize(byte[] value) {
        SerialFilterChecker.check();

        Serializable result;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(value);
             ObjectInputStream objectInputStream = new CustomObjectInputStream(byteArrayInputStream)) {
            result = (Serializable) objectInputStream.readObject();
        } catch (Exception e) {
            throw new CacheException("Error deserializaing object. Cause: " + e, e);
        }

        return result;
    }

    /**
     * 自定义 ObjectInputStream
     */
    public static class CustomObjectInputStream extends ObjectInputStream {
        public CustomObjectInputStream(InputStream inputStream) throws IOException {
            super(inputStream);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            return Resources.classForName(desc.getName());
        }
    }
}
