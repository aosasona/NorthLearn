package com.trulyao.northlearn.models

import java.util.Optional

data class User(val id: Int, val username: String, val password: String)

public typealias Users = ArrayList<User>

val users: Users = arrayListOf(
    User(id = 1, username = "merkyle", password = "Merkyle06"),
    User(id = 2, username = "jen89", password = "Dadablue129"),
    User(id = 3, username = "anias", password = "Castiel609"),
    User(id = 4, username = "jdoe", password = "Johndoe"),
    User(id = 5, username = "wchester", password = "DeSam74"),
)

public fun findByUsername(username: String): Optional<User> {
    for (user in users) {
        if (user.username == username.lowercase()) return Optional.of(user)
    }

    return Optional.empty()
}

public fun findByID(userID: Int): Optional<User> {
    for (user in users) {
        if (user.id == userID) return Optional.of(user)
    }

    return Optional.empty();
}