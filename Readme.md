# Movie database with GraphQL interface

#### Kotlin, Spring Boot, Netflix DGS, JetBrains Exposed, PostgresSQL

## Example Queries
https://127.0.0.1:8080/graphiql

### Get Top 100 most expensive movies 
```graphql
{
    movies(limit: 100, offset: 0, sortedBy: {field: "budgedUsd", order: DESC}) {
        id
        title
        originalTitle
        url
        tagline
        description
        releaseDate
        budgedUsd
        revenueUsd
        rating
        votesCount
        tags {
            id
            name
        }
        genres {
            id
            name
        }
    }
}
```

### Get Top 3 movies by user rating for each genre
```graphql
{
  genres {
    name
    movies(limit: 3, sortedBy: {field: "rating", order: DESC}) {
      title
      rating
      tagline
    }
  }
}
```