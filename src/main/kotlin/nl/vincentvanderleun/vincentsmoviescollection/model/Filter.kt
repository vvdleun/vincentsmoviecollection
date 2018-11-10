package nl.vincentvanderleun.vincentsmoviescollection.model

data class Filters (
	val filters: List<Filter>
)

data class Filter (
	val id: String,
	val name: String,
	val orderKey: String,
	val active: Boolean,
	val values: List<FilterValue>
)

data class FilterValue (
	val id: String,
	val name: String,
	val active: Boolean,
	val selected: Boolean
)