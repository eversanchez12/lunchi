package com.sango.core.util

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * A converter to handle the empty response from the API
 */
class NullOrEmptyConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit?
    ): Converter<ResponseBody, Any?> {
        val delegate = retrofit?.nextResponseBodyConverter<Any?>(this, type, annotations)
        return Converter { value -> if (value.contentLength() == 0L) null else delegate?.convert(value) }
    }
}
