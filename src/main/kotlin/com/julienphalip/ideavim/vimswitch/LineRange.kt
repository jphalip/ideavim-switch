package com.julienphalip.ideavim.vimswitch

// Contains line information for editor operations
data class LineRange(
  // Text content of the line
  val text: String,
  // Offset where the line starts in the document
  val start: Int,
  // Offset where the line ends in the document
  val end: Int,
  // Cursor position relative to line start
  val caretOffset: Int,
)
