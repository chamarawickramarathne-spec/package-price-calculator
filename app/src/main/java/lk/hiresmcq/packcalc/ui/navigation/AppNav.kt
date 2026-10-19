package lk.hiresmcq.packcalc.ui.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
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
import lk.hiresmcq.packcalc.ui.login.LoginDestination
import lk.hiresmcq.packcalc.ui.login.LoginMode
import lk.hiresmcq.packcalc.ui.packages.PackageDetailScreen
import lk.hiresmcq.packcalc.ui.packages.PackageDetailViewModel
import lk.hiresmcq.packcalc.ui.packages.PackageListScreen
import lk.hiresmcq.packcalc.ui.packages.PackageListViewModel
import lk.hiresmcq.packcalc.ui.templates.TemplateEditorScreen
import lk.hiresmcq.packcalc.ui.templates.TemplateEditorViewModel
import lk.hiresmcq.packcalc.ui.templates.TemplateListViewModel
import lk.hiresmcq.packcalc.ui.templates.TemplatesScreen
import lk.hiresmcq.packcalc.ui.update.UpdateDialog
import lk.hiresmcq.packcalc.ui.update.UpdateViewModel

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val container = appContainer()
    val appContext = androidx.compose.ui.platform.LocalContext.current.applicationContext
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Email gate (v1.4.0): first launch starts on the login screen; once an
    // email is remembered the app starts straight on the calculator.
    val startDestination =
        if (container.sessionStore.email == null) Dest.LOGIN else Dest.NEW_PACKAGE

    val updateViewModel: UpdateViewModel = viewModel(
        factory = SimpleVmFactory(container) {
            UpdateViewModel(it.updateRepository, it.apkDownloader, appContext)
        },
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isTopLevel = currentRoute in Dest.topLevelRoutes
    val openMenu: () -> Unit = { scope.launch { drawerState.open() } }
    // Re-read when the route changes so returning from the email gate shows the
    // latest saved business name in the drawer.
    val businessName = remember(currentRoute) { container.businessStore.load().displayName }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isTopLevel,
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                email = container.sessionStore.email,
                businessName = businessName,
                onNavigate = { route ->
                    scope.launch { drawerState.close() }
                    if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                onChangeEmail = {
                    scope.launch { drawerState.close() }
                    navController.navigate(Dest.EMAIL_CHANGE)
                },
            )
        },
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable(Dest.LOGIN) {
                LoginDestination(
                    mode = LoginMode.Gate,
                    onDone = {
                        navController.navigate(Dest.NEW_PACKAGE) {
                            popUpTo(Dest.LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable(Dest.EMAIL_CHANGE) {
                LoginDestination(
                    mode = LoginMode.Change,
                    onDone = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                )
            }

            composable(Dest.NEW_PACKAGE) {
                CalculatorDestination(
                    navController = navController,
                    mode = CalculatorMode.New,
                    onMenuClick = openMenu,
                )
            }

            composable(Dest.PACKAGES) {
                val vm: PackageListViewModel = viewModel(
                    factory = SimpleVmFactory(container) { PackageListViewModel(it.packageRepository) },
                )
                PackageListScreen(
                    viewModel = vm,
                    onNewPackage = { navController.navigate(Dest.NEW_PACKAGE) },
                    onOpenPackage = { navController.navigate(Dest.packageDetail(it)) },
                    onMenuClick = openMenu,
                )
            }

            composable(
                route = Dest.PACKAGE_DETAIL,
                arguments = listOf(navArgument("packageId") { type = NavType.LongType }),
            ) { entry ->
                val packageId = entry.arguments?.getLong("packageId") ?: return@composable
                val vm: PackageDetailViewModel = viewModel(
                    factory = SimpleVmFactory(container) {
                        PackageDetailViewModel(
                            it.packageRepository,
                            it.usageLogRepository,
                            appContext,
                            packageId,
                        )
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
                    onMenuClick = openMenu,
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
                    onMenuClick = openMenu,
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

            composable(
                route = Dest.CALCULATOR_EDIT,
                arguments = listOf(navArgument("packageId") { type = NavType.LongType }),
            ) { entry ->
                val packageId = entry.arguments?.getLong("packageId") ?: return@composable
                CalculatorDestination(
                    navController = navController,
                    mode = CalculatorMode.Edit(packageId),
                    onMenuClick = null,
                )
            }

            composable(
                route = Dest.CALCULATOR_TEMPLATE,
                arguments = listOf(navArgument("templateId") { type = NavType.IntType }),
            ) { entry ->
                val templateId = entry.arguments?.getInt("templateId") ?: return@composable
                CalculatorDestination(
                    navController = navController,
                    mode = CalculatorMode.FromTemplate(templateId),
                    onMenuClick = null,
                )
            }
        }

        UpdateDialog(updateViewModel)
    }
}

@Composable
private fun CalculatorDestination(
    navController: NavHostController,
    mode: CalculatorMode,
    onMenuClick: (() -> Unit)?,
) {
    val container = appContainer()
    val appContext = androidx.compose.ui.platform.LocalContext.current.applicationContext
    val vm: CalculatorViewModel = viewModel(
        key = "calculator-${mode}",
        factory = SimpleVmFactory(container) {
            CalculatorViewModel(
                catalogRepository = it.catalogRepository,
                templateRepository = it.templateRepository,
                packageRepository = it.packageRepository,
                usageLogRepository = it.usageLogRepository,
                appContext = appContext,
                mode = mode,
            )
        },
    )
    CalculatorScreen(
        viewModel = vm,
        onBack = { navController.popBackStack() },
        onMenuClick = onMenuClick,
        onDone = {
            navController.navigate(Dest.PACKAGES) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        },
    )
}
