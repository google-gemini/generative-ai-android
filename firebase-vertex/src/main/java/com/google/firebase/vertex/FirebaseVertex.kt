package com.google.firebase.vertex

import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.app

class FirebaseVertex(
    private val firebaseApp: FirebaseApp,
) {
    fun getModel(modelName: String, location: String = "us-central1") =
        FirebaseGenerativeModel(modelName, location)

    companion object {
        val instance: FirebaseVertex
            get() = Firebase.app[FirebaseVertex::class.java]
    }
}

val Firebase.vertex: FirebaseVertex
    get() = FirebaseVertex.instance
