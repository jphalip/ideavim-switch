package com.julienphalip.ideavim.vimswitch

// Represents a match that should be replaced, with its position and replacement text
data class SwitchMatch(
  val start: Int,
  val end: Int,
  val replacement: String,
)
