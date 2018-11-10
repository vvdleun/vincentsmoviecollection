package nl.vincentvanderleun.vincentsmoviescollection.dao.couchdb

import nl.vincentvanderleun.vincentsmoviescollection.model.Filter
import nl.vincentvanderleun.vincentsmoviescollection.model.FilterValue
import nl.vincentvanderleun.vincentsmoviescollection.model.Media
import nl.vincentvanderleun.vincentsmoviescollection.model.Movie
import nl.vincentvanderleun.vincentsmoviescollection.service.MoshiServiceSingleton
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.JsonReader

class CouchDbQueryPageDocJsonAdapter {
	@FromJson fun fromJson(reader: JsonReader): CouchDbQueryPageDoc? {
		
		val moshi = MoshiServiceSingleton.getDefaultMoshi()

		// Cheat by delegating the reading of the whole JSON structure to KotlinJsonAdapter'
		// Map<String, Any> adapter. This is lazy design and probably rather slow, because of all the
		// reflection involved...
		val adapter = moshi.adapter(Map::class.java)
		val values = adapter.fromJson(reader)

		val typeDoc: Any? = values?.get("type")

		if (typeDoc != null) {
			if (typeDoc == "media") {
				val media = Media(
						values.get("_id").toString(),
						values.get("name").toString())

				return CouchDbQueryPageDoc(media=media, filter=null, filterValue=null, movie=null)

			} else if (typeDoc == "filter") {
				val filter = Filter(
						values.get("_id").toString(),
						values.get("name").toString(),
						values.get("orderKey").toString(),
						values.get("active") as Boolean,
						ArrayList())

				val valueList = values.get("values") as List<*>
				val filterValueKeys = valueList.map({ it.toString() })

				val couchDbFilter = CouchDbFilter(filter, filterValueKeys)

				return CouchDbQueryPageDoc(media=null, filter=couchDbFilter, filterValue=null, movie=null)

			} else if (typeDoc == "filterValue") {
				val filterValue = FilterValue(
						values.get("_id").toString(),
						values.get("name").toString(),
						values.get("active") as Boolean,
						values.get("selected") as Boolean)

				val filterKey = values.get("filter").toString()

				val couchDbFilterValue = CouchDbFilterValue(filterValue, filterKey)
				
				return CouchDbQueryPageDoc(media=null, filter=null, filterValue=couchDbFilterValue, movie=null)

			} else if (typeDoc == "movie") {

				val movie = Movie(
						values.get("_id").toString(),
						values.get("name").toString(),
						values.get("orderKey")?.toString(),
						values.get("active") as Boolean,
						values.get("media").toString(),
						HashMap(),
						values.get("url")?.toString(),
						(values.get("year") as Double?)?.toInt(),
						ArrayList(),
						values.get("country")?.toString())

				val movieChildIds = ArrayList<String>()
				if(values.get("childs") != null) {
					val movieChildIdsList = values.get("childs") as List<*>
					movieChildIds.addAll(movieChildIdsList.map({ it.toString() }))
				}

				val filterValueIdsList = values.get("filterValues") as List<*>
				val filterValueIds = filterValueIdsList.map({ it.toString() })

				val couchDbMovie = CouchDbMovie(movie, movieChildIds, filterValueIds, values.get("parent")?.toString())
				
				return CouchDbQueryPageDoc(media=null, filter=null, filterValue=null, movie=couchDbMovie)
			}
		}

		throw IllegalStateException("Unknown type '" + typeDoc + "', cannot create doc")
	}
}