package com.julienphalip.ideavim.vimswitch

import com.maddyhome.idea.vim.vimscript.model.datatypes.VimList
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString
import org.assertj.core.api.Assertions.assertThat

class CustomDefinitionsTest : SwitchTestBase() {
  override fun getDefinitions(): String = "group:basic"

  override fun getCustomDefinitions(): VimList {
    // Pattern set 1: Case insensitive with word boundaries
    val normalizedCaseWordPatterns =
      Switch.createNormalizedCaseWordPatterns(
        listOf(VimString("one"), VimString("two"), VimString("three")),
      )

    // Pattern set 2: Case sensitive with word boundaries
    val wordBoundaryPatterns =
      Switch.createWordBoundaryPatterns(
        listOf(VimString("un"), VimString("deux"), VimString("trois")),
      )

    // Pattern set 3: Case insensitive without word boundaries
    val normalizedCasePatterns =
      Switch.createNormalizedCasePatterns(
        listOf(VimString("alpha"), VimString("beta"), VimString("gamma")),
      )

    return VimList(
      mutableListOf(normalizedCaseWordPatterns, wordBoundaryPatterns, normalizedCasePatterns),
    )
  }

  /** Ensure that custom definitions take precedence over built-in definitions. See #4. */
  fun testCustomDefinitionsTakePrecedence() {
    // When on the quote, the quote rule is applied
    assertThat(switch("<caret>\"true\"")).isEqualTo("<caret>'true'")
    assertThat(switch("<caret>\"one\"")).isEqualTo("<caret>'one'")
    // When on true/false, switch between those words as the true/false rule
    // is defined before the quote rule in the basic patterns
    assertThat(switch("\"<caret>true\"")).isEqualTo("\"<caret>false\"")
    // When on one/two, switch between those words as the custom one/two rule
    // takes precedence over the quote rule from the built-in basic patterns
    assertThat(switch("\"<caret>one\"")).isEqualTo("\"<caret>two\"")
  }

  fun testWordBoundaryPatterns() {
    // Test case sensitivity (should not switch due to case mismatch)
    assertThat(switch("<caret>Un")).isEqualTo("<caret>Un")

    // Test word boundaries
    assertThat(switch("<caret>un")).isEqualTo("<caret>deux")
    assertThat(switch("pre<caret>un")).isEqualTo("pre<caret>un") // Should not match within word
    assertThat(switch("<caret>un.")).isEqualTo("<caret>deux.")
  }

  fun testNormalizedCaseWordPatterns() {
    // Test case insensitivity and case preservation for uniform case
    assertThat(switch("<caret>ONE")).isEqualTo("<caret>TWO") // Preserves uppercase
    assertThat(switch("<caret>one")).isEqualTo("<caret>two") // Preserves lowercase
    // Mixed case defaults to lowercase
    assertThat(switch("<caret>OnE")).isEqualTo("<caret>two")

    // Test word boundaries
    assertThat(switch("someone")).isEqualTo("someone") // Should not match within word
    assertThat(switch("<caret>one.")).isEqualTo("<caret>two.")
  }

  fun testNormalizedCasePatterns() {
    // Test case insensitivity and case preservation for uniform case
    assertThat(switch("<caret>ALPHA")).isEqualTo("<caret>BETA") // Preserves uppercase
    assertThat(switch("<caret>alpha")).isEqualTo("<caret>beta") // Preserves lowercase
    // Mixed case defaults to lowercase
    assertThat(switch("<caret>AlPhA")).isEqualTo("<caret>beta")

    // Test no word boundaries (should match within words)
    assertThat(switch("meg<caret>alpha")).isEqualTo("meg<caret>beta")
    assertThat(switch("<caret>alpha.")).isEqualTo("<caret>beta.")
  }

  fun testPatternCycling() {
    // Test cycling through multiple values
    // Normalized case word patterns
    assertThat(switch("<caret>one")).isEqualTo("<caret>two")
    assertThat(switch("<caret>two")).isEqualTo("<caret>three")
    assertThat(switch("<caret>three")).isEqualTo("<caret>one")

    // Word boundary patterns
    assertThat(switch("<caret>un")).isEqualTo("<caret>deux")
    assertThat(switch("<caret>deux")).isEqualTo("<caret>trois")
    assertThat(switch("<caret>trois")).isEqualTo("<caret>un")

    // Normalized case patterns
    assertThat(switch("<caret>alpha")).isEqualTo("<caret>beta")
    assertThat(switch("<caret>beta")).isEqualTo("<caret>gamma")
    assertThat(switch("<caret>gamma")).isEqualTo("<caret>alpha")
  }
}
