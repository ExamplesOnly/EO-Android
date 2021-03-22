package com.examplesonly.android.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.examplesonly.android.account.UserDataProvider
import com.examplesonly.android.network.Api
import com.examplesonly.android.network.user.UserInterface
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FcmTokenUpdater(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    private var token: String? = null
    private val userInterface: UserInterface = Api(applicationContext).client.create(UserInterface::class.java)
    private val userDataProvider: UserDataProvider = UserDataProvider.getInstance(applicationContext)

    override suspend fun doWork(): Result {
        token = inputData.getString("token")

        // If token is not passed, Get it from FirebaseMessaging
        if (token == null) {
            val task = FirebaseMessaging.getInstance().token.execute()

            if (!task.isSuccessful)
                return Result.retry()

            token = task.result
        }

        return token?.let { publishToken(it) } ?: run {
            Result.retry()
        }
    }

    private fun publishToken(token: String): Result {
        return try {
            val response = userInterface
                    .updateFcmToken(userDataProvider.refreshToken, token).execute()
            if (response.isSuccessful) {
                Timber.e("FcmTokenUpdater %s", response.body().toString())
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private suspend fun <T> Task<T>.execute() = suspendCoroutine<Task<T>> { cont ->
        addOnCompleteListener {
            cont.resume(it)
        }
    }
}