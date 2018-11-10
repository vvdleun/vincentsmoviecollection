package nl.vincentvanderleun.vincentsmoviescollection.dao

interface ReadOnlyDao<T> {
	public fun getAll(): List<T>
}