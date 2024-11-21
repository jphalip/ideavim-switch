package com.julienphalip.ideavim.vimswitch.patterns

import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.capture
import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.group
import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.optionalCapture
import com.julienphalip.ideavim.vimswitch.patterns.PatternUtils.word

object JavaScriptPatterns {
  enum class PatternNames(
    val id: String,
  ) {
    JS_FUNCTION("javascript_function"),
    JS_ARROW_FUNCTION("javascript_arrow_function"),
    JS_ES6_DECLARATIONS("javascript_es6_declarations"),
  }

  val patterns =
    mapOf(
      PatternNames.JS_FUNCTION.id to
        mapOf(
          optionalCapture("async\\s+") + "function\\s*" + capture("\\w+") + "\\s*\\(\\)\\s*\\{" to
            "const $2 = $1() => {",
          optionalCapture("async\\s+") +
            "function\\s*" +
            capture("\\w+") +
            "\\s*\\(" +
            capture("[^()]+") +
            "\\)\\s*\\{" to "const $2 = $1($3) => {",
          group("var|let|const") +
            "\\s+" +
            capture("\\w+") +
            "\\s*=\\s*" +
            optionalCapture("async\\s+") +
            "function\\s*\\(" to "$2function $1(",
        ),
      PatternNames.JS_ARROW_FUNCTION.id to
        mapOf(
          "function\\s*\\(\\)\\s*\\{" to "() => {",
          "function\\s*\\(" + capture("[^()]+") + "\\)\\s*\\{" to "($1) => {",
          "\\(" + capture("[^()]+") + "\\)\\s*=>\\s*\\{" to "function($1) {",
          capture("\\w+") + "\\s*=>\\s*\\{" to "function($1) {",
        ),
      PatternNames.JS_ES6_DECLARATIONS.id to
        mapOf(
          word("var") + "\\s+" to "let ",
          word("let") + "\\s+" to "const ",
          word("const") + "\\s+" to "let ",
        ),
    )
}
