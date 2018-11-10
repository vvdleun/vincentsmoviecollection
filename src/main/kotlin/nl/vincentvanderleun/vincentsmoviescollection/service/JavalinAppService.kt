package nl.vincentvanderleun.vincentsmoviescollection.service

import nl.vincentvanderleun.vincentsmoviescollection.model.Config
import io.javalin.Javalin

class JavalinAppService(val config: Config) {
	
	fun configuredApp(): Javalin {
		val app = Javalin.create()
				.defaultContentType("application/json")
				.enableCaseSensitiveUrls()
				.start(config.port)
		return app
	}
	
}