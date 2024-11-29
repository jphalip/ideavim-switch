package com.julienphalip.ideavim.vimswitch

import com.julienphalip.ideavim.vimswitch.patterns.BasicPatterns
import com.julienphalip.ideavim.vimswitch.patterns.JavaPatterns
import com.julienphalip.ideavim.vimswitch.patterns.JavaScriptPatterns
import com.julienphalip.ideavim.vimswitch.patterns.MarkdownPatterns
import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils
import com.julienphalip.ideavim.vimswitch.patterns.RSpecPatterns
import com.julienphalip.ideavim.vimswitch.patterns.RubyPatterns
import com.julienphalip.ideavim.vimswitch.patterns.RustPatterns
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimList
import com.maddyhome.idea.vim.vimscript.model.datatypes.VimString

// Handles loading and filtering of pattern definitions from all available language modules
class PatternLoader {
  // All available pattern definitions
  private val builtins =
    BasicPatterns.patterns +
      JavaPatterns.patterns +
      JavaScriptPatterns.patterns +
      MarkdownPatterns.patterns +
      RSpecPatterns.patterns +
      RubyPatterns.patterns +
      RustPatterns.patterns

  private val groups =
    mapOf(
      "basic" to BasicPatterns.PatternNames.entries.map { it.id }.toSet(),
      "java" to JavaPatterns.PatternNames.entries.map { it.id }.toSet(),
      "javascript" to JavaScriptPatterns.PatternNames.entries.map { it.id }.toSet(),
      "markdown" to MarkdownPatterns.PatternNames.entries.map { it.id }.toSet(),
      "rspec" to RSpecPatterns.PatternNames.entries.map { it.id }.toSet(),
      "ruby" to RubyPatterns.PatternNames.entries.map { it.id }.toSet(),
      "rust" to RustPatterns.PatternNames.entries.map { it.id }.toSet(),
    )

  // Get patterns enabled in .ideavimrc, or default to basic patterns
  fun getEnabledPatterns(
    builtinDefinitions: String,
    customDefinitions: VimList,
    reverse: Boolean,
  ): List<Any> {
    // Expand any group references and get individual pattern names
    val builtinPatternNames =
      builtinDefinitions
        .split(",")
        .flatMap { def ->
          when {
            def.trim().startsWith("group:") -> {
              val groupName = def.trim().substring(6)
              groups[groupName] ?: emptySet()
            }
            else -> setOf(def.trim())
          }
        }
        .toSet()
    val builtinPatterns = builtinPatternNames.mapNotNull { builtins[it] }.toList()

    // Parse custom definitions into patterns
    val customPatterns: List<Map<String, String>> =
      customDefinitions.values.mapNotNull { definition ->
        val map = LinkedHashMap<String, String>()
        (definition as? VimList)?.values?.forEach { entry ->
          val pair = entry as? VimList
          val key = (pair?.get(0) as? VimString)?.toString()
          val value = (pair?.get(1) as? VimString)?.toString()
          if (key != null && value != null) {
            map[key] = value
          }
        }
        map.takeIf { it.isNotEmpty() } // Return the map if it has any entries
      }

    val allPatterns = builtinPatterns + customPatterns

    if (reverse) {
      // For reverse, swap pattern and replacement (v->k instead of k->v)
      // and reverse lists to cycle in opposite direction
      return allPatterns.map { def ->
        when (def) {
          is PatternUtils.ReversiblePattern -> {
            def.backwardMap()
          }
          is List<*> -> def.reversed()
          else -> def
        }
      }
    } else {
      return allPatterns.map { def ->
        when (def) {
          is PatternUtils.ReversiblePattern -> {
            def.forwardMap()
          }
          else -> def
        }
      }
    }
  }
}
