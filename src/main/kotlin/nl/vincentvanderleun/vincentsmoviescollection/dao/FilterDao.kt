package nl.vincentvanderleun.vincentsmoviescollection.dao

import nl.vincentvanderleun.vincentsmoviescollection.model.Filter

interface FilterDao : ReadOnlyDao<Filter> {
	override fun getAll(): List<Filter>
}