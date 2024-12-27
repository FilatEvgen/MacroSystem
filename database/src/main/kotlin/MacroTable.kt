import org.jetbrains.exposed.dao.id.IntIdTable

object Macros: IntIdTable() {
    val description = varchar("description", 255)
    val comment = varchar("comment", 255)
    val startStopKey = integer("startStopKey")
    val loopType = varchar("loopType", 50)
    val keys = text("keys")

}