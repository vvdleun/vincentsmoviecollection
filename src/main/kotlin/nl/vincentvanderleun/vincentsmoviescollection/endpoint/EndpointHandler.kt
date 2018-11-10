package nl.vincentvanderleun.vincentsmoviescollection.endpoint

import nl.vincentvanderleun.vincentsmoviescollection.dao.DaoFactory
import nl.vincentvanderleun.vincentsmoviescollection.model.Config
import io.javalin.Context

abstract class EndpointHandler(val config: Config) {
	val dao = DaoFactory(config).getDao()
	
	fun writeJsonResult(ctx: Context, json: String) {
		ctx.contentType("application/json; charset=utf-8").result(json)
	}	
	
}