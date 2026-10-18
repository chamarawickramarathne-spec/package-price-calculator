package lk.hiresmcq.packcalc.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private data class DrawerEntry(val route: String, val label: String, val icon: ImageVector)

private val drawerEntries = listOf(
    DrawerEntry(Dest.NEW_PACKAGE, "New Package", Icons.Filled.Add),
    DrawerEntry(Dest.PACKAGES, "Saved Packages", Icons.Filled.Dashboard),
    DrawerEntry(Dest.TEMPLATES, "Templates", Icons.Filled.CameraAlt),
    DrawerEntry(Dest.EQUIPMENT, "Equipment", Icons.Filled.Map),
)

/**
 * App navigation drawer. When an email has been saved (v1.4.0 gate) it is
 * shown under the studio name; tapping it opens the change-email route.
 */
@Composable
fun AppDrawer(
    currentRoute: String?,
    email: String?,
    onNavigate: (String) -> Unit,
    onChangeEmail: () -> Unit,
) {
    ModalDrawerSheet {
        Text(
            text = "Package Price Calculator",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 28.dp, top = 24.dp, bottom = 4.dp),
        )
        Text(
            text = "Hire Artist Studio",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 28.dp, bottom = if (email == null) 16.dp else 4.dp),
        )
        if (email != null) {
            Row(
                modifier = Modifier
                    .clickable(onClick = onChangeEmail)
                    .padding(horizontal = 28.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.padding(end = 12.dp))
                Column {
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Tap to change email",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.padding(bottom = 8.dp))
        }
        drawerEntries.forEach { entry ->
            NavigationDrawerItem(
                label = { Text(entry.label) },
                icon = { Icon(entry.icon, contentDescription = null) },
                selected = currentRoute == entry.route,
                onClick = { onNavigate(entry.route) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            )
        }
    }
}
