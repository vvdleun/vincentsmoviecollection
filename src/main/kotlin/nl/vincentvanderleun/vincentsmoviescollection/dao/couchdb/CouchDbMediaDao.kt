package nl.vincentvanderleun.vincentsmoviescollection.dao.couchdb

import nl.vincentvanderleun.vincentsmoviescollection.dao.MediaDao
import nl.vincentvanderleun.vincentsmoviescollection.dao.couchdb.CouchDb
import nl.vincentvanderleun.vincentsmoviescollection.dao.couchdb.CouchDbQueryField
import nl.vincentvanderleun.vincentsmoviescollection.model.Media
import nl.vincentvanderleun.vincentsmoviescollection.model.Config

class CouchDbMediaDao(val config: Config) : MediaDao {
	private val QUERY_MEDIA_FIELDS = listOf(CouchDbQueryField("type", "media"))
	private val PAGE_SIZE = 100
	
	override fun getAll(): List<Media> {
		val media = ArrayList<Media>()

		val pages = CouchDb(config).queryAllPages(QUERY_MEDIA_FIELDS, PAGE_SIZE)
		for (page in pages) {
			media.addAll(page.docs.map { it.media!! })
		}
						
		return media
	}
}