package com.julienphalip.ideavim.vimswitch.patterns

import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.words

object RSpecPatterns {
  enum class PatternNames(
    val id: String,
  ) {
    RSPEC_SHOULD("rspec_should"),
    RSPEC_TO("rspec_to"),
    RSPEC_BE_TRUTHY_FALSEY("rspec_be_truthy_falsey"),
    RSPEC_BE_PRESENT_BLANK("rspec_be_present_blank"),
  }

  val patterns =
    mapOf(
      PatternNames.RSPEC_SHOULD.id to words("should", "should_not"),
      PatternNames.RSPEC_TO.id to
        mapOf(
          ".to(?!_not)\\b" to ".not_to",
          ".not_to\\b" to ".to",
          ".to_not\\b" to ".to",
        ),
      PatternNames.RSPEC_BE_TRUTHY_FALSEY.id to words("be_truthy", "be_falsey"),
      PatternNames.RSPEC_BE_PRESENT_BLANK.id to words("be_present", "be_blank"),
    )
}
