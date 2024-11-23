package com.julienphalip.ideavim.vimswitch

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.newapi.ij

// Handles all editor-related operations
class EditorAdapter {
  // Extract current line information from the editor
  fun getLineRange(editor: VimEditor): LineRange {
    val ijEditor = editor.ij
    val document = ijEditor.document
    val caret = ijEditor.caretModel.primaryCaret
    val line = document.getLineNumber(caret.offset)
    val lineStart = document.getLineStartOffset(line)
    val lineEnd = document.getLineEndOffset(line)

    return LineRange(
      text = document.getText(TextRange(lineStart, lineEnd)),
      start = lineStart,
      end = lineEnd,
      caretOffset = caret.offset - lineStart,
    )
  }

  // Replace text in the editor within a write action
  fun replace(
    editor: Editor,
    lineRange: LineRange,
    match: SwitchMatch,
  ) {
    WriteCommandAction.runWriteCommandAction(editor.project) {
      editor.document.replaceString(
        lineRange.start + match.start,
        lineRange.start + match.end,
        match.replacement,
      )
    }
  }
}
