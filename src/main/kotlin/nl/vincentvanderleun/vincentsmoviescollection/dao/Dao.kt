package nl.vincentvanderleun.vincentsmoviescollection.dao

interface Dao {
	fun getMediaDao(): MediaDao
	fun getFilterDao(): FilterDao
	fun getMovieDao(): MovieDao
}