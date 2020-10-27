package org.jetbrains.r.editor

import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.impl.EditorImpl
import org.jetbrains.plugins.notebooks.editor.CodeCellLinesChecker
import org.jetbrains.plugins.notebooks.editor.NotebookCellLines.CellType.*
import org.jetbrains.plugins.notebooks.editor.edt
import org.junit.Test

class RMarkdownCellLinesTest: RMarkdownEditorUiTestBase() {
  @Test
  fun `test notebook with text`(): Unit = edt{
    fixture.openNotebookTextInEditor("""
      line 1
      line 2
      line 3
    """.trimIndent())

    assertCodeCells {
      markers {
        marker(MARKDOWN, 0, 20)
      }
      intervals {
        interval(MARKDOWN, 0..2)
      }
    }
  }

  @Test
  fun `test notebook with code`(): Unit = edt{
    fixture.openNotebookTextInEditor("""
      ```{r}
      code line
      ```
    """.trimIndent())

    assertCodeCells {
      markers {
        marker(CODE, 0, 20)
      }
      intervals {
        interval(CODE, 0..2)
      }
    }
  }

  @Test
  fun `test code options`(): Unit = edt{
    fixture.openNotebookTextInEditor("""
      ```{r setup}
      ```
      ```{python}
      ```
      ```{r chunk-one}
      ```
    """.trimIndent())

    assertCodeCells {
      markers {
        marker(CODE, 0, 17)
        marker(CODE, 17, 16)
        marker(CODE, 33, 20)
      }
      intervals {
        interval(CODE, 0..1)
        interval(CODE, 2..3)
        interval(CODE, 4..5)
      }
    }
  }

  @Test
  fun `test uncompleted code cell`(): Unit = edt{
    fixture.openNotebookTextInEditor("""
      ```{r setup}
      actual markdown
      `
    """.trimIndent())

    assertCodeCells {
      markers {
        marker(MARKDOWN, 0, 30)
      }
      intervals {
        interval(MARKDOWN, 0..2)
      }
    }
  }

  @Test
  fun `two code cells`(): Unit = edt{
    fixture.openNotebookTextInEditor("""
      ```{r}
      code cell 1
      ```
      ```{r}
      code cell 2
      ```
    """.trimIndent())

    assertCodeCells {
      markers {
        marker(CODE, 0, 23)
        marker(CODE, 23, 22)
      }
      intervals {
        interval(CODE, 0..2)
        interval(CODE, 3..5)
      }
    }
  }

  @Test
  fun `rmd notebook header`(): Unit = edt{
    fixture.openNotebookTextInEditor("""
      ---
      title: "title"
      author: author
      date: 9/11/20
      output: html_notebook
      ---
      markdown_cell_1
      ---
      still_cell_1
      ---
    """.trimIndent())

    assertCodeCells {
      markers {
        marker(MARKDOWN, 0, 74)
        marker(MARKDOWN, 74, 36)
      }
      intervals {
        interval(MARKDOWN, 0..5)
        interval(MARKDOWN, 6..9)
      }
    }
  }

  @Test
  fun `single line code cells`(): Unit = edt {
    fixture.openNotebookTextInEditor("""
      ```{r}```
      ```{r} code ```
    """.trimIndent())

    assertCodeCells {
      markers {
        marker(CODE, 0, 10)
        marker(CODE, 10, 15)
      }
      intervals {
        interval(CODE, 0..0)
        interval(CODE, 1..1)
      }
    }
  }

  @Test
  fun `test add text to empty document and delete`(): Unit = edt {
    fixture.openNotebookTextInEditor("")

    assertCodeCells("add text") {
      fixture.type("add\nmultiline text")
      markers {
        marker(MARKDOWN, 0, 18)
      }
      intervals {
        interval(MARKDOWN, 0..1)
      }
      intervalListenerCall(0) {
        before {
          interval(RAW, 0..0)
        }
        after {
          interval(MARKDOWN, 0..0)
        }
      }
      intervalListenerCall(0) {
        before {
          interval(MARKDOWN, 0..0)
        }
        after {
          interval(MARKDOWN, 0..1)
        }
      }
    }

    assertCodeCells("remove all text") {
      fixture.performEditorAction(IdeActions.ACTION_SELECT_ALL)
      fixture.performEditorAction(IdeActions.ACTION_EDITOR_DELETE)
      markers {  }
      intervals {
        interval(RAW, 0..0)
      }
      intervalListenerCall(0){
        before{
          interval(MARKDOWN, 0..1)
        }
        after {
          interval(RAW, 0..0)
        }
      }
    }
  }

