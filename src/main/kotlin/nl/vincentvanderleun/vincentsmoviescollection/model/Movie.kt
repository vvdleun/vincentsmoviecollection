package nl.vincentvanderleun.vincentsmoviescollection.model

data class Movies (
	val movies: List<Movie>
)

data class Movie (
	val id: String,
	val name: String,
	val orderKey: String?,
	val active: Boolean,
	val media: String,
	val filterValues: Map<String, List<String>>,
	val url: String?,
	val year: Int?,
	val childs: List<Movie>?,
	val country: String?
)