package com.julienphalip.ideavim.vimswitch

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.maddyhome.idea.vim.VimPlugin
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimList
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString
import com.maddyhome.idea.vim.vimscript.services.VariableService
import org.mockito.Mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

abstract class SwitchTestBase : BasePlatformTestCase() {
  @Mock private lateinit var variableService: VariableService

  private lateinit var vimPluginMock: AutoCloseable

  private val patternLoader = PatternLoader()
  private val patternMatcher = PatternMatcher()

  override fun setUp() {
    super.setUp()
    MockitoAnnotations.openMocks(this)

    vimPluginMock =
      mockStatic(VimPlugin::class.java).apply {
        `when`<VariableService> { VimPlugin.getVariableService() }.thenReturn(variableService)
      }

    `when`(variableService.getGlobalVariableValue("switch_definitions"))
      .thenReturn(VimString(getPatternString()))
  }

  abstract fun getPatternString(): String

  override fun tearDown() {
    vimPluginMock.close()
    super.tearDown()
  }

  protected fun switch(input: String): String {
    val caretIndex = input.indexOf("<caret>")
    if (caretIndex == -1) return input

    val text = input.replace("<caret>", "")

    val definitions =
      patternLoader.getEnabledPatterns(
        getPatternString(),
        VimList(
          mutableListOf(),
        ),
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
}
