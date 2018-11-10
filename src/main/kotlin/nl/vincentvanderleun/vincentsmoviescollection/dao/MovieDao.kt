package nl.vincentvanderleun.vincentsmoviescollection.dao

import nl.vincentvanderleun.vincentsmoviescollection.model.Movie

interface MovieDao : ReadOnlyDao<Movie> {
	override fun getAll(): List<Movie>
}