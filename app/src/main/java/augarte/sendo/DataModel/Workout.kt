package augarte.sendo.DataModel

import java.sql.Blob
import java.sql.Date

class Workout{
    var id: Int? = null
    var name: String? = null
    var description: String? = null
    var image: Blob? = null
    var lastOpen: String? = null
    var createdBy: User? = null
    var createDate: Date? = null
    var modifyDate: Date? = null



}