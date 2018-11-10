package nl.vincentvanderleun.vincentsmoviescollection.dao

import nl.vincentvanderleun.vincentsmoviescollection.model.Media

interface MediaDao : ReadOnlyDao<Media> {
	override fun getAll(): List<Media>
}