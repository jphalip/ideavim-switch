package com.julienphalip.ideavim.vimswitch

import org.assertj.core.api.Assertions.assertThat

class BasicPatternsTest : SwitchTestBase() {
  override fun getPatternString(): String = "group:basic"

  fun testBinary() {
    assertThat(switch("value = <caret>0")).isEqualTo("value = <caret>1")
    assertThat(switch("value = <caret>1")).isEqualTo("value = <caret>0")
  }

  fun testUpDownLeftRight() {
    assertThat(switch("value = '<caret>up'")).isEqualTo("value = '<caret>down'")
    assertThat(switch("value = '<caret>down'")).isEqualTo("value = '<caret>left'")
    assertThat(switch("value = '<caret>left'")).isEqualTo("value = '<caret>right'")
    assertThat(switch("value = '<caret>right'")).isEqualTo("value = '<caret>up'")
  }

  fun testLogicalOps() {
    assertThat(switch("if (x <caret>&& y)")).isEqualTo("if (x <caret>|| y)")

    assertThat(switch("if (x <caret>|| y)")).isEqualTo("if (x <caret>&& y)")

    // Test with different caret positions
    assertThat(switch("if (x &<caret>& y)")).isEqualTo("if (x |<caret>| y)")

    assertThat(switch("if (x |<caret>| y)")).isEqualTo("if (x &<caret>& y)")
  }

  fun testBitwiseOps() {
    assertThat(switch("if (x <caret>& y)")).isEqualTo("if (x <caret>| y)")

    assertThat(switch("if (x <caret>| y)")).isEqualTo("if (x <caret>& y)")

    // Test with different caret positions
    assertThat(switch("if (x <caret>& y)")).isEqualTo("if (x <caret>| y)")

    assertThat(switch("if (x <caret>| y)")).isEqualTo("if (x <caret>& y)")
  }

  fun testCapitalTrueFalse() {
    assertThat(switch("if (<caret>True)")).isEqualTo("if (<caret>False)")

    assertThat(switch("if (<caret>False)")).isEqualTo("if (<caret>True)")

    // Test with different caret positions
    assertThat(switch("if (Tr<caret>ue)")).isEqualTo("if (Fa<caret>lse)")

    assertThat(switch("if (Fal<caret>se)")).isEqualTo("if (Tru<caret>e)")
  }

  fun testLowercaseTrueFalse() {
    assertThat(switch("if (<caret>true)")).isEqualTo("if (<caret>false)")

    assertThat(switch("if (<caret>false)")).isEqualTo("if (<caret>true)")

    // Test with different caret positions
    assertThat(switch("if (tr<caret>ue)")).isEqualTo("if (fa<caret>lse)")

    assertThat(switch("if (fal<caret>se)")).isEqualTo("if (tru<caret>e)")
  }

  fun testNoMatchForPartialWords() {
    // Should not match parts of larger words
    assertThat(switch("if (something<caret>true)")).isEqualTo("if (something<caret>true)")

    assertThat(switch("if (truly<caret>False)")).isEqualTo("if (truly<caret>False)")
  }

  fun testMultipleOccurrencesChoosesClosestToCursor() {
    assertThat(switch("if (true && <caret>true)")).isEqualTo("if (true && <caret>false)")

    assertThat(switch("if (<caret>true && false)")).isEqualTo("if (<caret>false && false)")
  }

  fun testWithSurroundingSpacesAndPunctuation() {
    assertThat(switch("value = <caret>true;")).isEqualTo("value = <caret>false;")

    assertThat(switch("return <caret>True,")).isEqualTo("return <caret>False,")
  }

  fun testAndOr() {
    assertThat(switch("if (x <caret>and y)")).isEqualTo("if (x <caret>or y)")

    assertThat(switch("if (x <caret>or y)")).isEqualTo("if (x <caret>and y)")

    // Test with different caret positions
    assertThat(switch("if (x a<caret>nd y)")).isEqualTo("if (x o<caret>r y)")

    assertThat(switch("if (x o<caret>r y)")).isEqualTo("if (x a<caret>nd y)")
  }

  fun testUppercaseAndOr() {
    assertThat(switch("if (x <caret>AND y)")).isEqualTo("if (x <caret>OR y)")

    assertThat(switch("if (x <caret>OR y)")).isEqualTo("if (x <caret>AND y)")

    // Test with different caret positions
    assertThat(switch("if (x A<caret>ND y)")).isEqualTo("if (x O<caret>R y)")

    assertThat(switch("if (x O<caret>R y)")).isEqualTo("if (x A<caret>ND y)")
  }

  fun testAndOrWithWordBoundaries() {
    // Should not match parts of larger words
    assertThat(switch("if (expand<caret>or)")).isEqualTo("if (expand<caret>or)")

    assertThat(switch("if (command<caret>and)")).isEqualTo("if (command<caret>and)")
  }

  fun testAndOrWithSurroundingPunctuation() {
    assertThat(switch("(x <caret>and y);")).isEqualTo("(x <caret>or y);")

    assertThat(switch("return x <caret>AND y,")).isEqualTo("return x <caret>OR y,")
  }

  fun testEquality() {
    assertThat(switch("if x <caret>== y")).isEqualTo("if x <caret>!= y")

    assertThat(switch("if x <caret>!= y")).isEqualTo("if x <caret>== y")

    // Test with different spacing
    assertThat(switch("if x<caret>==y")).isEqualTo("if x<caret>!=y")

    assertThat(switch("if x  <caret>==  y")).isEqualTo("if x  <caret>!=  y")
  }

  fun testIsIsNot() {
    assertThat(switch("if x <caret>is y")).isEqualTo("if x <caret>is not y")

    assertThat(switch("if x <caret>is not y")).isEqualTo("if x <caret>is y")

    // Test with different spacing
    assertThat(switch("if x<caret>is y")).isEqualTo("if x<caret>is y")

    assertThat(switch("if x  <caret>is  not  y")).isEqualTo("if x  <caret>is  y")
  }

  fun testQuotes() {
    assertThat(switch("value = \"<caret>hello\"")).isEqualTo("value = '<caret>hello'")

    assertThat(switch("value = '<caret>hello'")).isEqualTo("value = `<caret>hello`")

    assertThat(switch("value = `<caret>hello`")).isEqualTo("value = \"<caret>hello\"")

    // Test with multiple quotes on same line
    assertThat(switch("value = \"first\" + \"<caret>second\""))
      .isEqualTo("value = \"first\" + '<caret>second'")

    assertThat(switch("value = '<caret>first' + 'second'"))
      .isEqualTo("value = `<caret>first` + 'second'")
  }
}
