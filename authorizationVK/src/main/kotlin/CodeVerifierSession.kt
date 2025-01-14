package org.example

import kotlinx.serialization.Serializable

@Serializable
data class CodeVerifierSession(
    val codeVerifier: String
)