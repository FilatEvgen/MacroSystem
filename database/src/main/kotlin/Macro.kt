import kotlinx.serialization.Serializable

@Serializable
data class Macro(
    val description: String,
    val comment: String,
    val startStopKey: Int,
    val loopType: LoopType,
    val keys: List<EventConfig>
)

@Serializable
data class EventConfig(
    val eventKey: Int,
    val delay: Long = DEFAULT_DELAY,
    val interval: Long = 0L
)

const val DEFAULT_DELAY = 34L
@Serializable
enum class LoopType {
    INFINITE,
    ONCE
}