package com.julienphalip.ideavim.vimswitch

// Matches patterns in text at cursor position and generates replacements.
// Supports two types of definitions:
// 1. Map of regex patterns to their replacements
// 2. List of words to cycle through
class PatternMatcher {
  fun findMatch(
    text: String,
    cursorOffset: Int,
    definitions: List<Any>,
  ): SwitchMatch? {
    // Try each definition type until we find a match at the cursor position
    for (definition in definitions) {
      when (definition) {
        is Map<*, *> -> {
          // Regex-based replacements (e.g. "(?i)true" -> "false")
          val match = findMapMatch(text, cursorOffset, definition)
          if (match != null) return match
        }
        is List<*> -> {
          // Cycle through words (e.g. ["up", "down", "left", "right"])
          val match = findListMatch(text, cursorOffset, definition)
          if (match != null) return match
        }
      }
    }
    return null
  }

  private fun findMapMatch(
    text: String,
    cursorOffset: Int,
    definition: Map<*, *>,
  ): SwitchMatch? {
    for ((pattern, replacementPattern) in definition) {
      try {
        val regex = pattern.toString().toRegex()
        val matches = regex.findAll(text)
        for (match in matches) {
          if (cursorOffset >= match.range.first && cursorOffset <= match.range.last) {
            // If pattern starts with (?i), preserve the original text's case
            val shouldPreserveCase = pattern.toString().startsWith("(?i)")
            var replacement = replacementPattern.toString()

            if (shouldPreserveCase) {
              replacement = preserveCase(match.value, replacement)
            }

            // Handle capture groups using regex replacement
            if (replacement.contains("$")) {
              replacement = match.value.replace(regex, replacement)
            } else {
              replacement = preserveWhitespace(match.value, replacement)
            }

            return SwitchMatch(
              match.range.first,
              match.range.last + 1,
              replacement,
            )
          }
        }
      } catch (e: Exception) {
        continue // Skip invalid patterns
      }
    }
    return null
  }

  // Cycles through a list of words, replacing one with the next in the list
  private fun findListMatch(
    text: String,
    cursorOffset: Int,
    definition: List<*>,
  ): SwitchMatch? {
    for (i in definition.indices) {
      val current = definition[i].toString()
      val nextIndex = (i + 1) % definition.size // Wrap around to start of list
      val next = definition[nextIndex].toString()

      val index = text.indexOf(current)
      if (index != -1 && cursorOffset >= index && cursorOffset < index + current.length) {
        return SwitchMatch(
          index,
          index + current.length,
          next,
        )
      }
    }
    return null
  }

  // Maintains case style (upper, lower, or capitalized) when replacing text
  private fun preserveCase(
    original: String,
    replacement: String,
  ): String {
    val result =
      when {
        // All caps: TRUE -> FALSE
        original.all { it.isUpperCase() || !it.isLetter() } -> {
          replacement.uppercase()
        }
        // All lowercase: true -> false
        original.all { it.isLowerCase() || !it.isLetter() } -> {
          replacement.lowercase()
        }
        // First letter capital: True -> False
        original.first().isUpperCase() &&
          original.drop(1).all { it.isLowerCase() || !it.isLetter() } -> {
          replacement.lowercase().replaceFirstChar { it.uppercase() }
        }
        // No case conversion needed
        else -> {
          replacement
        }
      }
    return result
  }

  // Maintains whitespace before and after when replacing text
  private fun preserveWhitespace(
    original: String,
    replacement: String,
  ): String {
    val beforeSpace = original.takeWhile { it.isWhitespace() }
    val afterSpace = original.takeLastWhile { it.isWhitespace() }
    return beforeSpace + replacement.trim() + afterSpace
  }
}
