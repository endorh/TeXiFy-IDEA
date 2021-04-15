package nl.hannahsten.texifyidea.run

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import nl.hannahsten.texifyidea.run.ui.LatexOutputPath

class LatexOutputPathTest : BasePlatformTestCase() {

    fun testOutputPathCreate() {
        val mainFile = myFixture.addFileToProject(
            "main.tex",
            """
            \documentclass{article}
            \begin{document}
                main
            \end{document}
            """.trimIndent()
        )
        val runConfig = LatexRunConfiguration(myFixture.project, LatexRunConfigurationProducer().configurationFactory, "Test run config")
        runConfig.psiFile = mainFile
        runConfig.setMainFile("main.tex")
        val outPath = LatexOutputPath("out", runConfig.getMainFileContentRoot(), runConfig.mainFile, project)
        // Cannot mkdirs in test, so will default to src
        assertEquals("/src", outPath.getAndCreatePath()?.path)
    }
}