package com.julienphalip.ideavim.vimswitch.patterns

import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.atLineStart
import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.capture
import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.words

object RustPatterns {
  enum class PatternNames(
    val id: String,
  ) {
    RUST_VOID_TYPECHECK("rust_void_typecheck"),
    RUST_TURBOFISH("rust_turbofish"),
    RUST_STRING("rust_string"),
    RUST_IS_SOME("rust_is_some"),
    RUST_ASSERT("rust_assert"),
    CARGO_DEPENDENCY_VERSION("cargo_dependency_version"),
  }

  val patterns =
    mapOf(
      PatternNames.RUST_VOID_TYPECHECK.id to
        mapOf(
          capture("let\\s*(?:mut\\s*)?[\\w]+") + "\\s*=" to "$1: () =",
          capture("let\\s*(?:mut\\s*)?[\\w]+") + ":\\s*\\(\\)\\s*=" to "$1 =",
        ),
      PatternNames.RUST_TURBOFISH.id to
        mapOf(
          capture("[\\w]+") + "\\(" to "$1::<Todo>(",
          capture("[\\w]+") + "::<[\\w\\s<>,]+>\\(" to "$1(",
        ),
      PatternNames.RUST_STRING.id to
        mapOf(
          "\"" + capture("[^\"]+") + "\"" to "r\"$1\"",
          "r\"" + capture("[^\"]+") + "\"" to "r#\"$1\"#",
          "r#\"" + capture("[^\"]+") + "\"#" to "\"$1\"",
        ),
      PatternNames.RUST_IS_SOME.id to words("is_some", "is_none"),
      PatternNames.RUST_ASSERT.id to words("assert_eq!", "assert_ne!"),
      PatternNames.CARGO_DEPENDENCY_VERSION.id to
        mapOf(
          atLineStart(capture("[\\w-]+") + "\\s*=\\s*" + capture("[\"'].+[\"']")) to
            "$1 = { version = $2 }",
          atLineStart(
            capture("[\\w-]+") +
              "\\s*=\\s*\\{\\s*version\\s*=\\s*" +
              capture("[\"'].+[\"']") +
              "\\s*\\}",
          ) to "$1 = $2",
        ),
    )
}
