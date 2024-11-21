package com.julienphalip.ideavim.vimswitch

import org.assertj.core.api.Assertions.assertThat

class MarkdownPatternsTest : SwitchTestBase() {
  override fun getPatternString(): String = "group:markdown"

  fun testBasicTaskToggle() {
    assertThat(switch("- [<caret> ] Task")).isEqualTo("- [<caret>x] Task")

    assertThat(switch("- [<caret>x] Task")).isEqualTo("- [<caret> ] Task")
  }

  fun testTasksWithIndentation() {
    assertThat(switch("  - [<caret> ] Indented task")).isEqualTo("  - [<caret>x] Indented task")

    assertThat(switch("    - [<caret>x] Double indented"))
      .isEqualTo("    - [<caret> ] Double indented")
  }

  fun testTasksWithDifferentCaretPositions() {
    // On the checkbox
    assertThat(switch("- [ <caret>] Task")).isEqualTo("- [x<caret>] Task")

    assertThat(switch("- [x<caret>] Task")).isEqualTo("- [ <caret>] Task")

    // On the dash
    assertThat(switch("-<caret> [ ] Task")).isEqualTo("-<caret> [x] Task")

    // On the task text
    assertThat(switch("- [ ] Task<caret>")).isEqualTo("- [ ] Task<caret>")
  }

  fun testTasksWithVariousContent() {
    assertThat(switch("- [<caret> ] Task with * special ^ characters"))
      .isEqualTo("- [<caret>x] Task with * special ^ characters")

    assertThat(switch("- [<caret>x] Task with [link](url)"))
      .isEqualTo("- [<caret> ] Task with [link](url)")

    assertThat(switch("- [<caret> ] Task with **bold** and *italic*"))
      .isEqualTo("- [<caret>x] Task with **bold** and *italic*")
  }

  fun testNoMatchForInvalidSyntax() {
    // Missing space after dash
    val invalid1 = "-[<caret> ] Invalid"
    assertThat(switch(invalid1)).isEqualTo(invalid1)

    // Wrong checkbox syntax
    val invalid2 = "- (<caret> ) Wrong brackets"
    assertThat(switch(invalid2)).isEqualTo(invalid2)

    // Not at start of line
    val invalid3 = "Some text - [<caret> ] Invalid"
    assertThat(switch(invalid3)).isEqualTo(invalid3)
  }
}
