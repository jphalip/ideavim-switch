package com.julienphalip.ideavim.vimswitch

import com.maddyhome.idea.vim.vimscript.model.datatypes.VimList
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString
import com.maddyhome.idea.vim.vimscript.services.VariableService
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mock
import org.mockito.Mockito.`when`

class SwitchReverseTest : SwitchTestBase() {
  override fun getPatternString(): String = "group:basic,group:java"

  @Mock private lateinit var variableService: VariableService

  private val patternLoader = PatternLoader()
  private val patternMatcher = PatternMatcher()

  override fun setUp() {
    super.setUp()
    `when`(variableService.getGlobalVariableValue("switch_definitions"))
      .thenReturn(VimString(getPatternString()))
    `when`(variableService.getGlobalVariableValue("switch_custom_definitions"))
      .thenReturn(VimList(mutableListOf()))
  }

  private fun switchReverse(input: String): String {
    val caretIndex = input.indexOf("<caret>")
    if (caretIndex == -1) return input

    val text = input.replace("<caret>", "")

    val definitions =
      patternLoader.getEnabledPatterns(
        getPatternString(),
        VimList(
          mutableListOf(),
        ),
        true,
      )

    val match = patternMatcher.findMatch(text, caretIndex, definitions)
    if (match != null) {
      val relativeCaretPos = caretIndex - match.start
      val result = text.substring(0, match.start) + match.replacement + text.substring(match.end)
      return result.substring(0, match.start + relativeCaretPos) +
        "<caret>" +
        result.substring(match.start + relativeCaretPos)
    }
    return input
  }

  fun testBasicReverseSwitch() {
    // Test basic boolean switching
    assertThat(switchReverse("fal<caret>se")).isEqualTo("tru<caret>e")
    assertThat(switchReverse("tr<caret>ue")).isEqualTo("fa<caret>lse")

    // Test operator switching
    assertThat(switchReverse("<caret>&&")).isEqualTo("<caret>||")
    assertThat(switchReverse("<caret>||")).isEqualTo("<caret>&&")
  }

  fun testJavaVisibilityReverseSwitch() {
    assertThat(switchReverse("pri<caret>vate void method()"))
      .isEqualTo("pub<caret>lic void method()")

    assertThat(switchReverse("pub<caret>lic void method()"))
      .isEqualTo("pro<caret>tected void method()")

    assertThat(switchReverse("pro<caret>tected void method()"))
      .isEqualTo("pri<caret>vate void method()")
  }

  fun testJavaAssertionsReverseSwitch() {
    assertThat(switchReverse("assert<caret>Equals(expected, actual)"))
      .isEqualTo("assert<caret>NotEquals(expected, actual)")

    assertThat(switchReverse("assert<caret>True(condition)"))
      .isEqualTo("assert<caret>False(condition)")

    assertThat(switchReverse("assert<caret>Null(object)")).isEqualTo("assert<caret>NotNull(object)")
  }
}