  @Test
  fun `test add code to empty document and delete`(): Unit = edt {
    fixture.openNotebookTextInEditor("")

    assertCodeCells("add code") {
      // for first ` ide actually types `<caret>`
      fixture.type("""
        ```{r}
        code
        ``
      """.trimIndent())
      markers {
        marker(CODE, 0, 15)
      }
      intervals {
        interval(CODE, 0..2)
      }
      intervalListenerCall(0) {
        before {
          interval(RAW, 0..0)
        }
        after {
          interval(MARKDOWN, 0..0)
        }
      }
      intervalListenerCall(0) {
        before {
          interval(MARKDOWN, 0..0)
        }
        after {
          interval(MARKDOWN, 0..1)
        }
      }
      intervalListenerCall(0) {
        before {
          interval(MARKDOWN, 0..1)
        }
        after {
          interval(MARKDOWN, 0..2)
        }
      }
      intervalListenerCall(0) {
        before {
          interval(MARKDOWN, 0..2)
        }
        after {
          interval(CODE, 0..2)
        }
      }
    }

    assertCodeCells("remove code") {
      fixture.performEditorAction(IdeActions.ACTION_SELECT_ALL)
      fixture.performEditorAction(IdeActions.ACTION_EDITOR_DELETE)
      markers {}
      intervals {
        interval(RAW, 0..0)
      }
      intervalListenerCall(0) {
        before {
          interval(CODE, 0..2)
        }
        after {
          interval(RAW, 0..0)
        }
      }
    }
  }

  @Test
  fun `test insert code cell`(): Unit = edt {
    fixture.openNotebookTextInEditor("""
      markdown line 1
      <caret>
      markdown line 2
    """.trimIndent())

    assertCodeCells {
      markers {
        marker(MARKDOWN, 0, 32)
      }
      intervals {
        interval(MARKDOWN, 0..2)
      }
    }

    assertCodeCells("insert ```") {
      fixture.type("```")
      // actually it types ```<caret>`,
      // ``` is valid start of cell, but ```` is invalid and ignored
      // so cell added and removed
      markers {
        marker(MARKDOWN, 0, 36)
      }
      intervals {
        interval(MARKDOWN, 0..2)
      }
      intervalListenerCall(0) {
        before {
          interval(MARKDOWN, 0..2)
        }
        after {
          interval(MARKDOWN, 0..0)
          interval(MARKDOWN, 1..2)
        }
      }
      intervalListenerCall(0) {
        before {
          interval(MARKDOWN, 0..0)
          interval(MARKDOWN, 1..2)
        }
        after {
          interval(MARKDOWN, 0..2)
        }
      }
    }

    assertCodeCells("complete start of the cell to ```{r}") {
      fixture.type("{r}")
      markers {
        marker(MARKDOWN, 0, 16)
        marker(MARKDOWN, 16, 23)
      }
      intervals {
        interval(MARKDOWN, 0..0)
        interval(MARKDOWN, 1..2)
      }
      intervalListenerCall(0) {
        before {
          interval(MARKDOWN, 0..2)
        }
        after {
          interval(MARKDOWN, 0..0)
          interval(MARKDOWN, 1..2)
        }
      }
    }

    assertCodeCells("add code cell end") {
      // one ` was already typed and placed after caret
      fixture.type("``")
      markers {
        marker(MARKDOWN, 0, 16)
        marker(CODE, 16, 10)
        marker(MARKDOWN, 26, 15)
      }
      intervals {
        interval(MARKDOWN, 0..0)
        interval(CODE, 1..1)
        interval(MARKDOWN, 2..2)
      }
      intervalListenerCall(1) {
        before {
          interval(MARKDOWN, 1..2)
        }
        after {
          interval(CODE, 1..1)
          interval(MARKDOWN, 2..2)
        }
      }
    }
  }

  @Test
  fun `test markdown merge when remove code cell`(): Unit = edt {
    fixture.openNotebookTextInEditor("""
      line 0
      ```{r} code```<caret>
      line 1
    """.trimIndent())

    assertCodeCells {
      markers {
        marker(MARKDOWN, 0,7 )
        marker(CODE, 7, 15)
        marker(MARKDOWN, 22, 6)
      }
      intervals {
        interval(MARKDOWN, 0..0)
        interval(CODE, 1..1)
        interval(MARKDOWN, 2..2)
      }
    }

    assertCodeCells {
      fixture.performEditorAction(IdeActions.ACTION_EDITOR_DELETE_LINE)

      markers {
        marker(MARKDOWN, 0,13 )
      }
      intervals {
        interval(MARKDOWN, 0..1)
      }
      intervalListenerCall(0){
        before {
          interval(MARKDOWN, 0..0)
          interval(CODE, 1..1)
          interval(MARKDOWN, 2..2)
        }
        after {
          interval(MARKDOWN, 0..1)
        }
      }
    }
  }

  private fun assertCodeCells(description: String = "", handler: CodeCellLinesChecker.() -> Unit) {
    CodeCellLinesChecker(description) { fixture.editor as EditorImpl }.invoke(handler)
  }
}