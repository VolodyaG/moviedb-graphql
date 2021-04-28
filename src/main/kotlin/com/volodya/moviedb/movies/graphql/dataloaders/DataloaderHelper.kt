package com.volodya.moviedb.movies.graphql.dataloaders

// We populate empty default values for provided keys of DataLoader to avoid NPE on lists
fun <K, V> Set<K>.associateWithOrDefaultEmpty(valueSelector: (K) -> List<V>?): Map<K, List<V>> {
    return associateWith { valueSelector.invoke(it).orEmpty() }
}