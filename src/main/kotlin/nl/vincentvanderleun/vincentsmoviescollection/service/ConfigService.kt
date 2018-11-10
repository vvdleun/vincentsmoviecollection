package nl.vincentvanderleun.vincentsmoviescollection.service

import nl.vincentvanderleun.vincentsmoviescollection.model.Config

import java.io.File

class ConfigService(val path: String) {
	fun getConfig(): Config = parseConfig()
	
	private fun parseConfig(): Config {
		val jsonText = File(path).readText(Charsets.UTF_8)
		
		val moshi = MoshiServiceSingleton.getDefaultMoshi()
		val configAdapter = moshi.adapter(Config::class.java)

		return configAdapter.fromJson(jsonText)!!
	}
}