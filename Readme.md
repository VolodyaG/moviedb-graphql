# Movie database with GraphQL interface

#### Kotlin, Spring Boot, Netflix DGS, JetBrains Exposed, PostgresSQL

## Example Queries
https://127.0.0.1:8080/graphiql

### Find good movies with "Magic"
```graphql
{
    movies(searchQuery: "Magic", sortedBy: {field: "rating", order: DESC}) {
        id
        title
        tagline
        description
        releaseDate
        rating
        directors {
            name
        }
        composers {
            name
        }
        genres {
            name
        }
        characters {
            name
            actor {
                name
            }
        }
    }
}
```

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
        directors {
            id
            name
            gender
        }
        composers {
            id
            name
            gender
        }
        characters {
            id
            order
            name
            actor {
                id
                name
                gender
            }
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
            directors {
                name
            }
        }
    }
}
```

### Find Female actresses 
```graphql
{
    person(id: 138) {
        name
        directorOfMovies {
            title
            releaseDate
        }
        playedCharacters {
            name
            movie {
                title
            }
        }
    }
}
```

### Get All movies of Quentin Tarantino
```graphql
{
  person(id: 138) {
    name
    playedCharacters {
      name
      movie {
        title
      }
    }
    directorOfMovies {
      title
    }
  }
}

```
