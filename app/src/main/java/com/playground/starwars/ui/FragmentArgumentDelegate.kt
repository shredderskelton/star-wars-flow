package com.playground.starwars.ui

import android.os.Binder
import android.os.Bundle
import androidx.core.app.BundleCompat
import androidx.fragment.app.Fragment
import kotlin.reflect.KProperty

class FragmentArgumentDelegate<T : Any> : kotlin.properties.ReadWriteProperty<Fragment, T> {

    var value: T? = null

    override operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (value == null) {
            val arguments = checkNotNull(thisRef.arguments) { "Arguments must not be null" }
            val valueUnchecked =
                checkNotNull(arguments[property.name]) { "Arguments must contain key: ${property.name}" }
            @Suppress("UNCHECKED_CAST")
            value = valueUnchecked as T
        }
        return checkNotNull(value) { "Property ${property.name} could not be read" }
    }

    override operator fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        if (thisRef.arguments == null)
            thisRef.arguments = Bundle()

        val args = thisRef.arguments!!
        val key = property.name

        when (value) {
            is String -> args.putString(key, value)
            is Int -> args.putInt(key, value)
            is Short -> args.putShort(key, value)
            is Long -> args.putLong(key, value)
            is Byte -> args.putByte(key, value)
            is ByteArray -> args.putByteArray(key, value)
            is Char -> args.putChar(key, value)
            is CharArray -> args.putCharArray(key, value)
            is CharSequence -> args.putCharSequence(key, value)
            is Float -> args.putFloat(key, value)
            is Bundle -> args.putBundle(key, value)
            is Binder -> BundleCompat.putBinder(args, key, value)
            is android.os.Parcelable -> args.putParcelable(key, value)
            is java.io.Serializable -> args.putSerializable(key, value)
            else -> throw IllegalStateException(
                "Type ${value.javaClass.canonicalName} of property ${property.name} is not supported"
            )
        }
    }
}