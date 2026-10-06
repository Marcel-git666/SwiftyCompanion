package com.example.swiftycompanion.features.users.data

import com.example.swiftycompanion.features.users.device.models.CursusUserDto
import com.example.swiftycompanion.features.users.device.models.ProjectUserDto
import com.example.swiftycompanion.features.users.device.models.UserDto
import com.example.swiftycompanion.features.users.domain.models.Cursus
import com.example.swiftycompanion.features.users.domain.models.Level
import com.example.swiftycompanion.features.users.domain.models.Project
import com.example.swiftycompanion.features.users.domain.models.ProjectGroup
import com.example.swiftycompanion.features.users.domain.models.Skill
import com.example.swiftycompanion.features.users.domain.models.User
import kotlin.time.Instant

fun UserDto.toDomain(): User {
    val mainCursus = cursusUsers.mainCursus()

    return User(
        login = login,
        displayName = displayName,
        email = email,
        imageUrl = image.versions?.medium ?: image.link,
        location = location,
        wallet = wallet,
        correctionPoints = correctionPoints,
        cursus = mainCursus?.toDomain(),
        projectGroups = projectGroups(mainCursusId = mainCursus?.cursus?.id),
    )
}
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

private fun List<CursusUserDto>.mainCursus(): CursusUserDto? =
    firstOrNull { it.cursus.slug == MAIN_CURSUS_SLUG }
        ?: maxByOrNull { Instant.parseOrNull(it.beginAt) ?: Instant.DISTANT_PAST }

private const val MAIN_CURSUS_SLUG = "42cursus"

private fun UserDto.projectGroups(mainCursusId: Long?): List<ProjectGroup> {
    val cursusNames = cursusUsers.associate { it.cursus.id to it.cursus.name }

    return projectsUsers
        .filter { it.status == "finished" && it.validated != null }
        .groupBy { it.cursusIds.firstOrNull() }
        .entries
        .sortedByDescending { it.key == mainCursusId }
        .map { (cursusId, projects) ->
            ProjectGroup(
                cursusName = cursusId?.let { cursusNames[it] },
                projects = projects.map { it.toDomain() },
            )
        }
}