package nl.vincentvanderleun.vincentsmoviescollection.endpoint

import nl.vincentvanderleun.vincentsmoviescollection.model.Config
import nl.vincentvanderleun.vincentsmoviescollection.model.MediaList
import nl.vincentvanderleun.vincentsmoviescollection.service.MoshiServiceSingleton
import io.javalin.Context


class MediaEndpoint(config: Config) : EndpointHandler(config) {
	fun getMedia(ctx: Context) {
		val media = dao.getMediaDao().getAll()
		
		val jsonAdapter = MoshiServiceSingleton.getDefaultMoshi().adapter(MediaList::class.java)
		writeJsonResult(ctx, jsonAdapter.toJson(MediaList(media)));
	}
}