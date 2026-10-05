package com.example.swiftycompanion.features.users.domain.models

data class User(
    val login: String,
    val displayName: String,
    val email: String,
    val imageUrl: String?,
    val location: String?,
    val wallet: Int,
    val correctionPoints: Int,
    val cursus: Cursus?,
    val projects: List<Project>,
)

data class Cursus(
    val name: String,
    val level: Level,
    val skills: List<Skill>,
)

data class Skill(
    val name: String,
    val level: Level,
)

data class Project(
    val name: String,
    val finalMark: Int,
    val isValidated: Boolean,
)
