package com.COMP1815.kotlin.model

/**
 * Team class - used to represent a particular team
 */
data class Team(var name: String, val leader: User, val users: MutableList<User>) {
    companion object {
        val teams: MutableList<Team> = ArrayList()

        fun findTeamByName(name: String): Team? {
            return teams.find { it.name == name }
        }

        fun findTeamByLeader(email: String): Team? {
            return teams.find { it.leader.email == email }
        }

        fun parseTeam(team: String) {
            team.removeSurrounding("Team(", ")")
                    .split(",")
                    .apply {
                        val users = get(2).removePrefix(" users=")
                                .removeSurrounding("[", "]")
                                .replace(" ", "")
                                .split("#")
                                .map {
                                    User.findUserByEmail(it)
                                            ?: throw User.IllegalUserException("Cannot find user: $it") // If any user is not found throws an exception
                                }
                                .toMutableList()

                        Team(
                                first().removePrefix("name=").removeSurrounding("'"),
                                User.findUserByEmail(get(1).removePrefix(" leader="))
                                        ?: throw IllegalTeamException("${get(1)} not found"),
                                users
                        )
                    }
        }

    }

    init {
        // Avoid duplicates
        users.removeIf { it.email == leader.email }

        if (findTeamByName(name) != null)
            throw IllegalTeamException("") // If that team is not found throws an exception
        teams.add(this) // Add this team to the static teams list
    }

    override fun toString(): String {
        return "Team(name='$name', leader=${leader.email}, users=${users.map { it.email }.toString().replace(",", " #")})"
    }

    class IllegalTeamException(s: String) : Throwable(s)
}
