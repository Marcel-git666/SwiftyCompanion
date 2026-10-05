package com.example.swiftycompanion.features.users.device.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long,
    val login: String,
    val email: String,
    @SerialName("displayname") val displayName: String,
    val phone: String? = null,
    val location: String? = null,
    val wallet: Int,
    @SerialName("correction_point") val correctionPoints: Int,
    val image: ImageDto,
    @SerialName("cursus_users") val cursusUsers: List<CursusUserDto> = emptyList(),
    @SerialName("projects_users") val projectsUsers: List<ProjectUserDto> = emptyList(),
)
@Serializable
data class ImageDto(
    val link: String? = null,
    val versions: ImageVersionsDto? = null,
)

@Serializable
data class ImageVersionsDto(
    val large: String? = null,
    val medium: String? = null,
)

@Serializable
data class CursusUserDto(
    val level: Double,
    val grade: String? = null,
    @SerialName("begin_at") val beginAt: String,
    val cursus: CursusDto,
    val skills: List<SkillDto> = emptyList(),
)

@Serializable
data class CursusDto(
    val id: Long,
    val name: String,
    val slug: String,
)

@Serializable
data class SkillDto(
    val id: Long,
    val name: String,
    val level: Double,
)

@Serializable
data class ProjectUserDto(
    val id: Long,
    val status: String,
    @SerialName("final_mark") val finalMark: Int? = null,
    @SerialName("validated?") val validated: Boolean? = null,
    @SerialName("cursus_ids") val cursusIds: List<Long> = emptyList(),
    val project: ProjectDto,
)

@Serializable
data class ProjectDto(
    val id: Long,
    val name: String,
    val slug: String,
    @SerialName("parent_id") val parentId: Long? = null,
)
