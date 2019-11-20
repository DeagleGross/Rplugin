/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.rinterop

import com.intellij.openapi.util.SystemInfo
import junit.framework.TestCase
import org.jetbrains.concurrency.AsyncPromise
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.resolvedPromise
import org.jetbrains.r.run.RProcessHandlerBaseTestCase

class RInteropTest : RProcessHandlerBaseTestCase() {
  fun testExecuteCode() {
    val result = rInterop.executeCode("""
      print(123)
      x = 1
      x + 2
      cat("err\n", file=stderr())
    """.trimIndent())
    TestCase.assertEquals("[1] 123\n[1] 3\n", result.stdout)
    TestCase.assertEquals("err\n", result.stderr)
  }

  fun testExecuteBigCommand() {
    rInterop.executeCode("x <- 123")
    val result = rInterop.executeCode("""print(if (typeof(environment()${'$'}x) == "closure" && isdebugged(environment()${'$'}x)) attr(environment()${'$'}x, "original") else environment()${'$'}x)""")
    TestCase.assertEquals("[1] 123\n", result.stdout)
    TestCase.assertEquals("", result.stderr)
  }

  fun testExecuteLongStringLiteral() {
    val long = "a".repeat(1000)
    val result = rInterop.executeCode("cat(\"$long\")")
    TestCase.assertEquals(long, result.stdout)
    TestCase.assertEquals("", result.stderr)
  }

  fun testExecuteMultilineWithFor() {
    val result = rInterop.executeCode("""
      x <- 312312
      print(x)
      for (i in c(1,2,3,4,5,6)) {
        print(i)
      }
      z <- function(x) {
        print(x)
      }
      z(123)
      for (i in c(1,2,3,4,5,6,7,8,9)) {
        z(i)
      }
      print("Finish")
    """.trimIndent())
    TestCase.assertEquals("""
      [1] 312312
      [1] 1
      [1] 2
      [1] 3
      [1] 4
      [1] 5
      [1] 6
      [1] 123
      [1] 1
      [1] 2
      [1] 3
      [1] 4
      [1] 5
      [1] 6
      [1] 7
      [1] 8
      [1] 9
      [1] "Finish"
    """.trimIndent(), result.stdout.trim())
    TestCase.assertEquals("", result.stderr)
  }

  fun testExecuteError() {
    rInterop.executeCode("""
      Sys.setlocale(\"LC_MESSAGES\", 'en_GB.UTF-8')
      Sys.setenv(LANG = \"en_US.UTF-8\")
    """.trimIndent())
    val result = rInterop.executeCode("foo()")
    TestCase.assertEquals(result.stdout, "")
    TestCase.assertTrue(result.stderr.contains("could not find function \"foo\""))
  }

  fun testExecuteCodeInterrupt() {
    val promise = rInterop.executeCodeAsync("x <- 0; while (TRUE) x = x + 1")
    Thread.sleep(100)
    promise.cancel()
    TestCase.assertEquals("[1] 123\n", rInterop.executeCode("123").stdout)
  }

  fun testReplReadline() {
    rInterop.replExecute("x = 0")
    rInterop.replExecute("""
      for (i in 1:3) {
        x = x + as.integer(readline())
      }
    """.trimIndent())
    rInterop.replSendReadLn("100")
    rInterop.replSendReadLn("200")
    rInterop.replSendReadLn("300")
    TestCase.assertEquals("[1] 600\n", rInterop.executeCode("x").stdout)
  }

  fun testViewRequest() {
    val promise = AsyncPromise<Pair<RValue, String>>()
    rInterop.addReplListener(object : RInterop.ReplListener {
      override fun onViewRequest(ref: RRef, title: String, value: RValue): Promise<Unit> {
        promise.setResult(value to title)
        return resolvedPromise()
      }
    })
    rInterop.replStartProcessing()
    rInterop.replExecute("tt = data.frame(x = 1:9, y = 2:10)")
    rInterop.replExecute("View(tt, 'abcdd')")
    val (value, title) = promise.blockingGet(DEFAULT_TIMEOUT)!!
    TestCase.assertEquals("abcdd", title)
    TestCase.assertTrue(value is RValueDataFrame)
    TestCase.assertEquals(9, (value as RValueDataFrame).rows)
  }

  fun testParallelPrint() {
    if (!SystemInfo.isUnix) {
      // As stated in parallel::mclapply documentation, it does not work on Windows
      return
    }
    val result = rInterop.executeCode("""
      a <- parallel::mclapply(1:9, mc.cores = 2, function(x) {
        cat(x)
        for (i in 1:x) cat("!", file=stderr())
        return(x*x)
      })
    """.trimIndent())
    TestCase.assertEquals("123456789", result.stdout.asSequence().sorted().joinToString(""))
    TestCase.assertEquals("!".repeat(9 * 10 / 2), result.stderr)
    TestCase.assertEquals((1..9).map { (it * it).toString() }, RRef.expressionRef("as.character(a)", rInterop).evaluateAsStringList())
  }
}