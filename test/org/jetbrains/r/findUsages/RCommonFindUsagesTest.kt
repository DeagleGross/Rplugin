/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.findUsages

import com.intellij.testFramework.UsefulTestCase
import com.intellij.usages.PsiElementUsageTarget
import com.intellij.usages.UsageTargetUtil
import org.intellij.lang.annotations.Language
import org.jetbrains.r.run.RProcessHandlerBaseTestCase

class RCommonFindUsagesTest  : FindUsagesTestBase() {
  fun testLocalVariable() {
    doTest("""
      my.local.<caret>variable <- 10
      print(my.local.variable)
      print("hello")
      some.function <- function() {
        print(my.local.variable + 20)
      }
    """, """
      Usage (2 usages)
       Variable
        my.local.variable
       Found usages (2 usages)
        Unclassified (2 usages)
         light_idea_test_case (2 usages)
           (2 usages)
           2print(my.local.variable)
           5print(my.local.variable + 20)
    """)
  }

  //TODO: Fix R-334
  fun testLocalFunction() {
    doTest("""
      my.local.<caret>function <- function(x, y) x + y
      print(my.local.function(2, 3))
      print("hello")
      some.other.function <- function() {
        print(my.local.function(4, 5))
      }
    """, """
      Usage (2 usages)
       Variable
        my.local.function
       Found usages (2 usages)
        Unclassified (2 usages)
         light_idea_test_case (2 usages)
           (2 usages)
           2print(my.local.function(2, 3))
           5print(my.local.function(4, 5))
     """)
  }

  fun testLibraryFunction() {
    doTest("""
      base.package <- packageDescription("base")      
      dplyr.package <- package<caret>Description("dplyr")      
    """, """
      Usage (2 usages)
       Variable
        packageDescription(pkg, lib.loc = NULL, fields = NULL, drop = TRUE, encoding = "")
       Found usages (2 usages)
        Unclassified (2 usages)
         light_idea_test_case (2 usages)
           (2 usages)
           1base.package <- packageDescription("base")      
           2dplyr.package <- packageDescription("dplyr")
     """)
  }

  fun testParameter() {
    doTest("""
      func <- function(x<caret>, y, z) {
        x + y + z
      }
      
      x <- 15
      p <- x + 10
      func(x = p)
    """, """
      Usage (2 usages)
       Variable
        x
       Found usages (2 usages)
        Unclassified (2 usages)
         light_idea_test_case (2 usages)
           (2 usages)
           2x + y + z
           7func(x = p)
     """)
  }

  fun testRoxygenParameter() {
    doTest("""
      #' Title
      #' 
      #' Description
      #'
      #' @param x, y X and y
      #' @param z Z
      #' @example
      #' #' @param x,y,z Params
      func <- function(x<caret>, y, z) {
        x + y + z
      }
    """, """
      Usage (2 usages)
       Variable
        x
       Found usages (2 usages)
        Unclassified (1 usage)
         light_idea_test_case (1 usage)
           (1 usage)
           10x + y + z
        Usage in roxygen2 documentation (1 usage)
         light_idea_test_case (1 usage)
           (1 usage)
           5#' @param x, y X and y
     """)
  }

  fun testRoxygenHelpPageLink() {
    doTest("""
      #' Title
      #' 
      #' Description
      #'
      #' @see [bar]
      #' [bar][baz]
      #' [bar](baz)
      #' <bar:bar>
      func <- function(x, y, z) {
        bar(x) + y + z
      }
      
      ba<caret>r <- function(x) { x + 42 }
    """, """
      Usage (2 usages)
       Variable
        bar
       Found usages (2 usages)
        Unclassified (1 usage)
         light_idea_test_case (1 usage)
           (1 usage)
           10bar(x) + y + z
        Usage in roxygen2 documentation (1 usage)
         light_idea_test_case (1 usage)
           (1 usage)
           5#' @see [bar]
     """)
  }
}