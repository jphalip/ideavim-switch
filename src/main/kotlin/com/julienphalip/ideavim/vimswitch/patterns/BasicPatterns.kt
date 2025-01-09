package com.julienphalip.ideavim.vimswitch.patterns

import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.normalizedCaseWords
import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.notFollowedBy
import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.standalone
import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.withOptionalSpaces
import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.withRequiredSpaces
import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.words

object BasicPatterns {
  enum class PatternNames(
    val id: String,
  ) {
    BINARY("basic:binary"),
    UP_DOWN_LEFT_RIGHT("basic:up_down_left_right"),
    BITWISE_OPS("basic:bitwise_ops"),
    LOGICAL_OPS("basic:logical_ops"),
    TRUE_FALSE("basic:true_false"),
    AND_OR("basic:and_or"),
    EQUALITY("basic:equality"),
    IS_IS_NOT("basic:is_is_not"),
    QUOTES("basic:quotes"),
  }

  val patterns =
    mapOf(
      PatternNames.BINARY.id to words("0", "1"),
      PatternNames.UP_DOWN_LEFT_RIGHT.id to
        normalizedCaseWords(
          "up",
          "down",
          "left",
          "right",
        ),
      PatternNames.LOGICAL_OPS.id to
        mapOf(
          "&&" to "||",
          "\\|\\|" to "&&",
        ),
      PatternNames.BITWISE_OPS.id to
        mapOf(
          standalone("&") to "|",
          standalone("\\|") to "&",
        ),
      PatternNames.TRUE_FALSE.id to normalizedCaseWords("true", "false"),
      PatternNames.AND_OR.id to normalizedCaseWords("and", "or"),
      PatternNames.EQUALITY.id to
        mapOf(
          withOptionalSpaces("==") to "!=",
          withOptionalSpaces("!=") to "==",
        ),
      PatternNames.IS_IS_NOT.id to
        mapOf(
          withRequiredSpaces(notFollowedBy("is", "\\s+not")) to "is not",
          withRequiredSpaces("is\\s+not") to "is",
        ),
      PatternNames.QUOTES.id to
        mapOf(
          "\"([^\"]+)\"" to "'$1'",
          "'([^']+)'" to "`$1`",
          "`([^`]+)`" to "\"$1\"",
        ),
    )
}
