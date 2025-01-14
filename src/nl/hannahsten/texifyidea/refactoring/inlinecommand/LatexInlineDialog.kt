package nl.hannahsten.texifyidea.refactoring.inlinecommand

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.refactoring.JavaRefactoringSettings
import com.intellij.refactoring.RefactoringBundle
import com.intellij.refactoring.inline.InlineOptionsDialog

abstract class LatexInlineDialog(project: Project?, genericDefinition: PsiElement, invokedOnReference: Boolean) : InlineOptionsDialog(project, true, genericDefinition) {

    init {
        super.myInvokedOnReference = invokedOnReference
    }

    public abstract override fun doAction()

    abstract fun getNumberOfOccurrences(): Int

    override fun getNumberOfOccurrences(nameIdentifierOwner: PsiNameIdentifierOwner?): Int {
        val tempreferences = ReferencesSearch.search(nameIdentifierOwner as PsiElement).findAll().asSequence()

        return tempreferences
            .distinct()
            .toList().size
    }

    override fun getBorderTitle(): String {
        return RefactoringBundle.message("inline.method.border.title")
    }

    override fun isInlineThis(): Boolean {
        return JavaRefactoringSettings.getInstance().INLINE_METHOD_THIS
    }

    override fun isKeepTheDeclarationByDefault(): Boolean {
        return JavaRefactoringSettings.getInstance().INLINE_METHOD_KEEP
    }

    override fun hasHelpAction(): Boolean {
        return false
    }

    override fun allowInlineAll(): Boolean {
        return true
    }

    protected fun updateSettingsPreferences() {
        val settings = JavaRefactoringSettings.getInstance()
        if (myRbInlineThisOnly.isEnabled && myRbInlineAll.isEnabled) {
            settings.INLINE_METHOD_THIS = isInlineThisOnly
        }
        if (myKeepTheDeclaration != null && myKeepTheDeclaration!!.isEnabled) {
            settings.INLINE_METHOD_KEEP = isKeepTheDeclaration
        }
    }
}