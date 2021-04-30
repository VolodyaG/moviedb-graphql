package com.volodya.moviedb.common

import com.volodya.moviedb.graphql.typings.OrderBy
import com.volodya.moviedb.graphql.typings.SortedBy
import org.jetbrains.exposed.sql.*

fun <T> SizedIterable<T>.optionalOrderBy(condition: List<Pair<Expression<*>, SortOrder>>?): SizedIterable<T> {
    return if (condition != null) this.orderBy(*condition.toTypedArray()) else this
}

fun Table.sortedBy(sortedBy: SortedBy?): List<Pair<Expression<*>, SortOrder>>? {
    if (sortedBy == null) return null

    // Todo Create mapping between graph and table field?
    val column = this.columns.firstOrNull { it.name == sortedBy.field.toSnakeCase() }
    requireNotNull(column) { "Column '${sortedBy.field}' is not found" }
    return listOf(
        IsNullOp(column) to SortOrder.ASC, // So all null values will be in the end
        column to sortedBy.order.toSortOrder()
    )
}

fun OrderBy.toSortOrder(): SortOrder {
    return when (this) {
        OrderBy.ASC -> SortOrder.ASC
        OrderBy.DESC -> SortOrder.DESC
    }
}

private val humps = "(?<=.)(?=\\p{Upper})".toRegex()
private fun String.toSnakeCase() = replace(humps, "_").toLowerCase()

class ILikeOp(expr1: Expression<*>, expr2: Expression<*>) : ComparisonOp(expr1, expr2, "ILIKE")

infix fun <T : String?> Expression<T>.ilike(pattern: String): ILikeOp = ILikeOp(this, stringParam(pattern))