package ee.ut.cs.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class City(
    @PrimaryKey var cityName: String,
    var latitude: String,
    var longitude: String)