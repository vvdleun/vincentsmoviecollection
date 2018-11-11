package nl.vincentvanderleun.vincentsmoviescollection.service

import nl.vincentvanderleun.vincentsmoviescollection.model.Config
import io.javalin.Javalin
import org.eclipse.jetty.server.Connector
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector

class JavalinAppService(val config: Config) {
	
	fun configuredApp(): Javalin {
		val app = Javalin.create()
				.server({
					val server = Server()
					val serverConnector = ServerConnector(server)
					serverConnector.host = config.host
					serverConnector.port = config.port
					server.connectors = arrayOf<Connector>(serverConnector)
					server
				})
				.defaultContentType("application/json")
				.enableCaseSensitiveUrls()
				.start()
		
		return app
	}
	
}