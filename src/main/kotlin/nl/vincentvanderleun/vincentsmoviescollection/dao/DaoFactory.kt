package nl.vincentvanderleun.vincentsmoviescollection.dao

import nl.vincentvanderleun.vincentsmoviescollection.model.Config
import nl.vincentvanderleun.vincentsmoviescollection.dao.couchdb.CouchDbDaoFactory


class DaoFactory (val config: Config) {
	fun getDao(): Dao {
		if (config.dbType.toLowerCase() == "couchdb") {
			return CouchDbDaoFactory(config)
		} else {
			throw IllegalStateException("Unsupported database configured: '" + config.dbType + "'")
		}
	}
}