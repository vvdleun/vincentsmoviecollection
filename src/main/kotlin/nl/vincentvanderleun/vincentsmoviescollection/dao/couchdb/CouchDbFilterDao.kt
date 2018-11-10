package nl.vincentvanderleun.vincentsmoviescollection.dao.couchdb

import nl.vincentvanderleun.vincentsmoviescollection.dao.FilterDao
import nl.vincentvanderleun.vincentsmoviescollection.dao.couchdb.CouchDb
import nl.vincentvanderleun.vincentsmoviescollection.dao.couchdb.CouchDbQueryField
import nl.vincentvanderleun.vincentsmoviescollection.model.Config
import nl.vincentvanderleun.vincentsmoviescollection.model.Filter
import nl.vincentvanderleun.vincentsmoviescollection.model.FilterValue

class CouchDbFilterDao(val config: Config) : FilterDao {
	private val QUERY_FILTER_FIELDS = listOf(
			CouchDbQueryField("type", "filter"),
			CouchDbQueryField("active", true))
	private val SORT_FILTER_FIELDS = listOf(
			CouchDbSortField("type"),
			CouchDbSortField("orderKey"))
	private val QUERY_FILTER_VALUE_FIELDS = listOf(
			CouchDbQueryField("type", "filterValue"),
			CouchDbQueryField("active", true))
	private val PAGE_SIZE = 100
	
	override fun getAll(): List<Filter> {
		val filters = ArrayList<Filter>()
		
		val filterValuePages = CouchDb(config).queryAllPages(QUERY_FILTER_VALUE_FIELDS, PAGE_SIZE)
		val filterValues = mapFilterValuesToId(filterValuePages)
		
		val filterPages = CouchDb(config).queryAllPages(QUERY_FILTER_FIELDS, SORT_FILTER_FIELDS, PAGE_SIZE)
		for (filterPage in filterPages) {
			filters.addAll(filterPage.docs.map { convertToFilter(it.filter!!, filterValues) })
		}			

		return filters
	}

	private fun mapFilterValuesToId(filterValuePages: List<CouchDbQueryPage>): Map<String, CouchDbFilterValue> {
		val filterValues = HashMap<String, CouchDbFilterValue>()
		for (filterValuePage in filterValuePages) {
			for (couchDbFilter in filterValuePage.docs.map({ it.filterValue!! })) {
				filterValues.put(couchDbFilter.filterValue.id, couchDbFilter)
			}
		}
		return filterValues
	}
		
	private fun convertToFilter(couchDbFilter: CouchDbFilter, mappedFilterValues: Map<String, CouchDbFilterValue>): Filter {
		val filterValues: List<FilterValue> = couchDbFilter.valueIds
				.filter({ fvId -> mappedFilterValues.contains(fvId) })
				.map({ fvId -> mappedFilterValues.get(fvId)!! })
				.filter({ cfv -> cfv.filterId == couchDbFilter.filter.id })
				.map({ cfv -> cfv.filterValue })

		val filter = couchDbFilter.filter.copy(values = filterValues)

		return filter
	}
}