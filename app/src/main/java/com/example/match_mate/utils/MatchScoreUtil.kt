package com.example.match_mate.utils
import com.example.match_mate.data.model.User
import kotlin.math.abs

object MatchScoreUtil {

    fun calculateMatchScore(loggedInUser: User, otherUser: User): Int {
        val ageScore = calculateAgeScore(loggedInUser.dobAge, otherUser.dobAge)
        val cityScore = if (loggedInUser.locationCity.equals(otherUser.locationCity, ignoreCase = true)) 20 else 0

        return (ageScore + cityScore).coerceIn(0, 100)
    }

    private fun calculateAgeScore(age1: Int, age2: Int): Int {
        val ageDiff = abs(age1 - age2)
        return when {
            ageDiff == 0 -> 80
            ageDiff <= 2 -> 70
            ageDiff <= 5 -> 60
            ageDiff <= 10 -> 50
            else -> 30
        }
    }
}