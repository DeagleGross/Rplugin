// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: service.proto

package org.jetbrains.r.rinterop;

public interface GraphicsPullSnapshotRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:rplugininterop.GraphicsPullSnapshotRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string groupId = 1;</code>
   */
  java.lang.String getGroupId();
  /**
   * <code>string groupId = 1;</code>
   */
  com.google.protobuf.ByteString
      getGroupIdBytes();

  /**
   * <code>int32 snapshotNumber = 2;</code>
   */
  int getSnapshotNumber();

  /**
   * <code>bool withRecorded = 3;</code>
   */
  boolean getWithRecorded();
}
