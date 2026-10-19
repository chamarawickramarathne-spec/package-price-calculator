package lk.hiresmcq.packcalc.ui.navigation

object Dest {
    const val LOGIN = "login"
    const val NEW_PACKAGE = "new-package"
    const val PACKAGES = "packages"
    const val PACKAGE_DETAIL = "packages/{packageId}"
    const val TEMPLATES = "templates"
    const val EQUIPMENT = "equipment"

    const val CALCULATOR_EDIT = "calculator/edit/{packageId}"
    const val CALCULATOR_TEMPLATE = "calculator/template/{templateId}"

    const val TEMPLATE_NEW = "template/new"

    const val EQUIPMENT_NEW = "equipment/new"

    const val TAXONOMY = "taxonomy"

    const val BUSINESS = "business"

    const val EMAIL_CHANGE = "email/change"

    fun packageDetail(id: Long) = "packages/$id"
    fun calculatorEdit(id: Long) = "calculator/edit/$id"
    fun calculatorFromTemplate(id: Int) = "calculator/template/$id"

    val topLevelRoutes = listOf(NEW_PACKAGE, PACKAGES, TEMPLATES, EQUIPMENT, BUSINESS)
}
