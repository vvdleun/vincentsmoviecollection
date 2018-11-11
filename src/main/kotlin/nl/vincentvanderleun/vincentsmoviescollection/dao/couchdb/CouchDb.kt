package nl.vincentvanderleun.vincentsmoviescollection.dao.couchdb

import nl.vincentvanderleun.vincentsmoviescollection.model.Config
import nl.vincentvanderleun.vincentsmoviescollection.model.Media
import nl.vincentvanderleun.vincentsmoviescollection.service.MoshiServiceSingleton
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.moshi.moshiDeserializerOf
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class CouchDb(val config: Config) {
	fun queryAllPages(queryFields: List<CouchDbQueryField>, pageSize: Int): List<CouchDbQueryPage> {
		return queryAllPages(queryFields, null, pageSize)
	}
	
	fun queryAllPages(queryFields: List<CouchDbQueryField>, sortFields: List<CouchDbSortField>?, pageSize: Int): List<CouchDbQueryPage> {
		val pages = ArrayList<CouchDbQueryPage>()
		
		var response: CouchDbQueryPage? = null

		while (response == null || response.docs.size == pageSize) {
			response = CouchDb(config).queryPage(queryFields, sortFields, pageSize, response?.bookmark)

			pages.add(response)
		}

		return pages
	}
		
	fun queryPage(queryFields: List<CouchDbQueryField>, sortFields: List<CouchDbSortField>?, pageSize: Int, bookmark: String?): CouchDbQueryPage {
		val query = createQuery(queryFields, sortFields, pageSize, bookmark)
		val moshi = MoshiServiceSingleton.getDefaultMoshi()
		val queryAdapter = moshi.adapter(Map::class.java)
		
		val url = createUrl("/_find")
		val jsonBody = queryAdapter.toJson(query)

		return postRequest(url, jsonBody)
		
	}
	
	private fun createQuery(queryFields: List<CouchDbQueryField>, sortFields: List<CouchDbSortField>?, limit: Int, bookmark: String?): Map<String, Any> {
		val query: HashMap<String, Any> = HashMap<String, Any>()
	
		val selector: HashMap<String, Any> = HashMap<String, Any>()
		for (queryField in queryFields) {
			if(queryField.op == null) {
				selector.put(queryField.field, queryField.value)
			} else {
				val queryFieldMap = HashMap<String, Any>()
				queryFieldMap.put(queryField.op, queryField.value)
				selector.put(queryField.field, queryFieldMap)
			}
		}

		if(sortFields != null) {
			val sort = ArrayList<Map<String, Any>>()
			for (sortField in sortFields) {
				val sortFieldMap = HashMap<String, Any>()
				sortFieldMap.put(sortField.field, sortField.order)
				sort.add(sortFieldMap)
			}
			query.put("sort", sort)
		}
			
		query.put("selector", selector)
		query.put("execution_stats", true)
		query.put("limit", limit)
		query.put("skip", 0)

		if (bookmark != null) {
			query.put("bookmark", bookmark)
		}
		
		return query
	}
	
	private fun postRequest(url: String, jsonBody: String): CouchDbQueryPage {
		val moshi = MoshiServiceSingleton.getDefaultMoshi()
		val queryResultAdapter = moshi.adapter(CouchDbQueryPage::class.java)
		
		val deserializer = moshiDeserializerOf(queryResultAdapter)
		val (_, _, result) = url.httpPost().authenticate(config.dbUser, config.dbPassword).jsonBody(jsonBody).responseObject(deserializer);

		return result.get()
	}
	
	private fun createUrl(path: String): String {
		var url = StringBuilder()

		url.append(config.dbUrl)
		if (!url.endsWith("/") && !path.startsWith("/")) {
			url.append("/")
		}

		url.append(path)

		return url.toString()
	}
	
}