import nl.vincentvanderleun.vincentsmoviescollection.model.Config
import nl.vincentvanderleun.vincentsmoviescollection.endpoint.LegacyEndpoint
import nl.vincentvanderleun.vincentsmoviescollection.endpoint.MediaEndpoint
import nl.vincentvanderleun.vincentsmoviescollection.endpoint.MovieEndpoint
import nl.vincentvanderleun.vincentsmoviescollection.endpoint.FilterEndpoint
import nl.vincentvanderleun.vincentsmoviescollection.service.ConfigService
import nl.vincentvanderleun.vincentsmoviescollection.service.JavalinAppService

private data class ParsedParameters(val configPath: String?)

fun main(args: Array<String>) {
	val parsedParameters = parseAndValidateParameters(args)
	if (parsedParameters.configPath != null) {
		val config = ConfigService(parsedParameters.configPath).getConfig()
		startServer(config)
	}
}

private fun parseAndValidateParameters(args: Array<String>): ParsedParameters {
	if (args.size == 0 || args.get(0) == "--help") {
		println("Usage: --config <path to json config file>")
		return ParsedParameters(null)
	}

	if (args.get(0) == "--config" && args.size == 2) {
		return ParsedParameters(args.get(1))
	}

	println("Unsupported parameters specified")
	return ParsedParameters(null)
}


private fun startServer(config: Config) {
	JavalinAppService(config).configuredApp()
		.get("/media", { ctx ->
			MediaEndpoint(config).getMedia(ctx)
		})
		.get("/filters", { ctx ->
			FilterEndpoint(config).getFilters(ctx)
		})
		.get("/movies", { ctx ->
			MovieEndpoint(config).getMovies(ctx)
		})
		.get("/legacy", { ctx ->
			LegacyEndpoint(config).getMovies(ctx)
		})
			
}
