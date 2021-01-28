package nl.hannahsten.texifyidea.action.wizard.ipsum

import nl.hannahsten.texifyidea.lang.Described
import kotlin.random.Random

/**
 * @author Hannah Schellekens
 */
data class DummyTextData(
    val ipsumType: IpsumType,

    val blindtextType: BlindtextType = BlindtextType.PARAGRAPH,
    val blindtextLonger: Boolean = false,
    val blindtextRepetitions: Int = 1,
    val blindtextParagraphs: Int = 1,
    val blindtextLevel: Int = 1,

    val lipsumParagraphs: IntRange = 1..7,
    val lipsumSentences: IntRange = 1..999,
    val lipsumParagraphSeparator: LipsumParagraphSeparation = LipsumParagraphSeparation.PARAGRAPH,

    val rawParagraphs: IntRange = 3..7,
    val rawSentencesPerParagraph: IntRange = 2..20,
    val rawSeed: Int = Random.nextInt()
) {

    /**
     * @author Hannah Schellekens
     */
    enum class IpsumType(override val description: String): Described {

        BLINDTEXT("blindtext package"),
        LIPSUM("lipsum package"),
        RAW("raw text");

        override fun toString() = description
    }

    /**
     * @author Hannah Schellekens
     */
    enum class BlindtextType {

        DOCUMENT,
        PARAGRAPH,
        ITEMIZE,
        ENUMERATE,
        DESCRIPTION;

        override fun toString() = name.toLowerCase()
    }

    /**
     * @author Hannah Schellekens
     */
    enum class LipsumParagraphSeparation {

        PARAGRAPH,
        SPACE;

        override fun toString() = name.toLowerCase()
    }
}
