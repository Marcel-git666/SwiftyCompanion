package com.example.swiftycompanion.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.swiftycompanion.R
import com.example.swiftycompanion.features.users.domain.models.Project
import com.example.swiftycompanion.features.users.domain.models.ProjectGroup
import com.example.swiftycompanion.features.users.domain.models.Skill
import com.example.swiftycompanion.features.users.domain.models.User
import kotlinx.coroutines.launch

@Composable
fun ProfileList(
    user: User,
    modifier: Modifier = Modifier,
) {
    val skills = user.cursus?.skills.orEmpty()
    val itemModifier = Modifier
        .widthIn(max = 720.dp)
        .fillMaxWidth()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val pagesPerViewport = if (maxWidth >= 600.dp) 2 else 1

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                ProfileHeader(user = user, modifier = itemModifier)
            }

            item {
                SectionTitle(
                    text = stringResource(R.string.profile_skills, skills.size),
                    modifier = itemModifier,
                )
            }
            if (skills.isEmpty()) {
                item { EmptyText(stringResource(R.string.profile_no_skills), itemModifier) }
            } else {
                items(skills) { skill ->
                    SkillRow(skill = skill, modifier = itemModifier)
                }
            }

            if (user.projectGroups.isEmpty()) {
                item {
                    SectionTitle(
                        text = stringResource(R.string.profile_projects, 0),
                        modifier = itemModifier,
                    )
                }
                item { EmptyText(stringResource(R.string.profile_no_projects), itemModifier) }
            } else {
                item {
                    ProjectGroupsPager(
                        groups = user.projectGroups,
                        pagesPerViewport = pagesPerViewport,
                        modifier = itemModifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectGroupsPager(
    groups: List<ProjectGroup>,
    pagesPerViewport: Int,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { groups.size })
    val visiblePages = minOf(pagesPerViewport, groups.size)
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionTitle(
                text = stringResource(R.string.profile_projects, groups.sumOf { it.projects.size }),
                modifier = Modifier.weight(1f),
            )
            if (groups.size > visiblePages) {
                IconButton(
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                    enabled = pagerState.canScrollBackward,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.profile_previous_cursus),
                    )
                }
                IconButton(
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                    enabled = pagerState.canScrollForward,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_forward),
                        contentDescription = stringResource(R.string.profile_next_cursus),
                    )
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            pageSize = PagesPerViewport(visiblePages),
            pageSpacing = 24.dp,
            verticalAlignment = Alignment.Top,
        ) { page ->
            ProjectGroupColumn(group = groups[page])
        }
    }
}

@Composable
private fun ProjectGroupColumn(
    group: ProjectGroup,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = projectGroupTitle(group),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        group.projects.forEach { project ->
            ProjectRow(project = project)
        }
    }
}

private class PagesPerViewport(private val count: Int) : PageSize {
    override fun Density.calculateMainAxisPageSize(availableSpace: Int, pageSpacing: Int): Int =
        (availableSpace - (count - 1) * pageSpacing) / count
}

@Composable
private fun projectGroupTitle(group: ProjectGroup): String =
    stringResource(
        R.string.profile_project_group,
        group.cursusName ?: stringResource(R.string.profile_project_group_other),
        group.projects.size,
    )

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier.padding(top = 16.dp),
    )
}

@Composable
private fun EmptyText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

@Composable
private fun SkillRow(
    skill: Skill,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = skill.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.profile_level_value, skill.level.whole, skill.level.percent),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        LinearProgressIndicator(
            progress = { skill.level.percent / 100f },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ProjectRow(
    project: Project,
    modifier: Modifier = Modifier,
) {
    val color = if (project.isValidated) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val result = stringResource(
        if (project.isValidated) R.string.profile_project_passed else R.string.profile_project_failed,
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = project.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = result,
            style = MaterialTheme.typography.labelMedium,
            color = color,
        )
        Text(
            text = project.finalMark.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}