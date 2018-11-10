package nl.vincentvanderleun.vincentsmoviescollection.dao.couchdb

import nl.vincentvanderleun.vincentsmoviescollection.dao.Dao
import nl.vincentvanderleun.vincentsmoviescollection.dao.FilterDao
import nl.vincentvanderleun.vincentsmoviescollection.dao.MediaDao
import nl.vincentvanderleun.vincentsmoviescollection.dao.MovieDao
import nl.vincentvanderleun.vincentsmoviescollection.model.Config

class CouchDbDaoFactory (val config: Config) : Dao {
	override fun getMediaDao(): MediaDao {
		return CouchDbMediaDao(config)
	}

	override fun getFilterDao(): FilterDao {
		return CouchDbFilterDao(config)
	}

	override fun getMovieDao(): MovieDao {
		return CouchDbMovieDao(config)
	}
	
}