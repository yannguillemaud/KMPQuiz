package ygmd.kmpquiz.infra.fetcher.cs2

object CS2QandaFetcherConstants {
    const val API_URL: String = "https://api.github.com/repos/" +
            "yannguillemaud/cs2-map-positions/" +
            "git/trees/main?recursive=1"
    const val RAWCONTENT_API_URL = "https://raw.githubusercontent.com/" +
            "yannguillemaud/" +
            "cs2-map-positions/main"
}