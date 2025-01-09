package com.julienphalip.ideavim.vimswitch

import com.maddyhome.idea.vim.vimscript.model.datatypes.VimList
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString
import org.assertj.core.api.Assertions.assertThat

class CustomDefinitionsTest : SwitchTestBase() {
  override fun getDefinitions(): String = "group:basic"

  override fun getCustomDefinitions(): VimList? {
    val patterns =
      Switch.createNormalizedCaseWordPatterns(listOf(VimString("one"), VimString("two")))
    return VimList(mutableListOf(patterns))
  }

  /**
   * Ensure that custom definitions take precedence over built-in definitions.
   * See #4.
   **/
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
}
