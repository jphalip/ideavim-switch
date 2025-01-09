package com.julienphalip.ideavim.vimswitch

import org.assertj.core.api.Assertions.assertThat

class JavaPatternsTest : SwitchTestBase() {
  override fun getDefinitions(): String = "group:java"

  fun testJavaAssertions() {
    assertThat(switch("assert<caret>Equals(expected, actual)"))
      .isEqualTo("assert<caret>NotEquals(expected, actual)")

    assertThat(switch("assertNot<caret>Equals(expected, actual)"))
      .isEqualTo("assertEqu<caret>als(expected, actual)")

    assertThat(switch("assert<caret>True(condition)")).isEqualTo("assert<caret>False(condition)")

    assertThat(switch("assert<caret>False(condition)")).isEqualTo("assert<caret>True(condition)")

    assertThat(switch("assert<caret>Null(object)")).isEqualTo("assert<caret>NotNull(object)")

    assertThat(switch("assertNot<caret>Null(object)")).isEqualTo("assertNul<caret>l(object)")
  }

  fun testJavaOptionalChecks() {
    assertThat(switch("optional.isPre<caret>sent()")).isEqualTo("optional.isEmp<caret>ty()")

    assertThat(switch("optional.isEm<caret>pty()")).isEqualTo("optional.isPr<caret>esent()")
  }

  fun testJavaVisibilityModifiers() {
    assertThat(switch("pri<caret>vate void method()")).isEqualTo("pro<caret>tected void method()")

    assertThat(switch("pro<caret>tected void method()")).isEqualTo("pub<caret>lic void method()")

    assertThat(switch("pub<caret>lic void method()")).isEqualTo("pri<caret>vate void method()")
  }

  fun testSwitchWithCursorAtDifferentPositions() {
    assertThat(switch("assert<caret>True(condition)")).isEqualTo("assert<caret>False(condition)")

    assertThat(switch("assertT<caret>rue(condition)")).isEqualTo("assertF<caret>alse(condition)")

    assertThat(switch("assertTr<caret>ue(condition)")).isEqualTo("assertFa<caret>lse(condition)")
  }
}
