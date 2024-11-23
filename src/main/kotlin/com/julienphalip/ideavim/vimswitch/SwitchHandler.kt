package com.julienphalip.ideavim.vimswitch

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiTreeUtil
import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.common.CommandAliasHandler
import com.maddyhome.idea.vim.ex.ranges.Range
import com.maddyhome.idea.vim.newapi.ij
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

// Handles the :Switch and :SwitchReverse commands execution
class SwitchHandler(
  // reverse: true for :SwitchReverse, false for :Switch
  private val reverse: Boolean,
  private val editorAdapter: EditorAdapter = EditorAdapter(),
  private val matcher: PatternMatcher = PatternMatcher(),
) : CommandAliasHandler {
  private fun cycleToNextEnumValue(
    editor: Editor,
    enumValues: List<String>,
  ) {
    val document = editor.document
    val caretOffset = editor.caretModel.offset

    // Get line number for the caret position
    val lineNumber = document.getLineNumber(caretOffset)

    // Get line start and end offsets
    val lineStartOffset = document.getLineStartOffset(lineNumber)
    val lineEndOffset = document.getLineEndOffset(lineNumber)

    // Get the text of the current line
    val lineText =
      document.getText(com.intellij.openapi.util.TextRange(lineStartOffset, lineEndOffset))

    // Calculate caret offset relative to line start
    val relativeCaretOffset = caretOffset - lineStartOffset

    val lineRange =
      LineRange(
        text = lineText,
        start = lineStartOffset,
        end = lineEndOffset,
        caretOffset = relativeCaretOffset,
      )

    val element =
      editor.project?.let { project ->
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return
        psiFile.findElementAt(caretOffset)
      } ?: return

    val currentValue = element.text ?: return

    val currentIndex = enumValues.indexOf(currentValue)
    if (currentIndex == -1) return

    val nextValue =
      if (currentIndex == enumValues.size - 1) {
        enumValues[0] // Wrap around to first value
      } else {
        enumValues[currentIndex + 1]
      }

    val currentValueStart = element.textRange.startOffset - lineStartOffset
    val currentValueEnd = element.textRange.endOffset - lineStartOffset

    val match =
      SwitchMatch(
        start = currentValueStart,
        end = currentValueEnd,
        replacement = nextValue,
      )

    editorAdapter.replace(editor, lineRange, match)

    // Move caret to the start of the replaced word
    editor.caretModel.moveToOffset(lineStartOffset + currentValueStart)
  }

  private fun getEnumValuesAtCaret(editor: Editor): List<String>? {
    val project = editor.project ?: return null
    val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) ?: return null
    val offset = editor.caretModel.offset
    val element = psiFile.findElementAt(offset) ?: return null

    return when {
      psiFile is KtFile -> {
        val refExpr = PsiTreeUtil.getParentOfType(element, KtSimpleNameExpression::class.java)
        val fullExpr = PsiTreeUtil.getParentOfType(refExpr, KtDotQualifiedExpression::class.java)
        val receiver = fullExpr?.receiverExpression

        if (receiver is KtNameReferenceExpression) {
          val resolvedReceiver = receiver.references.firstOrNull()?.resolve()
          if (resolvedReceiver is KtClass && resolvedReceiver.isEnum()) {
            resolvedReceiver.body
              ?.children
              ?.filter { it.firstChild?.node?.elementType?.toString() == "IDENTIFIER" }
              ?.map { it.text }
              ?.map { it.trim(',') }
              ?.filterNot { it.isEmpty() } ?: emptyList()
          } else {
            null
          }
        } else {
          null
        }
      }
      else -> null
    }
  }

  override fun execute(
    command: String,
    range: Range,
    editor: VimEditor,
    context: ExecutionContext,
  ) {
    // Cycle through ENUM values if we're on an ENUM entry
    val enumValuesAtCaret = getEnumValuesAtCaret(editor.ij)
    if (!enumValuesAtCaret.isNullOrEmpty()) {
      cycleToNextEnumValue(editor.ij, enumValuesAtCaret)
    }

    // Get the current line and cursor position
    val lineRange = editorAdapter.getLineRange(editor)

    // Get enabled patterns, reversing them if needed
    val definitions =
      if (reverse) {
        // For reverse, swap pattern and replacement (v->k instead of k->v)
        // and reverse lists to cycle in opposite direction
        Switch.getEnabledPatterns().map { def ->
          when (def) {
            is Map<*, *> -> def.entries.associate { (k, v) -> v.toString() to k.toString() }
            is List<*> -> def.reversed()
            else -> def
          }
        }
      } else {
        Switch.getEnabledPatterns()
      }

    // Find and apply replacement if there's a match at cursor position
    val switchMatch = matcher.findMatch(lineRange.text, lineRange.caretOffset, definitions)
    if (switchMatch != null) {
      editorAdapter.replace(editor.ij, lineRange, switchMatch)
    }
  }
}
