package nl.vincentvanderleun.vincentsmoviescollection.endpoint

import nl.vincentvanderleun.vincentsmoviescollection.model.Config
import nl.vincentvanderleun.vincentsmoviescollection.model.Movies
import nl.vincentvanderleun.vincentsmoviescollection.service.MoshiServiceSingleton
import io.javalin.Context


class MovieEndpoint(config: Config) : EndpointHandler(config) {
	fun getMovies(ctx: Context) {
		val movies = dao.getMovieDao().getAll()
		
		val jsonAdapter = MoshiServiceSingleton.getDefaultMoshi().adapter(Movies::class.java)
		writeJsonResult(ctx, jsonAdapter.toJson(Movies(movies)));
	}
}