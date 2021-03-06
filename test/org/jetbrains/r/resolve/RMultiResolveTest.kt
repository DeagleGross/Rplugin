/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.resolve

import junit.framework.TestCase
import org.jetbrains.r.RLightCodeInsightFixtureTestCase

class RMultiResolveTest : RLightCodeInsightFixtureTestCase() {
  override fun setUp() {
    super.setUp()
    addLibraries()
  }

  fun testMultiResolve() {
    doTest(2,
           """
           filt<caret>er()
           """.trimIndent())
  }

  fun testMultiResolveLocal() {
    doTest( 1,
            """
            filter <- function(x)x
            filt<caret>er()
            """.trimIndent())
  }

  fun testMultiResolveFunction() {
    doTest( 1,
            """
            filter <- function(x)x
            function() { filt<caret>er() }
            """.trimIndent())
  }

  fun testMultiResolveNamespaceAccess() {
    doTest(1, """
      filter <- function(x, y)
      dplyr::fil<caret>ter()
    """.trimIndent())
  }

  fun testMultiResolveInDocumentation() {
    doTest(2, """
      #' [filt<caret>er()]
      NULL
    """.trimIndent())
  }

  private fun doTest(count: Int,  text: String) {
    myFixture.configureByText("test.R", text)
    val results = resolve()
    TestCase.assertEquals(results.size, count)
  }
}