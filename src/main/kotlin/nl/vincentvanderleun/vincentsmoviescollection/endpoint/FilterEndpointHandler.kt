package nl.vincentvanderleun.vincentsmoviescollection.endpoint

import nl.vincentvanderleun.vincentsmoviescollection.model.Config
import nl.vincentvanderleun.vincentsmoviescollection.model.Filters
import nl.vincentvanderleun.vincentsmoviescollection.service.MoshiServiceSingleton
import io.javalin.Context


class FilterEndpoint(config: Config) : EndpointHandler(config) {
	fun getFilters(ctx: Context) {
		val filters = dao.getFilterDao().getAll()
		
		val jsonAdapter = MoshiServiceSingleton.getDefaultMoshi().adapter(Filters::class.java)
		writeJsonResult(ctx, jsonAdapter.toJson(Filters(filters)))
	}
}