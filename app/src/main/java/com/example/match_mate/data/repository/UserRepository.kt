package com.example.match_mate.data.repository


import android.util.Log
import androidx.lifecycle.LiveData
import com.example.match_mate.data.api.ApiResult
import com.example.match_mate.data.api.RetrofitInstance
import com.example.match_mate.data.db.DatabaseManager
import com.example.match_mate.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.math.log
import kotlin.random.Random

class UserRepository {

    private val apiService = RetrofitInstance.apiService
    private val userDao = DatabaseManager.getDatabase().userDao()

    companion object {
        private val educationList = listOf(
            "B.Tech", "M.Tech", "MBA", "MCA", "B.Sc", "B.Com", "PhD"
        )
        private val religionList = listOf(
            "Hindu", "Muslim", "Christian", "Sikh", "Jain", "Buddhist"
        )
    }

    suspend fun getAllUsers(): ApiResult<List<User>> {
        return ApiResult.Success(userDao.getAllUsers())
    }

    suspend fun getAcceptedUsers(): ApiResult<List<User>> {
        return ApiResult.Success(userDao.getAcceptedUsers())
    }

    suspend fun getDeclinedUsers(): ApiResult<List<User>> {
        return ApiResult.Success(userDao.getDeclinedUsers())
    }

    suspend fun updateUserStatus(user: User, status: String): ApiResult<User> {
        if (status == "accepted") {
            userDao.markUserAsAccepted(user.uuid)
            return ApiResult.Success(user)
        } else if (status == "declined") {
            userDao.markUserAsDeclined(user.uuid)
            return ApiResult.Success(user)
        }
        return ApiResult.Error(400, "Invalid status")
    }

    suspend fun getLoggedInUser(): User? {
        try {
            val loggedIn = userDao.getLoggedInUser()

            return loggedIn
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching logged-in user: ${e.message}")
            return null
        }
    }

    suspend fun getUsersPaginated(page: Int, pageSize: Int): ApiResult<List<User>> {
        return try {
            val offset = (page - 1) * pageSize
            val users = userDao.getUsersPaginated(pageSize, offset)
            ApiResult.Success(users)
        } catch (e: Exception) {
            ApiResult.Error(e.hashCode(), "Failed to fetch paginated users: ${e.message}")
        }
    }

    suspend fun fetchUsers(): ApiResult<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                if (Random.nextFloat() < 0.3f) {
                    throw Exception("Simulated network failure")
                }

                val response = apiService.fetchUsers(10)

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        val userList = handleApiResponse(response.body().toString(), "pending")
                        userDao.insertUsers(userList)
                        Log.e("UserRepository", "fetchUsers: ${userList.size}")
                        ApiResult.Success(userList)
                    } else {
                        ApiResult.Error(200, "Response body is null")
                    }
                } else {
                    ApiResult.Error(response.code(), "Failed to fetch users, API response error")
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Error fetching users: ${e.message}")
                ApiResult.Error(e.hashCode(), "Error: ${e.message}")
            }
        }
    }

    suspend fun fetchLoggedInUser(): ApiResult<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.fetchMe()

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val rawResponse = response.body().toString()
                    Log.d("UsersRepository", "Raw response body: $rawResponse")
                    if (responseBody != null) {
                        Log.e("UsersRepository", "fetchLoggedInUser: $response")
                        val user = handleApiResponse(response.body().toString(), "self")[0]
                        userDao.insertUser(user)
                        ApiResult.Success(user)
                    } else {
                        ApiResult.Error(200, "Response body is null")
                    }
                } else {
                    ApiResult.Error(response.code(), "Failed to fetch user, API response: ${response.code()}")
                }
            } catch (e: Exception) {
                ApiResult.Error(e.hashCode(), "Error: ${e.message}")
            }
        }
    }

    private fun handleApiResponse(jsonString: String, status: String): List<User> {
        val jsonObject = JSONObject(jsonString)
        val results = jsonObject.getJSONArray("results")
        val userList = mutableListOf<User>()

        for (i in 0 until results.length()) {
            val userJson = results.getJSONObject(i)
            val user = parseUserFromResponse(userJson.toString(), status)
            userList.add(user)
        }
        return userList
    }

    private fun parseUserFromResponse(responseBody: String, status: String): User {
        val jsonObject = JSONObject(responseBody)
        val nameObject = jsonObject.getJSONObject("name")
        val locationObject = jsonObject.getJSONObject("location")
        val loginObject = jsonObject.getJSONObject("login")
        val dobObject = jsonObject.getJSONObject("dob")
        val registeredObject = jsonObject.getJSONObject("registered")
        val pictureObject = jsonObject.getJSONObject("picture")
        return User(
            uuid = loginObject.getString("uuid"),
            gender = jsonObject.getString("gender"),
            nameTitle = nameObject.getString("title"),
            nameFirst = nameObject.getString("first"),
            nameLast = nameObject.getString("last"),
            locationStreetNumber = locationObject.getJSONObject("street").getInt("number"),
            locationStreetName = locationObject.getJSONObject("street").getString("name"),
            locationCity = locationObject.getString("city"),
            locationState = locationObject.getString("state"),
            locationCountry = locationObject.getString("country"),
            locationPostcode = locationObject.getString("postcode"),
            locationCoordinatesLatitude = locationObject.getJSONObject("coordinates").getString("latitude"),
            locationCoordinatesLongitude = locationObject.getJSONObject("coordinates").getString("longitude"),
            locationTimezoneOffset = locationObject.getJSONObject("timezone").getString("offset"),
            locationTimezoneDescription = locationObject.getJSONObject("timezone").getString("description"),
            email = jsonObject.getString("email"),
            loginUsername = loginObject.getString("username"),
            loginPassword = loginObject.getString("password"),
            dobDate = dobObject.getString("date"),
            dobAge = dobObject.getInt("age"),
            registeredDate = registeredObject.getString("date"),
            registeredAge = registeredObject.getInt("age"),
            pictureLarge = pictureObject.getString("large"),
            pictureMedium = pictureObject.getString("medium"),
            pictureThumbnail = pictureObject.getString("thumbnail"),
            education = getRandomEducation(),
            religion = getRandomReligion(),
            status = status
        )
    }

    suspend fun acceptUser(uuid: String): ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                userDao.markUserAsAccepted(uuid)
                ApiResult.Success(Unit)
            } catch (e: Exception) {
                Log.e("UserRepository", "Error accepting user: ${e.message}")
                ApiResult.Error(e.hashCode(), "Failed to accept user: ${e.message}")
            }
        }
    }

    suspend fun declineUser(uuid: String): ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                userDao.markUserAsDeclined(uuid)
                ApiResult.Success(Unit)
            } catch (e: Exception) {
                Log.e("UserRepository", "Error declining user: ${e.message}")
                ApiResult.Error(e.hashCode(), "Failed to decline user: ${e.message}")
            }
        }
    }

    private fun getRandomEducation(): String {
        return educationList.random()
    }

    private fun getRandomReligion(): String {
        return religionList.random()
    }
}
