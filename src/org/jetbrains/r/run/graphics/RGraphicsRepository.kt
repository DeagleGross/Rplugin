/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.run.graphics

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.jetbrains.concurrency.AsyncPromise
import org.jetbrains.concurrency.Promise
import java.io.File

class RGraphicsRepository(private val project: Project) {
  private val devices = mutableSetOf<RGraphicsDevice>()
  private val notNullDevicePromise = AsyncPromise<Unit>()
  private val snapshotListeners = mutableListOf<(List<RSnapshot>) -> Unit>()

  private var currentDevice: RGraphicsDevice? = null

  var configuration: RGraphicsDevice.Configuration?
    get() {
      return synchronized(this) {
        currentDevice?.configuration
      }
    }
    set(value) {
      if (value != null) {
        synchronized(this) {
          currentDevice?.let { device ->
            device.configuration = value
          }
        }
      }
    }

  @Synchronized
  fun setActiveDevice(device: RGraphicsDevice) {
    if (device !in devices) {
      devices.add(device)
      device.addListener { update ->
        notifyUpdate(update)
      }
    }
    currentDevice = device
    if (!notNullDevicePromise.isSucceeded) {
      notNullDevicePromise.setResult(Unit)
    }
    notifyUpdate(device.lastUpdate)
  }

  @Synchronized
  fun addSnapshotListener(listener: (List<RSnapshot>) -> Unit) {
    snapshotListeners.add(listener)
    currentDevice?.let { device ->
      listener(device.lastUpdate)
    }
  }

  @Synchronized
  fun clearSnapshot(number: Int) {
    currentDevice?.clearSnapshot(number)
  }

  @Synchronized
  fun clearAllSnapshots() {
    currentDevice?.clearAllSnapshots()
  }

  @Synchronized
  fun rescale(snapshot: RSnapshot, group: RDeviceGroup, newParameters: RGraphicsUtils.ScreenParameters, onRescale: (File) -> Unit) {
    notNullDevicePromise.onSuccess {
      currentDevice?.rescale(snapshot, group, newParameters, onRescale)
    }
  }

  @Synchronized
  fun createDeviceGroupAsync(snapshot: RSnapshot): Promise<RDeviceGroup> {
    return notNullDevicePromise.thenAsync {
      currentDevice?.createDeviceGroupAsync(snapshot)
    }
  }

  private fun notifyUpdate(update: List<RSnapshot>) {
    for (listener in snapshotListeners) {
      listener(update)
    }
  }

  companion object {
    fun getInstance(project: Project) = project.service<RGraphicsRepository>()
  }
}