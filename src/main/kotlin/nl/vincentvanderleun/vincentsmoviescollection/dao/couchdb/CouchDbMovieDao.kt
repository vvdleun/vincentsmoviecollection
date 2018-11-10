package nl.vincentvanderleun.vincentsmoviescollection.dao.couchdb

import nl.vincentvanderleun.vincentsmoviescollection.dao.MovieDao
import nl.vincentvanderleun.vincentsmoviescollection.dao.couchdb.CouchDb
import nl.vincentvanderleun.vincentsmoviescollection.dao.couchdb.CouchDbQueryField
import nl.vincentvanderleun.vincentsmoviescollection.model.Filter
import nl.vincentvanderleun.vincentsmoviescollection.model.FilterValue
import nl.vincentvanderleun.vincentsmoviescollection.model.Movie
import nl.vincentvanderleun.vincentsmoviescollection.model.Config

import java.util.TreeMap

class CouchDbMovieDao(val config: Config) : MovieDao {
	private val QUERY_STANDARD_MOVIES_FIELDS = listOf(
			CouchDbQueryField("type", "movie"),
			CouchDbQueryField("active", true),
			CouchDbQueryField("censored", false))
	private val QUERY_ALL_MOVIES_FIELDS = listOf(
			CouchDbQueryField("type", "movie"),
			CouchDbQueryField("active", true))
	private val PAGE_SIZE = 100
	private val FILTER_DAO = CouchDbFilterDao(config)

	override fun getAll(): List<Movie> {
		// "all" is a rekbaar begrip :'( ...
		return getAll(false)
	}

	fun getAll(all: Boolean): List<Movie> {
		val mappedFilterValues = mapFilterValuesToFilter()

		val queryFields = if (all) QUERY_ALL_MOVIES_FIELDS else QUERY_STANDARD_MOVIES_FIELDS
		val moviePages = CouchDb(config).queryAllPages(queryFields, PAGE_SIZE)
		val mappedMovies: Map<String, CouchDbMovie> = mapMoviesToIds(moviePages)

		val allMovies = mappedMovies.values
		val mainMovies = allMovies.filter({ it.parent == null })

		return mainMovies.map({ convertToMovie(it, mappedMovies, mappedFilterValues) })
	}

	private fun mapFilterValuesToFilter(): Map<String, Filter> {
		val mappedFilterValues = HashMap<String, Filter>()
		FILTER_DAO.getAll().forEach({f ->
			f.values.forEach({ fv ->
				mappedFilterValues.put(fv.id, f)
			})
		})
		return mappedFilterValues	
	}
		
	private fun mapMoviesToIds(pages: List<CouchDbQueryPage>): Map<String, CouchDbMovie> {
		val comparator = object : Comparator<String> {
			override fun compare(key1: String, key2: String): Int {
				var usedKey1 = key1
				var usedKey2 = key2
				if (key1.toLowerCase().startsWith("movie_") && key2.toLowerCase().startsWith("movie_")) {
					usedKey1 = "movie_" + key1.substring(6).padStart(7, '0')
					usedKey2 = "movie_" + key2.substring(6).padStart(7, '0')
				}

				return usedKey1.compareTo(usedKey2)
			}
		}

		val mappedMovies = TreeMap<String, CouchDbMovie>(comparator)
		pages.forEach({ p->
			p.docs.map({ cm -> cm.movie!! }).forEach({ m ->
				mappedMovies.put(m.movie.id, m)
			})
		})
		return mappedMovies
	}
	
	private fun convertToMovie(couchDbMovie: CouchDbMovie, mappedMovies: Map<String, CouchDbMovie>, mappedFilterValues: Map<String, Filter>): Movie {
		// Create list with child movie objects (if main title is a compilation)
		val childs = ArrayList<Movie>()
		if (couchDbMovie.childIds != null) {
			childs.addAll(couchDbMovie.childIds
					.filter({ id -> mappedMovies.contains(id) })
					.map({ id ->
						val movie = mappedMovies.get(id)!!
						if (movie.childIds != null && movie.childIds.size > 0) {
							throw IllegalStateException("Nested child movies are not supported")
						}
						convertToMovie(movie, mappedMovies, mappedFilterValues)
					}))
		}

		// Model defines that filters must be mapped to a format that looks like this:
		// { filterId: [filterValueId1, filterValueId2...] }		
		val movieFilterValues = HashMap<String, ArrayList<String>>()
		couchDbMovie.filterValueIds.forEach({fvId ->
			val filter = mappedFilterValues.get(fvId)
			if (filter != null) {
				movieFilterValues.computeIfAbsent(filter.id, { ArrayList<String>() })
				movieFilterValues.get(filter.id)!!.add(fvId)
			}
		})
		
		return couchDbMovie.movie.copy(childs=childs, filterValues=movieFilterValues)
	}
}