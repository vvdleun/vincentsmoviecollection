package nl.vincentvanderleun.vincentsmoviescollection.dao.couchdb

import nl.vincentvanderleun.vincentsmoviescollection.model.Filter
import nl.vincentvanderleun.vincentsmoviescollection.model.FilterValue
import nl.vincentvanderleun.vincentsmoviescollection.model.Media
import nl.vincentvanderleun.vincentsmoviescollection.model.Movie

// Query page input

class CouchDbQueryField(val field: String, val op: String?, val value: Any) {
	constructor(field: String, value: Any) : this(field, null, value)
}

class CouchDbSortField(val field: String, val order: String) {
	constructor(field: String): this(field, "asc")
}

// Query page output

data class CouchDbQueryPage (
	val docs: List<CouchDbQueryPageDoc>,
	val bookmark: String?,
	val execution_stats: Map<String, Any>?,
	val warning: String?
)

data class CouchDbQueryPageDoc (
	val media: Media?,
	val filter: CouchDbFilter?,
	val filterValue: CouchDbFilterValue?,
	val movie: CouchDbMovie?
)

// Filter model defines List<FilterValue> for values, while Filter objects in the CouchDb database currently
// only store the keys of the values. Therefore we need a temporary container for the FilterValue keys when
// querying Filters in CouchDb.
data class CouchDbFilter(
	val filter: Filter,
	val valueIds: List<String>
)

// FilterValue model does not define field to store the Filter key the FilterValue belongs to
data class CouchDbFilterValue(
	val filterValue: FilterValue,
	val filterId: String
)

// Like the models above, Movie model does not define some fields that are needed when querying with CouchDb
data class CouchDbMovie(
	val movie: Movie,
	val childIds: List<String>?,
	val filterValueIds: List<String>,
	val parent: String?
)