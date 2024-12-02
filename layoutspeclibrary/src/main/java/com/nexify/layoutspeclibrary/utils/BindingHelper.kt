package com.nexify.layoutspeclibrary.utils

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

object BindingHelper {
    // 缓存
    private val CACHED_BINDING_METHODS = hashMapOf<Class<*>, Method>()

    @Suppress("UNCHECKED_CAST")
    fun <T> getBindingView(componentClazz: Class<*>, layoutInflater: LayoutInflater): T {
        val method = getInflateMethod(componentClazz)
        return method.invoke(null, layoutInflater) as T
    }


    private fun getInflateMethod(componentClazz: Class<*>): Method {
        if (CACHED_BINDING_METHODS.contains(componentClazz)) {
            return CACHED_BINDING_METHODS[componentClazz]!!
        }
        var genericSuperClass = componentClazz.genericSuperclass
        var typeArguments: List<Class<*>>? = if (genericSuperClass is ParameterizedType)
            genericSuperClass.actualTypeArguments.map { it as Class<*> }
        else null
        var superClazz = componentClazz.superclass
        while (typeArguments == null || !typeArguments.any { it.interfaces.contains(ViewBinding::class.java) }) {
            if (superClazz == Object::class.java || superClazz == Any::class.java) {
                throw RuntimeException("class ${componentClazz.canonicalName} has No Typed Parameters!")
            }
            genericSuperClass = superClazz.genericSuperclass
            typeArguments = if (genericSuperClass is ParameterizedType)
                genericSuperClass.actualTypeArguments.map { it as Class<*> }
            else null
            superClazz = superClazz.superclass
        }
        val clazz = typeArguments.first { it.interfaces.contains(ViewBinding::class.java) }
        val m = clazz.getMethod("inflate", LayoutInflater::class.java)
        CACHED_BINDING_METHODS[componentClazz] = m
        return m
    }
}
