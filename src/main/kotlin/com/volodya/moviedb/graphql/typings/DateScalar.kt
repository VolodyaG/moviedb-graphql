package com.volodya.moviedb.graphql.typings

import com.netflix.graphql.dgs.DgsScalar
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import java.time.LocalDate


@DgsScalar(name = "Date")
class DateScalar : Coercing<LocalDate, String> {
    override fun serialize(dataFetcherResult: Any) =
        runCatching { (dataFetcherResult as LocalDate).toString() }
            .getOrElse { throw CoercingSerializeException(it) }

    override fun parseValue(input: Any) =
        runCatching { LocalDate.parse(input.toString())!! }
            .getOrElse { throw CoercingParseValueException(it) }

    override fun parseLiteral(input: Any) =
        runCatching { LocalDate.parse((input as StringValue).value)!! }
            .getOrElse { throw CoercingParseLiteralException(it) }
}