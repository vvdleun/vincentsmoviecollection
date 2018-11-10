package nl.vincentvanderleun.vincentsmoviescollection.model

data class MediaList(
	val media: List<Media>
)


data class Media(
	val id: Any,
	val name: String)