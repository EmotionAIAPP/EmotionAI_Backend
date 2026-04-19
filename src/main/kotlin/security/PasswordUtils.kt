package com.example.security

import org.mindrot.jbcrypt.BCrypt

object PasswordUtils {
    fun hash(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())
    fun verify(password: String, hash: String): Boolean = BCrypt.checkpw(password, hash)
}