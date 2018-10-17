package nl.rubensten.texifyidea.util

import com.intellij.psi.impl.source.tree.LeafPsiElement
import nl.rubensten.texifyidea.psi.LatexCommands
import nl.rubensten.texifyidea.psi.LatexContent
import nl.rubensten.texifyidea.psi.LatexTypes

/**
 * Checks whether the given LaTeX commands is a definition or not.
 *
 * This is either a command definition or an environment definition. Does not count redefinitions.
 *
 * @return `true` if the command is an environment definition or a command definition, `false` when the command is
 *         `null` or otherwise.
 */
fun LatexCommands?.isDefinition() = this != null && this.name in Magic.Command.definitions

/**
 * Checks whether the given LaTeX commands is a (re)definition or not.
 *
 * This is either a command definition or an environment (re)definition.
 *
 * @return `true` if the command is an environment (re)definition or a command (re)definition, `false` when the command is
 *         `null` or otherwise.
 */
fun LatexCommands?.isDefinitionOrRedefinition() = this != null &&
        (this.name in Magic.Command.redefinitions || this.name in Magic.Command.redefinitions)

/**
 * Checks whether the given LaTeX commands is a command definition or not.
 *
 * @return `true` if the command is a command definition, `false` when the command is `null` or otherwise.
 */
fun LatexCommands?.isCommandDefinition(): Boolean {
    return this != null && ("\\newcommand" == name ||
            "\\let" == name ||
            "\\def" == name ||
            "\\DeclareMathOperator" == name ||
            "\\renewcommand" == name)
}

/**
 * Checks whether the given LaTeX commands is an environment definition or not.
 *
 * @return `true` if the command is an environment definition, `false` when the command is `null` or otherwise.
 */
fun LatexCommands?.isEnvironmentDefinition(): Boolean {
    return this != null && ("\\newenvironment" == name ||
            "\\renewenvironment" == name)
}

/**
 * @see TexifyUtil.getForcedFirstRequiredParameterAsCommand
 */
fun LatexCommands.firstRequiredParamAsCommand(): LatexCommands? = TexifyUtil.getForcedFirstRequiredParameterAsCommand(this)

/**
 * Get the command that gets defined by a definition (`\let` or `\def` command).
 */
fun LatexCommands.definitionCommand(): LatexCommands? = nextCommand()

/**
 * Checks whether the command has a star or not.
 */
fun LatexCommands.hasStar() = childrenOfType(LeafPsiElement::class).any {
    it.elementType == LatexTypes.STAR
}

/**
 * Looks for the next command relative to the given command.
 *
 * @return The next command in the file, or `null` when there is no such command.
 */
fun LatexCommands.nextCommand(): LatexCommands? {
    val content = parentOfType(LatexContent::class) ?: return null
    val next = content.nextSiblingIgnoreWhitespace() as? LatexContent ?: return null
    return next.firstChildOfType(LatexCommands::class)
}

/**
 * Looks for the previous command relative to the given command.
 *
 * @return The previous command in the file, or `null` when there is no such command.
 */
fun LatexCommands.previousCommand(): LatexCommands? {
    val content = parentOfType(LatexContent::class) ?: return null
    val previous = content.previousSiblingIgnoreWhitespace() as? LatexContent ?: return null
    return previous.firstChildOfType(LatexCommands::class)
}

/**
 * @see TexifyUtil.getForcedFirstRequiredParameterAsCommand
 */
fun LatexCommands.forcedFirstRequiredParameterAsCommand(): LatexCommands? = TexifyUtil.getForcedFirstRequiredParameterAsCommand(this)

/**
 * Get the name of the command that is defined by `this` command.
 */
fun LatexCommands.definedCommandName() = when (name) {
    "\\DeclareMathOperator", "\\newcommand" -> forcedFirstRequiredParameterAsCommand()?.name
    else -> definitionCommand()?.name
}

/**
 * @see TexifyUtil.isCommandKnown
 */
fun LatexCommands.isKnown(): Boolean = TexifyUtil.isCommandKnown(this)

/**
 * Get the `index+1`th required parameter of the command.
 *
 * @throws IllegalArgumentException When the index is negative.
 */
@Throws(IllegalArgumentException::class)
fun LatexCommands.requiredParameter(index: Int): String? {
    require(index >= 0) { "Index must not be negative" }

    val parameters = requiredParameters
    if (parameters.isEmpty() || index >= parameters.size) {
        return null
    }

    return parameters[index]
}

/**
 * Finds the indentation of the line where the section command starts.
 */
fun LatexCommands.findIndentation(): String {
    val file = containingFile
    val document = file.document() ?: return ""
    val lineNumber = document.getLineNumber(textOffset)
    return document.lineIndentation(lineNumber)
}

/**
 * If the given command is an include command, the contents of the first argument will be read.
 *
 * @return The included filename or `null` when it's not an include command or when there
 * are no required parameters.
 */
fun LatexCommands.includedFileName(): String? {
    if (commandToken.text !in Magic.Command.includes) return null
    val required = requiredParameters
    if (required.isEmpty()) return null
    return required.first()
}