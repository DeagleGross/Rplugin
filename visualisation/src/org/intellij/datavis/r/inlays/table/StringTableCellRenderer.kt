/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.datavis.r.inlays.table

import javax.swing.table.DefaultTableCellRenderer

class StringTableCellRenderer : DefaultTableCellRenderer() {
  override fun setValue(value: Any?) {
    if(value == null) {
      text = "<null>"
    } else {
      text = value.toString()
    }
  }
}