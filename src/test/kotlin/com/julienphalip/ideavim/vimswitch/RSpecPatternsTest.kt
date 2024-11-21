package com.julienphalip.ideavim.vimswitch

import org.assertj.core.api.Assertions.assertThat

class RSpecPatternsTest : SwitchTestBase() {
  override fun getPatternString(): String = "group:rspec"

  fun testShould() {
    assertThat(switch("it <caret>should work")).isEqualTo("it <caret>should_not work")

    assertThat(switch("it should_<caret>not work")).isEqualTo("it should <caret>work")
  }

  fun testExpect() {
    assertThat(switch("expect(value).<caret>to eq(42)"))
      .isEqualTo("expect(value).<caret>not_to eq(42)")

    assertThat(switch("expect(value).<caret>not_to eq(42)"))
      .isEqualTo("expect(value).<caret>to eq(42)")

    assertThat(switch("expect(value).<caret>to_not eq(42)"))
      .isEqualTo("expect(value).<caret>to eq(42)")
  }

  fun testExpectWithComplexArguments() {
    assertThat(switch("expect(user.name).<caret>to eq('John')"))
      .isEqualTo("expect(user.name).<caret>not_to eq('John')")

    assertThat(switch("expect(calculate(x, y)).<caret>to be > 0"))
      .isEqualTo("expect(calculate(x, y)).<caret>not_to be > 0")
  }

  fun testToMatcher() {
    assertThat(switch("subject.<caret>to eq(42)")).isEqualTo("subject.<caret>not_to eq(42)")

    assertThat(switch("subject.<caret>not_to eq(42)")).isEqualTo("subject.<caret>to eq(42)")

    assertThat(switch("subject.<caret>to_not eq(42)")).isEqualTo("subject.<caret>to eq(42)")
  }

  fun testBeTruthyFalsey() {
    assertThat(switch("expect(value).to <caret>be_truthy"))
      .isEqualTo("expect(value).to <caret>be_falsey")

    assertThat(switch("expect(value).to <caret>be_falsey"))
      .isEqualTo("expect(value).to <caret>be_truthy")
  }

  fun testBePresentBlank() {
    assertThat(switch("expect(value).to <caret>be_present"))
      .isEqualTo("expect(value).to <caret>be_blank")

    assertThat(switch("expect(value).to <caret>be_blank"))
      .isEqualTo("expect(value).to <caret>be_present")
  }

  fun testWithDifferentCaretPositions() {
    // Within 'should'
    assertThat(switch("it sh<caret>ould work")).isEqualTo("it sh<caret>ould_not work")

    // Within 'expect'
    assertThat(switch("expect(value).t<caret>o eq(42)"))
      .isEqualTo("expect(value).n<caret>ot_to eq(42)")

    // Within 'be_truthy'
    assertThat(switch("expect(value).to be_tr<caret>uthy"))
      .isEqualTo("expect(value).to be_fa<caret>lsey")
  }

  fun testNoMatchForPartialWords() {
    // Should not match inside other words
    val shouldCase = "shoulder<caret>"
    assertThat(switch(shouldCase)).isEqualTo(shouldCase)

    val truthyCase = "untruth<caret>y"
    assertThat(switch(truthyCase)).isEqualTo(truthyCase)
  }

  fun testPreservesIndentation() {
    assertThat(switch("  expect(value).<caret>to eq(42)"))
      .isEqualTo("  expect(value).<caret>not_to eq(42)")

    assertThat(switch("    it <caret>should work")).isEqualTo("    it <caret>should_not work")
  }
}
