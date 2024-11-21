package com.julienphalip.ideavim.vimswitch.patterns

object MarkdownPatterns {
  enum class PatternNames(
    val id: String,
  ) {
    MARKDOWN_TASK_ITEM("markdown_task_item"),
  }

  val patterns =
    mapOf(
      PatternNames.MARKDOWN_TASK_ITEM.id to
        mapOf(
          "^\\s*- \\[ \\]" to "- [x]",
          "^\\s*- \\[x\\]" to "- [ ]",
        ),
    )
}
