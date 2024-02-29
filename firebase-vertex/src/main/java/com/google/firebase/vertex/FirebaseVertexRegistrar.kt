package com.google.firebase.vertex

import androidx.annotation.Keep
import com.google.firebase.FirebaseApp
import com.google.firebase.components.BuildConfig
import com.google.firebase.components.Component
import com.google.firebase.components.ComponentRegistrar
import com.google.firebase.components.Dependency
import com.google.firebase.components.Qualified.unqualified
import com.google.firebase.platforminfo.LibraryVersionComponent

@Keep
internal class FirebaseVertexRegistrar : ComponentRegistrar {
    override fun getComponents() =
        listOf(
            Component.builder(FirebaseVertex::class.java)
                .name(LIBRARY_NAME)
                .add(Dependency.required(firebaseApp))
                .factory { container ->
                    FirebaseVertex(
                        container[firebaseApp],
                    )
                }
                .build(),
            LibraryVersionComponent.create(LIBRARY_NAME, BuildConfig.VERSION_NAME),
        )

    private companion object {
        private const val LIBRARY_NAME = "fire-vertex"

        private val firebaseApp = unqualified(FirebaseApp::class.java)
    }
}
