package com.ez.admin.feign.decoder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 参数化类型实现
 * <p>
 * 用于构造泛型类型，例如 SaResult&lt;T&gt;
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-21
 */
public class ParametrizedTypeImpl implements ParameterizedType {

    private final Class<?> rawType;
    private final Type[] typeArguments;
    private final Type ownerType;

    /**
     * 构造参数化类型
     *
     * @param rawType       原始类型
     * @param typeArguments 泛型参数类型数组
     * @param ownerType     所属类型
     */
    public ParametrizedTypeImpl(Class<?> rawType, Type[] typeArguments, Type ownerType) {
        this.rawType = rawType;
        this.typeArguments = typeArguments != null ? typeArguments : new Type[0];
        this.ownerType = ownerType;
    }

    /**
     * 创建参数化类型
     *
     * @param rawType       原始类型
     * @param typeArguments 泛型参数类型数组
     * @param ownerType     所属类型
     * @return 参数化类型
     */
    public static ParameterizedType make(Class<?> rawType, Type[] typeArguments, Type ownerType) {
        return new ParametrizedTypeImpl(rawType, typeArguments, ownerType);
    }

    @Override
    public Type[] getActualTypeArguments() {
        return typeArguments;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }
}
