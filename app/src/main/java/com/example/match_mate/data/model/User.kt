package com.example.match_mate.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val uuid: String,

    val gender: String,
    val nameTitle: String,
    val nameFirst: String,
    val nameLast: String,
    val locationStreetNumber: Int,
    val locationStreetName: String,
    val locationCity: String,
    val locationState: String,
    val locationCountry: String,
    val locationPostcode: String,
    val locationCoordinatesLatitude: String,
    val locationCoordinatesLongitude: String,
    val locationTimezoneOffset: String,
    val locationTimezoneDescription: String,
    val email: String,
    val loginUsername: String,
    val loginPassword: String,
    val dobDate: String,
    val dobAge: Int,
    val registeredDate: String,
    val registeredAge: Int,
    val pictureLarge: String,
    val pictureMedium: String,
    val pictureThumbnail: String,
    val education: String,
    val religion: String = "Hindu",
    val status: String = "pending"
)
