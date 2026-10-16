package lk.hiresmcq.packcalc.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import lk.hiresmcq.packcalc.ui.SimpleVmFactory
import lk.hiresmcq.packcalc.ui.appContainer
import lk.hiresmcq.packcalc.ui.calculator.CalculatorMode
import lk.hiresmcq.packcalc.ui.calculator.CalculatorScreen
import lk.hiresmcq.packcalc.ui.calculator.CalculatorViewModel
import lk.hiresmcq.packcalc.ui.equipment.EquipmentEditorScreen
import lk.hiresmcq.packcalc.ui.equipment.EquipmentEditorViewModel
import lk.hiresmcq.packcalc.ui.equipment.EquipmentListViewModel
import lk.hiresmcq.packcalc.ui.equipment.EquipmentScreen
import lk.hiresmcq.packcalc.ui.equipment.TaxonomyScreen
import lk.hiresmcq.packcalc.ui.equipment.TaxonomyViewModel
import lk.hiresmcq.packcalc.ui.packages.PackageDetailScreen
import lk.hiresmcq.packcalc.ui.packages.PackageDetailViewModel
import lk.hiresmcq.packcalc.ui.packages.PackageListScreen
import lk.hiresmcq.packcalc.ui.packages.PackageListViewModel
import lk.hiresmcq.packcalc.ui.templates.TemplateEditorScreen
import lk.hiresmcq.packcalc.ui.templates.TemplateEditorViewModel
import lk.hiresmcq.packcalc.ui.templates.TemplateListViewModel
import lk.hiresmcq.packcalc.ui.templates.TemplatesScreen

private data class BottomTab(val route: String, val label: String, val icon: ImageVector)

private val bottomTabs = listOf(
    BottomTab(Dest.PACKAGES, "Packages", Icons.Filled.Dashboard),
    BottomTab(Dest.TEMPLATES, "Templates", Icons.Filled.CameraAlt),
    BottomTab(Dest.EQUIPMENT, "Equipment", Icons.Filled.Map),
)

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val container = appContainer()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomTabs.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(navController = navController, currentRoute = currentRoute)
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Dest.PACKAGES,
            modifier = Modifier.padding(padding),
        ) {
            composable(Dest.PACKAGES) {
                val vm: PackageListViewModel = viewModel(
                    factory = SimpleVmFactory(container) { PackageListViewModel(it.packageRepository) },
                )
                PackageListScreen(
                    viewModel = vm,
                    onNewPackage = { navController.navigate(Dest.CALCULATOR_NEW) },
                    onOpenPackage = { navController.navigate(Dest.packageDetail(it)) },
                )
            }

            composable(
                route = Dest.PACKAGE_DETAIL,
                arguments = listOf(navArgument("packageId") { type = NavType.IntType }),
            ) { entry ->
                val packageId = entry.arguments?.getInt("packageId") ?: return@composable
                val vm: PackageDetailViewModel = viewModel(
                    factory = SimpleVmFactory(container) {
                        PackageDetailViewModel(it.packageRepository, it.catalogRepository, packageId)
                    },
                )
                PackageDetailScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(Dest.calculatorEdit(it)) },
                )
            }

            composable(Dest.TEMPLATES) {
                val vm: TemplateListViewModel = viewModel(
                    factory = SimpleVmFactory(container) {
                        TemplateListViewModel(it.templateRepository, it.catalogRepository)
                    },
                )
                TemplatesScreen(
                    viewModel = vm,
                    onAddTemplate = { navController.navigate(Dest.TEMPLATE_NEW) },
                    onUseTemplate = { navController.navigate(Dest.calculatorFromTemplate(it)) },
                )
            }

            composable(Dest.EQUIPMENT) {
                val vm: EquipmentListViewModel = viewModel(
                    factory = SimpleVmFactory(container) { EquipmentListViewModel(it.catalogRepository) },
                )
                EquipmentScreen(
                    viewModel = vm,
                    onAddEquipment = { navController.navigate(Dest.EQUIPMENT_NEW) },
                    onOpenTaxonomy = { navController.navigate(Dest.TAXONOMY) },
                )
            }

            composable(Dest.TAXONOMY) {
                val vm: TaxonomyViewModel = viewModel(
                    factory = SimpleVmFactory(container) { TaxonomyViewModel(it.catalogRepository) },
                )
                TaxonomyScreen(viewModel = vm, onBack = { navController.popBackStack() })
            }

            composable(Dest.EQUIPMENT_NEW) {
                val vm: EquipmentEditorViewModel = viewModel(
                    factory = SimpleVmFactory(container) { EquipmentEditorViewModel(it.catalogRepository) },
                )
                EquipmentEditorScreen(viewModel = vm, onBack = { navController.popBackStack() })
            }

            composable(Dest.TEMPLATE_NEW) {
                val vm: TemplateEditorViewModel = viewModel(
                    factory = SimpleVmFactory(container) {
                        TemplateEditorViewModel(it.templateRepository, it.catalogRepository)
                    },
                )
                TemplateEditorScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onDone = { navController.popBackStack() },
                )
            }

            composable(Dest.CALCULATOR_NEW) {
                CalculatorDestination(navController, container, CalculatorMode.New)
            }
            composable(
                route = Dest.CALCULATOR_EDIT,
                arguments = listOf(navArgument("packageId") { type = NavType.IntType }),
            ) { entry ->
                val packageId = entry.arguments?.getInt("packageId") ?: return@composable
                CalculatorDestination(navController, container, CalculatorMode.Edit(packageId))
            }
            composable(
                route = Dest.CALCULATOR_TEMPLATE,
                arguments = listOf(navArgument("templateId") { type = NavType.IntType }),
            ) { entry ->
                val templateId = entry.arguments?.getInt("templateId") ?: return@composable
                CalculatorDestination(navController, container, CalculatorMode.FromTemplate(templateId))
            }
        }
    }
}

@Composable
private fun CalculatorDestination(
    navController: NavHostController,
    container: lk.hiresmcq.packcalc.di.AppContainer,
    mode: CalculatorMode,
) {
    val vm: CalculatorViewModel = viewModel(
        factory = SimpleVmFactory(container) {
            CalculatorViewModel(
                catalogRepository = it.catalogRepository,
                templateRepository = it.templateRepository,
                packageRepository = it.packageRepository,
                mode = mode,
            )
        },
    )
    CalculatorScreen(
        viewModel = vm,
        onBack = { navController.popBackStack() },
        onDone = { navController.popBackStack() },
    )
}

@Composable
private fun AppBottomBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar {
        bottomTabs.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = {
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
            )
        }
    }
}
