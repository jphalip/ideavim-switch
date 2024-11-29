package com.julienphalip.ideavim.vimswitch

import com.maddyhome.idea.vim.VimPlugin
import com.maddyhome.idea.vim.extension.VimExtension
import com.maddyhome.idea.vim.extension.VimExtensionFacade
import com.maddyhome.idea.vim.helper.noneOfEnum
import com.maddyhome.idea.vim.vimscript.model.ExecutionResult
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimDataType
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimList
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString
import kotlin.collections.ArrayList
import org.jetbrains.annotations.NonNls

// Extension for switching between common text patterns (e.g. true/false, &&/||, etc.)
// Also provides VimScript functions for custom pattern definitions
class Switch : VimExtension {
  companion object {
    @NonNls private const val SWITCH_DEFINITIONS_VARIABLE_NAME = "switch_definitions"

    @NonNls private const val SWITCH_CUSTOM_DEFINITIONS_VARIABLE_NAME = "switch_custom_definitions"
    private val patternLoader = PatternLoader()

    // Get patterns enabled via switch_definitions in .ideavimrc
    fun getEnabledPatterns(reverse: Boolean): List<Any> {
      val builtinDefinitions =
        VimPlugin.getVariableService()
          .getGlobalVariableValue(SWITCH_DEFINITIONS_VARIABLE_NAME)
          ?.toString() ?: ""
      val customDefinitions =
        VimPlugin.getVariableService()
          .getGlobalVariableValue(SWITCH_CUSTOM_DEFINITIONS_VARIABLE_NAME) as? VimList
          ?: VimList(mutableListOf())
      return patternLoader.getEnabledPatterns(builtinDefinitions, customDefinitions, reverse)
    }
  }

  override fun getName(): String = "switch"

  override fun init() {
    // Register main switch commands
    VimExtensionFacade.addCommand(
      "Switch",
      0,
      0,
      SwitchHandler(false),
    )
    VimExtensionFacade.addCommand(
      "SwitchReverse",
      0,
      0,
      SwitchHandler(true),
    )

    // Export VimScript functions for custom pattern definitions
    registerNormalizedCaseFunction()
    registerWordsFunction()
    registerNormalizedCaseWordsFunction()
  }

  private fun registerNormalizedCaseFunction() {
    VimExtensionFacade.exportScriptFunction(
      scope = null,
      name = "switchNormalizedCase",
      args = listOf("words"),
      defaultArgs = emptyList(),
      hasOptionalArguments = false,
      flags = noneOfEnum(),
    ) { _, _, args ->
      val words = args["words"]
      if (words !is VimList) {
        return@exportScriptFunction ExecutionResult.Error
      }
      val patterns = createNormalizedCasePatterns(words.values)
      ExecutionResult.Return(patterns)
    }
  }

  private fun registerWordsFunction() {
    VimExtensionFacade.exportScriptFunction(
      scope = null,
      name = "switchWords",
      args = listOf("words"),
      defaultArgs = emptyList(),
      hasOptionalArguments = false,
      flags = noneOfEnum(),
    ) { _, _, args ->
      val words = args["words"]
      if (words !is VimList) {
        return@exportScriptFunction ExecutionResult.Error
      }
      val patterns = createWordBoundaryPatterns(words.values)
      ExecutionResult.Return(patterns)
    }
  }

  private fun registerNormalizedCaseWordsFunction() {
    VimExtensionFacade.exportScriptFunction(
      scope = null,
      name = "switchNormalizedCaseWords",
      args = listOf("words"),
      defaultArgs = emptyList(),
      hasOptionalArguments = false,
      flags = noneOfEnum(),
    ) { _, _, args ->
      val words = args["words"]
      if (words !is VimList) {
        return@exportScriptFunction ExecutionResult.Error
      }
      val patterns = createNormalizedCaseWordPatterns(words.values)
      ExecutionResult.Return(patterns)
    }
  }

  // Creates patterns that preserve case but don't require word boundaries
  private fun createNormalizedCasePatterns(words: List<VimDataType>): VimList {
    val list =
      ArrayList<VimDataType>().apply {
        words.forEach { word ->
          words.forEach { otherWord ->
            if (word != otherWord) {
              add(
                VimList(
                  mutableListOf(
                    VimString("(?i)$word"),
                    VimString(otherWord.toString()),
                  ),
                ),
              )
            }
          }
        }
      }
    return VimList(list)
  }

  // Creates patterns that require word boundaries but don't preserve case
  private fun createWordBoundaryPatterns(words: List<VimDataType>): VimList {
    val list =
      ArrayList<VimDataType>().apply {
        words.forEachIndexed { index, word ->
          val nextIndex = (index + 1) % words.size
          add(
            VimList(
              mutableListOf(
                VimString("\\b${word}\\b"),
                VimString(words[nextIndex].toString()),
              ),
            ),
          )
        }
      }
    return VimList(list)
  }

  // Creates patterns that both preserve case and require word boundaries
  private fun createNormalizedCaseWordPatterns(words: List<VimDataType>): VimList {
    val list =
      ArrayList<VimDataType>().apply {
        words.forEach { word ->
          words.forEach { otherWord ->
            if (word != otherWord) {
              add(
                VimList(
                  mutableListOf(
                    VimString("(?i)\\b${word}\\b"),
                    VimString(otherWord.toString()),
                  ),
                ),
              )
            }
          }
        }
      }
    return VimList(list)
  }
}
