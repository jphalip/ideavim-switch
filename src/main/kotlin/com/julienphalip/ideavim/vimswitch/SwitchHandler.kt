package com.julienphalip.ideavim.vimswitch

import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.common.CommandAliasHandler
import com.maddyhome.idea.vim.ex.ranges.Range

// Handles the :Switch and :SwitchReverse commands execution
class SwitchHandler(
  // reverse: true for :SwitchReverse, false for :Switch
  private val reverse: Boolean,
  private val editorAdapter: EditorAdapter = EditorAdapter(),
  private val matcher: PatternMatcher = PatternMatcher(),
) : CommandAliasHandler {
  override fun execute(
    command: String,
    range: Range,
    editor: VimEditor,
    context: ExecutionContext,
  ) {
    // Get the current line and cursor position
    val lineRange = editorAdapter.getLineRange(editor)
    val definitions = Switch.getEnabledPatterns(reverse)
    // Find and apply replacement if there's a match at cursor position
    val switchMatch = matcher.findMatch(lineRange.text, lineRange.caretOffset, definitions)
    if (switchMatch != null) {
      editorAdapter.replace(editor, lineRange, switchMatch)
    }
  }
}
