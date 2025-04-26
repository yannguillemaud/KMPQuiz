package ygmd.kmpquiz.infra

val htmlEntities = mapOf(
    // Guillemets & ponctuation
    "&quot;" to "\"",
    "&apos;" to "'",
    "&#039;" to "'",
    "&ldquo;" to "“",
    "&rdquo;" to "”",
    "&lsquo;" to "‘",
    "&rsquo;" to "’",
    "&ndash;" to "–",
    "&mdash;" to "—",
    "&hellip;" to "…",
    "&nbsp;" to " ",
    "&iexcl;" to "¡",
    "&iquest;" to "¿",

    // Caractères spéciaux
    "&amp;" to "&",
    "&lt;" to "<",
    "&gt;" to ">",

    // Lettres accentuées (minuscules)
    "&aacute;" to "á",
    "&eacute;" to "é",
    "&iacute;" to "í",
    "&oacute;" to "ó",
    "&uacute;" to "ú",
    "&ntilde;" to "ñ",
    "&uuml;" to "ü",
    "&agrave;" to "à",
    "&egrave;" to "è",
    "&igrave;" to "ì",
    "&ograve;" to "ò",
    "&ugrave;" to "ù",
    "&acirc;" to "â",
    "&ecirc;" to "ê",
    "&icirc;" to "î",
    "&ocirc;" to "ô",
    "&ucirc;" to "û",
    "&ccedil;" to "ç",
    "&auml;" to "ä",
    "&euml;" to "ë",
    "&iuml;" to "ï",
    "&ouml;" to "ö",
    "&yuml;" to "ÿ",

    // Lettres accentuées (majuscules)
    "&Aacute;" to "Á",
    "&Eacute;" to "É",
    "&Iacute;" to "Í",
    "&Oacute;" to "Ó",
    "&Uacute;" to "Ú",
    "&Ntilde;" to "Ñ",
    "&Uuml;" to "Ü",
    "&Agrave;" to "À",
    "&Egrave;" to "È",
    "&Igrave;" to "Ì",
    "&Ograve;" to "Ò",
    "&Ugrave;" to "Ù",
    "&Acirc;" to "Â",
    "&Ecirc;" to "Ê",
    "&Icirc;" to "Î",
    "&Ocirc;" to "Ô",
    "&Ucirc;" to "Û",
    "&Ccedil;" to "Ç",
    "&Auml;" to "Ä",
    "&Euml;" to "Ë",
    "&Iuml;" to "Ï",
    "&Ouml;" to "Ö",
    "&Yuml;" to "Ÿ"
)

fun String.unescaped(): String {
    var result = this
    for((entity, char) in htmlEntities){
        result = result.replace(entity, char)
    }
    return result
}