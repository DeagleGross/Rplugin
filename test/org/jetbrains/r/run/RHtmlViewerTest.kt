/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.run

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import junit.framework.TestCase
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.resolvedPromise
import org.jetbrains.r.rinterop.RInterop

class RHtmlViewerTest : RProcessHandlerBaseTestCase() {
  override fun setUp() {
    super.setUp()

    // Setup custom "default" browser
    // which should be used for URLs starting with either 'https:' or 'http:'.
    // That means all web request will be redirected to R stdout
    rInterop.executeCode(".jetbrains $ ther_old_browser <<- function(url) { cat(url) }")
    rInterop.asyncEventsStartProcessing()
  }

  fun testOneFileUrl() {
    browseLocalUrl("no-such-url")
  }

  fun testTwoFileUrls() {
    browseLocalUrl("no-such-url1")
    browseLocalUrl("no-such-url2")
  }

  fun testAbsoluteLocalUrl() {
    browseLocalUrl("/tmp/rplugin/urls.txt")
  }

  fun testQualifiedLocalUrl() {
    browseLocalUrl("file:///tmp/rplugin/urls.txt")
  }

  fun testWwwUrl() {
    // Looks like it should be open in web browser but RStudio doesn't think so.
    // Thus our custom browsing function should treat it as a local URL
    browseLocalUrl("www.google.com")
  }

  fun testComUrl() {
    // The same as in 'testWwwUrl'
    browseLocalUrl("google.com")
  }

  fun testHttpsUrl() {
    browseWebUrl("https://google.com")
  }

  fun testHttpUrl() {
    browseWebUrl("http://google.com")
  }

  fun testPager() {
    var actualPath: String? = null
    var actualTitle: String? = null
    rInterop.addAsyncEventsListener(object : RInterop.AsyncEventsListener {
      override fun onShowFileRequest(filePath: String, title: String): Promise<Unit> {
        actualPath = filePath
        actualTitle = title
        return resolvedPromise()
      }
    })
    rInterop.asyncEventsStartProcessing()
    val file = FileUtil.createTempFile("rplugin", ".txt").also { it.deleteOnExit() }
    rInterop.executeCode("options()\$pager('${StringUtil.escapeStringCharacters(file.absolutePath)}', title = 'mytitle')")
    TestCase.assertEquals(file.absolutePath, actualPath)
    TestCase.assertEquals("mytitle", actualTitle)
  }

  private fun browseLocalUrl(expectedUrl: String) {
    var actualUrl: String? = null
    val listener = object : RInterop.AsyncEventsListener {
      override fun onShowFileRequest(filePath: String, title: String): Promise<Unit> {
        actualUrl = filePath
        return resolvedPromise()
      }
    }.also { rInterop.addAsyncEventsListener(it) }
    rInterop.executeCode("browseURL('$expectedUrl')")
    TestCase.assertEquals(expectedUrl, actualUrl)
    rInterop.removeAsyncEventsListener(listener)
  }

  private fun browseWebUrl(expectedUrl: String) {
    var wasShowFileRequest = false
    val listener = object : RInterop.AsyncEventsListener {
      override fun onShowFileRequest(filePath: String, title: String): Promise<Unit> {
        wasShowFileRequest = true
        return resolvedPromise()
      }
    }.also { rInterop.addAsyncEventsListener(it) }
    val actualUrl = rInterop.executeCode("browseURL('$expectedUrl')").stdout
    TestCase.assertFalse(wasShowFileRequest)
    TestCase.assertEquals(expectedUrl, actualUrl)
    rInterop.removeAsyncEventsListener(listener)
  }
}