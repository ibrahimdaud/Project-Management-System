package com.COMP1815.kotlin.model

/**
 * User class - used to represent a particular User
 */
data class User(val email: String, private var password: String, private val l: Boolean = false) {
    companion object {
        val users: MutableSet<User> = HashSet()

        fun findUserByEmail(email: String): User? {
            return users.find { it.email.toUpperCase() == email.toUpperCase() }
        }

        fun parseUser(user: String) {
            user.removeSurrounding("User(", ")")
                    .split(",")
                    .apply {
                        try {
                            User(first().removePrefix("email="), get(1).removePrefix(" password="), true)
                        } catch (e: IllegalUserException) {
                            System.err.println(e.message)
                        }
                    }
        }

        fun validateLogin(email: String, password: String): User? { // Performs the login check
            return users.find { it.email == email && it.password == password.hashCode().toString().reversed() }
        }
    }

    init {
        if (findUserByEmail(email) == null)
            users.add(this)
        else throw IllegalUserException("Duplicate user: $email") // If any duplicate found throws an exception
        if (!l)
            password = "${password.hashCode()}".reversed() // encrypt the password
    }

    class IllegalUserException(s: String) : Exception(s)
}