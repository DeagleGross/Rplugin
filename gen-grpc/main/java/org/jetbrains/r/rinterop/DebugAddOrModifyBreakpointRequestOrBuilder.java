// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: service.proto

package org.jetbrains.r.rinterop;

public interface DebugAddOrModifyBreakpointRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:rplugininterop.DebugAddOrModifyBreakpointRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 id = 1;</code>
   * @return The id.
   */
  int getId();

  /**
   * <code>.rplugininterop.SourcePosition position = 2;</code>
   * @return Whether the position field is set.
   */
  boolean hasPosition();
  /**
   * <code>.rplugininterop.SourcePosition position = 2;</code>
   * @return The position.
   */
  org.jetbrains.r.rinterop.SourcePosition getPosition();
  /**
   * <code>.rplugininterop.SourcePosition position = 2;</code>
   */
  org.jetbrains.r.rinterop.SourcePositionOrBuilder getPositionOrBuilder();

  /**
   * <code>bool enabled = 3;</code>
   * @return The enabled.
   */
  boolean getEnabled();

  /**
   * <code>bool suspend = 4;</code>
   * @return The suspend.
   */
  boolean getSuspend();

  /**
   * <code>string evaluateAndLog = 5;</code>
   * @return The evaluateAndLog.
   */
  java.lang.String getEvaluateAndLog();
  /**
   * <code>string evaluateAndLog = 5;</code>
   * @return The bytes for evaluateAndLog.
   */
  com.google.protobuf.ByteString
      getEvaluateAndLogBytes();

  /**
   * <code>string condition = 6;</code>
   * @return The condition.
   */
  java.lang.String getCondition();
  /**
   * <code>string condition = 6;</code>
   * @return The bytes for condition.
   */
  com.google.protobuf.ByteString
      getConditionBytes();

  /**
   * <code>bool removeAfterHit = 7;</code>
   * @return The removeAfterHit.
   */
  boolean getRemoveAfterHit();

  /**
   * <code>bool hitMessage = 8;</code>
   * @return The hitMessage.
   */
  boolean getHitMessage();

  /**
   * <code>bool printStack = 9;</code>
   * @return The printStack.
   */
  boolean getPrintStack();
}
