package com.julienphalip.ideavim.vimswitch.patterns

object RubyPatterns {
  enum class PatternNames(
    val id: String,
  ) {
    RUBY_HASH_STYLE("ruby_hash_style"),
    RUBY_ONELINE_HASH("ruby_oneline_hash"),
    RUBY_LAMBDA("ruby_lambda"),
    RUBY_IF_CLAUSE("ruby_if_clause"),
    RUBY_ARRAY_SHORTHAND("ruby_array_shorthand"),
    RUBY_FETCH("ruby_fetch"),
    RUBY_ASSERT_NIL("ruby_assert_nil"),
  }

  val patterns =
    mapOf(
      PatternNames.RUBY_HASH_STYLE.id to
        mapOf(
          ":([\\w]+)\\s*=>\\s*" to "$1: ",
          "([\\w]+):\\s" to ":$1 => ",
        ),
      PatternNames.RUBY_ONELINE_HASH.id to
        mapOf(
          ":([\\w]+)\\s*=>" to "$1:",
          "([\\w]+):" to ":$1 =>",
        ),
      PatternNames.RUBY_LAMBDA.id to
        mapOf(
          "lambda\\s*\\{\\s*\\|([^|]+)\\|" to "->($1) {",
          "->\\s*\\(([^)]+)\\)\\s*\\{" to "lambda { |$1|",
          "lambda\\s*\\{" to "-> {",
          "->\\s*\\{" to "lambda {",
        ),
      PatternNames.RUBY_IF_CLAUSE.id to
        mapOf(
          "if\\s+true\\s+or\\s+\\(([^)]+)\\)" to "if false and ($1)",
          "if\\s+false\\s+and\\s+\\(([^)]+)\\)" to "if $1",
          "if\\s+(?!true|false)([^\\s]+)" to "if true or ($1)",
        ),
      PatternNames.RUBY_ARRAY_SHORTHAND.id to
        mapOf(
          "\\[\\s*'([^']+)'(?:\\s*,\\s*'([^']+)')*\\s*\\]" to "%w($1 $2)",
          "%w\\(([^)]+)\\)" to "['$1']".replace(" ", "', '"),
        ),
      PatternNames.RUBY_FETCH.id to
        mapOf(
          "([\\w]+)\\[([^\\]]+)\\]" to "$1.fetch($2)",
          "([\\w]+)\\.fetch\\(([^)]+)\\)" to "$1[$2]",
        ),
      PatternNames.RUBY_ASSERT_NIL.id to
        mapOf(
          "assert_equal\\s+nil,\\s*([^\\s]+)" to "assert_nil $1",
          "assert_nil\\s+([^\\s]+)" to "assert_equal nil, $1",
        ),
    )
}
