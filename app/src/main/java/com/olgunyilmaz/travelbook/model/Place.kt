package com.olgunyilmaz.travelbook.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Place(

    @ColumnInfo(name = "name")
    var name : String,

    @ColumnInfo("latitude")
    var latitude : Double,

    @ColumnInfo("longitude")
    var longitude : Double

    ) : Serializable  {

    @PrimaryKey(autoGenerate = true)
    var id = 0


}