package nl.vincentvanderleun.vincentsmoviescollection.model

data class Config (
		val dbType: String,
		val dbUrl: String,
		val dbUser: String,
		val dbPassword: String,
		val port: Int)