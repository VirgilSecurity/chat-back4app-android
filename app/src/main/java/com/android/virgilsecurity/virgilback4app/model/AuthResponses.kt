package com.android.virgilsecurity.virgilback4app.model

/**
 * AuthResponses
 */

data class AuthenticateResponse(val authToken: String)

data class VirgilJwtResponse(val virgilToken: String)