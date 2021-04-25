package com.volodya.moviedb.dataset

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

private val objectMapper = jacksonObjectMapper().apply {
    findAndRegisterModules()
    this.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
    this.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
    this.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
    this.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
}

class ListDatasetCastItemDeserializer : JsonDeserializer<List<DatasetCastItem>>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): List<DatasetCastItem> {
        return kotlin.runCatching { return objectMapper.readValue(p.text) }.getOrDefault(emptyList())
    }
}

class ListDatasetCrewItemDeserializer : JsonDeserializer<List<DatasetCrewItem>>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): List<DatasetCrewItem> {
        return kotlin.runCatching { return objectMapper.readValue(p.text) }.getOrDefault(emptyList())
    }
}

class ListDatasetDictItemDeserializer : JsonDeserializer<List<DatasetDictItem>>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): List<DatasetDictItem> {
        return kotlin.runCatching { return objectMapper.readValue(p.text) }.getOrDefault(emptyList())
    }
}