package com.example.swiftycompanion.features.users.data

import com.example.swiftycompanion.features.users.device.models.CursusUserDto
import com.example.swiftycompanion.features.users.device.models.ProjectUserDto
import com.example.swiftycompanion.features.users.device.models.UserDto
import com.example.swiftycompanion.features.users.domain.models.Cursus
import com.example.swiftycompanion.features.users.domain.models.Level
import com.example.swiftycompanion.features.users.domain.models.Project
import com.example.swiftycompanion.features.users.domain.models.Skill
import com.example.swiftycompanion.features.users.domain.models.User
import kotlin.time.Instant

fun UserDto.toDomain(): User =
    User(
        login = login,
        displayName = displayName,
        email = email,
        imageUrl = image.versions?.medium ?: image.link,
        location = location,
        wallet = wallet,
        correctionPoints = correctionPoints,
        cursus = cursusUsers.maxByOrNull { Instant.parse(it.beginAt) }?.toDomain(),
        projects = projectsUsers
            .filter { it.status == "finished" }
            .map { it.toDomain() },
    )

private fun CursusUserDto.toDomain(): Cursus =
    Cursus(
        name = cursus.name,
        level = Level(level),
        skills = skills
            .map { Skill(name = it.name, level = Level(it.level)) }
            .sortedByDescending { it.level.value },
    )

private fun ProjectUserDto.toDomain(): Project =
    Project(
        name = project.name,
        finalMark = finalMark ?: 0,
        isValidated = validated == true,
    )