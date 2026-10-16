package lk.hiresmcq.packcalc.ui.navigation

object Dest {
    const val PACKAGES = "packages"
    const val PACKAGE_DETAIL = "packages/{packageId}"
    const val TEMPLATES = "templates"
    const val EQUIPMENT = "equipment"

    const val CALCULATOR_NEW = "calculator/new"
    const val CALCULATOR_EDIT = "calculator/edit/{packageId}"
    const val CALCULATOR_TEMPLATE = "calculator/template/{templateId}"

    const val TEMPLATE_NEW = "template/new"

    const val EQUIPMENT_NEW = "equipment/new"

    const val TAXONOMY = "taxonomy"

    fun packageDetail(id: Int) = "packages/$id"
    fun calculatorEdit(id: Int) = "calculator/edit/$id"
    fun calculatorFromTemplate(id: Int) = "calculator/template/$id"

    val bottomTabs = listOf(PACKAGES, TEMPLATES, EQUIPMENT)
}
