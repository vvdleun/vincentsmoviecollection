package nl.vincentvanderleun.vincentsmoviescollection.endpoint

import nl.vincentvanderleun.vincentsmoviescollection.model.Config
import nl.vincentvanderleun.vincentsmoviescollection.model.Movie
import nl.vincentvanderleun.vincentsmoviescollection.service.MoshiServiceSingleton
import io.javalin.Context

// The "/legacy" end-point is here, while the 2013-ish front-end (based on large portions of hand-written jQuery code) is
// still "in production". Everything legacy-related is in this same file, so that this portion can be easily removed
// once a new more modern and REST API-friendly front-end is built.

data class LegacyMovies(
	val filters: List<LegacyFilter>,
	val movies: List<LegacyMovie>,
	val media: Map<Int, String>,
	val parent: Int?,
	val success: Boolean
)

data class LegacyMovie(
	val id: Int,
	val name: String,
	val parent: Int,
	val media: Int,
	val attributes: List<Int>,
	val orderKey: String?,
	val url: String?
)

data class LegacyFilter (
	val id: Int,
	val name: String,
	val attributes: List<LegacyAttribute>
)

data class LegacyAttribute (
	val id: Int,
	val isDefault: Boolean,
	val name: String
)

class LegacyEndpoint(config: Config) : EndpointHandler(config) {
	fun getMovies(ctx: Context) {
		// Convert Filters to LegacyFilter objects

		val filters = dao.getFilterDao().getAll()
		
		// Generate legacy integer ids and map them to DAO's id
		val legacyFilterIds = HashMap<String, Int>()
		val legacyFilterValueIds = HashMap<String, Int>()
		var legacyFilterId = 0
		var legacyFilterValueId = 0
		filters.forEach({ f ->
			legacyFilterIds.put(f.id, ++legacyFilterId)
			f.values.forEach({ fv -> 
				legacyFilterValueIds.put(fv.id, ++legacyFilterValueId)
			})
		})
		val legacyFilters: List<LegacyFilter> = filters.map({ f ->
			val attributes = f.values.map({ fv ->
				LegacyAttribute(legacyFilterValueIds.get(fv.id)!!, fv.selected, fv.name)
			})
			LegacyFilter(legacyFilterIds.get(f.id)!!, f.name, attributes)
		})

		// Convert Media to LegacyMedia objects
		
		val media = dao.getMediaDao().getAll()
		
		var legacyMediaId = 0
		val legacyMediaIds = media.associateBy({ m -> m.id }, { ++legacyMediaId })
		val legacyMedia = media.associateBy({ m -> legacyMediaIds.get(m.id)!! }, { m -> m.name })

		// Convert movies to LegacyMovie objects
		
		val movies = dao.getMovieDao().getAll()

		val legacyMoviesList = ArrayList<LegacyMovie>()

		var legacyMovieId = 0
		val legacyMovieIds = HashMap<String, Int>()
		// First all non-sub movies
		legacyMoviesList.addAll(movies.map({ m ->
			legacyMovieIds.put(m.id, ++legacyMovieId)
			convertToLegacyMovie(legacyMovieId, m, 0, legacyMediaIds.get(m.media)!!, legacyFilterValueIds)
		}))
		// Then all sub movies
		movies.filter({ m -> m.childs != null && !m.childs.isEmpty() }).forEach({ m ->
			legacyMoviesList.addAll(m.childs!!.map({ cm ->
				convertToLegacyMovie(
						++legacyMovieId,
						cm,
						legacyMovieIds.get(m.id)!!,
						legacyMediaIds.get(cm.media)!!,
						legacyFilterValueIds)
			}))
		})
		
		// Construct LegacyMovies object
		// Use hardcoded values for parent and success
		val legacyMovies = LegacyMovies(legacyFilters, legacyMoviesList, legacyMedia, null, true)

		val jsonAdapter = MoshiServiceSingleton.getDefaultMoshi().adapter(LegacyMovies::class.java)
		writeJsonResult(ctx, jsonAdapter.toJson(legacyMovies));
	}

	private fun convertToLegacyMovie(legacyMovieId: Int, m: Movie, parent: Int, media: Int, legacyFilterValueIds: Map<String, Int>): LegacyMovie {
		var name = m.name
		if (m.year != null) {
			name = name + " (" + m.year + ")"
		}
	
		return LegacyMovie(
				legacyMovieId,
				name,
				parent,
				media,
				convertToLegacyIds(m.filterValues.values.flatten(), legacyFilterValueIds),
				m.orderKey,
				m.url)
	}
	
	private fun convertToLegacyIds(ids: List<String>, legacyFilterValueIds: Map<String, Int>): List<Int> {
		return ids.map({ fvId ->
			legacyFilterValueIds.get(fvId) }).filterNotNull()
	}
}

