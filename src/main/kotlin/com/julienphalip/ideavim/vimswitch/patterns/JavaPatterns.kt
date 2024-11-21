package com.julienphalip.ideavim.vimswitch.patterns

import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.word
import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.words

object JavaPatterns {
  enum class PatternNames(
    val id: String,
  ) {
    JAVA_ASSERT_EQUALS("java_assert_equals"),
    JAVA_ASSERT_TRUE_FALSE("java_assert_true_false"),
    JAVA_ASSERT_NULL("java_assert_null"),
    JAVA_VISIBILITY("java_visibility"),
    JAVA_OPTIONAL_CHECK("java_optional_check"),
  }

  val patterns =
    mapOf(
      PatternNames.JAVA_ASSERT_EQUALS.id to words("assertEquals", "assertNotEquals"),
      PatternNames.JAVA_ASSERT_TRUE_FALSE.id to words("assertTrue", "assertFalse"),
      PatternNames.JAVA_ASSERT_NULL.id to words("assertNull", "assertNotNull"),
      PatternNames.JAVA_VISIBILITY.id to words("private", "protected", "public"),
      PatternNames.JAVA_OPTIONAL_CHECK.id to
        mapOf(
          word("isPresent") + "\\(\\)" to "isEmpty()",
          word("isEmpty") + "\\(\\)" to "isPresent()",
        ),
    )
}
