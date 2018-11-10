package nl.vincentvanderleun.vincentsmoviescollection.service

import nl.vincentvanderleun.vincentsmoviescollection.dao.couchdb.CouchDbQueryPageDocJsonAdapter
import nl.vincentvanderleun.vincentsmoviescollection.model.Media

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiServiceSingleton {
	private val moshi: Moshi

	init {
		moshi = Moshi.Builder()
				.add(CouchDbQueryPageDocJsonAdapter())
				.add(KotlinJsonAdapterFactory())
				.build()
	}

	fun getDefaultMoshi(): Moshi {
		return moshi
	}
}