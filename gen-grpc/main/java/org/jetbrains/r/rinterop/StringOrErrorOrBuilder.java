// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: service.proto

package org.jetbrains.r.rinterop;

public interface StringOrErrorOrBuilder extends
    // @@protoc_insertion_point(interface_extends:rplugininterop.StringOrError)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string value = 1;</code>
   * @return The value.
   */
  java.lang.String getValue();
  /**
   * <code>string value = 1;</code>
   * @return The bytes for value.
   */
  com.google.protobuf.ByteString
      getValueBytes();

  /**
   * <code>string error = 2;</code>
   * @return The error.
   */
  java.lang.String getError();
  /**
   * <code>string error = 2;</code>
   * @return The bytes for error.
   */
  com.google.protobuf.ByteString
      getErrorBytes();

  public org.jetbrains.r.rinterop.StringOrError.ResultCase getResultCase();
}
