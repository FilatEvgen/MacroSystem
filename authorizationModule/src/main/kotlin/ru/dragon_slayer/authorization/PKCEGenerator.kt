package ru.dragon_slayer.authorization

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*

interface IPKCEGenerator {
    fun generatePKCE(): PKCEParams
    data class PKCEParams (
        val codeVerifier: String,
        val codeChallenge: String,
        val state: String
    )
}
object PKCEGenerator: IPKCEGenerator {
    override fun generatePKCE(): IPKCEGenerator.PKCEParams {
        val state = generateState()
        val codeVerifier = generateCodeVerifier()
        val codeChallenge = generateCodeChallenge(codeVerifier)

        AuthCache.put(state, codeVerifier)

        return IPKCEGenerator.PKCEParams(codeVerifier, codeChallenge, state)
    }

    private fun generateState(): String {
        val random = ByteArray(16)
        SecureRandom().nextBytes(random)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(random)
    }

    private fun generateCodeVerifier(): String {
        val random = ByteArray(32)
        SecureRandom().nextBytes(random)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(random)
    }

    private fun generateCodeChallenge(codeVerifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(codeVerifier.toByteArray())
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash)
    }
}