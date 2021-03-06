// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: service.proto

package org.jetbrains.r.rinterop;

/**
 * Protobuf type {@code rplugininterop.ExtendedSourcePosition}
 */
public final class ExtendedSourcePosition extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:rplugininterop.ExtendedSourcePosition)
    ExtendedSourcePositionOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ExtendedSourcePosition.newBuilder() to construct.
  private ExtendedSourcePosition(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ExtendedSourcePosition() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new ExtendedSourcePosition();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private ExtendedSourcePosition(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {

            startLine_ = input.readInt32();
            break;
          }
          case 16: {

            startOffset_ = input.readInt32();
            break;
          }
          case 24: {

            endLine_ = input.readInt32();
            break;
          }
          case 32: {

            endOffset_ = input.readInt32();
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_ExtendedSourcePosition_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_ExtendedSourcePosition_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.jetbrains.r.rinterop.ExtendedSourcePosition.class, org.jetbrains.r.rinterop.ExtendedSourcePosition.Builder.class);
  }

  public static final int STARTLINE_FIELD_NUMBER = 1;
  private int startLine_;
  /**
   * <code>int32 startLine = 1;</code>
   * @return The startLine.
   */
  @java.lang.Override
  public int getStartLine() {
    return startLine_;
  }

  public static final int STARTOFFSET_FIELD_NUMBER = 2;
  private int startOffset_;
  /**
   * <code>int32 startOffset = 2;</code>
   * @return The startOffset.
   */
  @java.lang.Override
  public int getStartOffset() {
    return startOffset_;
  }

  public static final int ENDLINE_FIELD_NUMBER = 3;
  private int endLine_;
  /**
   * <code>int32 endLine = 3;</code>
   * @return The endLine.
   */
  @java.lang.Override
  public int getEndLine() {
    return endLine_;
  }

  public static final int ENDOFFSET_FIELD_NUMBER = 4;
  private int endOffset_;
  /**
   * <code>int32 endOffset = 4;</code>
   * @return The endOffset.
   */
  @java.lang.Override
  public int getEndOffset() {
    return endOffset_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (startLine_ != 0) {
      output.writeInt32(1, startLine_);
    }
    if (startOffset_ != 0) {
      output.writeInt32(2, startOffset_);
    }
    if (endLine_ != 0) {
      output.writeInt32(3, endLine_);
    }
    if (endOffset_ != 0) {
      output.writeInt32(4, endOffset_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (startLine_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, startLine_);
    }
    if (startOffset_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, startOffset_);
    }
    if (endLine_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(3, endLine_);
    }
    if (endOffset_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(4, endOffset_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof org.jetbrains.r.rinterop.ExtendedSourcePosition)) {
      return super.equals(obj);
    }
    org.jetbrains.r.rinterop.ExtendedSourcePosition other = (org.jetbrains.r.rinterop.ExtendedSourcePosition) obj;

    if (getStartLine()
        != other.getStartLine()) return false;
    if (getStartOffset()
        != other.getStartOffset()) return false;
    if (getEndLine()
        != other.getEndLine()) return false;
    if (getEndOffset()
        != other.getEndOffset()) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + STARTLINE_FIELD_NUMBER;
    hash = (53 * hash) + getStartLine();
    hash = (37 * hash) + STARTOFFSET_FIELD_NUMBER;
    hash = (53 * hash) + getStartOffset();
    hash = (37 * hash) + ENDLINE_FIELD_NUMBER;
    hash = (53 * hash) + getEndLine();
    hash = (37 * hash) + ENDOFFSET_FIELD_NUMBER;
    hash = (53 * hash) + getEndOffset();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.jetbrains.r.rinterop.ExtendedSourcePosition parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.r.rinterop.ExtendedSourcePosition parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.ExtendedSourcePosition parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.r.rinterop.ExtendedSourcePosition parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.ExtendedSourcePosition parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.r.rinterop.ExtendedSourcePosition parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.ExtendedSourcePosition parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.jetbrains.r.rinterop.ExtendedSourcePosition parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.ExtendedSourcePosition parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.jetbrains.r.rinterop.ExtendedSourcePosition parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.ExtendedSourcePosition parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.jetbrains.r.rinterop.ExtendedSourcePosition parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(org.jetbrains.r.rinterop.ExtendedSourcePosition prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code rplugininterop.ExtendedSourcePosition}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:rplugininterop.ExtendedSourcePosition)
      org.jetbrains.r.rinterop.ExtendedSourcePositionOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_ExtendedSourcePosition_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_ExtendedSourcePosition_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.jetbrains.r.rinterop.ExtendedSourcePosition.class, org.jetbrains.r.rinterop.ExtendedSourcePosition.Builder.class);
    }

    // Construct using org.jetbrains.r.rinterop.ExtendedSourcePosition.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      startLine_ = 0;

      startOffset_ = 0;

      endLine_ = 0;

      endOffset_ = 0;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_ExtendedSourcePosition_descriptor;
    }

    @java.lang.Override
    public org.jetbrains.r.rinterop.ExtendedSourcePosition getDefaultInstanceForType() {
      return org.jetbrains.r.rinterop.ExtendedSourcePosition.getDefaultInstance();
    }

    @java.lang.Override
    public org.jetbrains.r.rinterop.ExtendedSourcePosition build() {
      org.jetbrains.r.rinterop.ExtendedSourcePosition result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.jetbrains.r.rinterop.ExtendedSourcePosition buildPartial() {
      org.jetbrains.r.rinterop.ExtendedSourcePosition result = new org.jetbrains.r.rinterop.ExtendedSourcePosition(this);
      result.startLine_ = startLine_;
      result.startOffset_ = startOffset_;
      result.endLine_ = endLine_;
      result.endOffset_ = endOffset_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof org.jetbrains.r.rinterop.ExtendedSourcePosition) {
        return mergeFrom((org.jetbrains.r.rinterop.ExtendedSourcePosition)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.jetbrains.r.rinterop.ExtendedSourcePosition other) {
      if (other == org.jetbrains.r.rinterop.ExtendedSourcePosition.getDefaultInstance()) return this;
      if (other.getStartLine() != 0) {
        setStartLine(other.getStartLine());
      }
      if (other.getStartOffset() != 0) {
        setStartOffset(other.getStartOffset());
      }
      if (other.getEndLine() != 0) {
        setEndLine(other.getEndLine());
      }
      if (other.getEndOffset() != 0) {
        setEndOffset(other.getEndOffset());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      org.jetbrains.r.rinterop.ExtendedSourcePosition parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.jetbrains.r.rinterop.ExtendedSourcePosition) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int startLine_ ;
    /**
     * <code>int32 startLine = 1;</code>
     * @return The startLine.
     */
    @java.lang.Override
    public int getStartLine() {
      return startLine_;
    }
    /**
     * <code>int32 startLine = 1;</code>
     * @param value The startLine to set.
     * @return This builder for chaining.
     */
    public Builder setStartLine(int value) {
      
      startLine_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 startLine = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearStartLine() {
      
      startLine_ = 0;
      onChanged();
      return this;
    }

    private int startOffset_ ;
    /**
     * <code>int32 startOffset = 2;</code>
     * @return The startOffset.
     */
    @java.lang.Override
    public int getStartOffset() {
      return startOffset_;
    }
    /**
     * <code>int32 startOffset = 2;</code>
     * @param value The startOffset to set.
     * @return This builder for chaining.
     */
    public Builder setStartOffset(int value) {
      
      startOffset_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 startOffset = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearStartOffset() {
      
      startOffset_ = 0;
      onChanged();
      return this;
    }

    private int endLine_ ;
    /**
     * <code>int32 endLine = 3;</code>
     * @return The endLine.
     */
    @java.lang.Override
    public int getEndLine() {
      return endLine_;
    }
    /**
     * <code>int32 endLine = 3;</code>
     * @param value The endLine to set.
     * @return This builder for chaining.
     */
    public Builder setEndLine(int value) {
      
      endLine_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 endLine = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearEndLine() {
      
      endLine_ = 0;
      onChanged();
      return this;
    }

    private int endOffset_ ;
    /**
     * <code>int32 endOffset = 4;</code>
     * @return The endOffset.
     */
    @java.lang.Override
    public int getEndOffset() {
      return endOffset_;
    }
    /**
     * <code>int32 endOffset = 4;</code>
     * @param value The endOffset to set.
     * @return This builder for chaining.
     */
    public Builder setEndOffset(int value) {
      
      endOffset_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 endOffset = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearEndOffset() {
      
      endOffset_ = 0;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:rplugininterop.ExtendedSourcePosition)
  }

  // @@protoc_insertion_point(class_scope:rplugininterop.ExtendedSourcePosition)
  private static final org.jetbrains.r.rinterop.ExtendedSourcePosition DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.jetbrains.r.rinterop.ExtendedSourcePosition();
  }

  public static org.jetbrains.r.rinterop.ExtendedSourcePosition getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ExtendedSourcePosition>
      PARSER = new com.google.protobuf.AbstractParser<ExtendedSourcePosition>() {
    @java.lang.Override
    public ExtendedSourcePosition parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new ExtendedSourcePosition(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ExtendedSourcePosition> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ExtendedSourcePosition> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.jetbrains.r.rinterop.ExtendedSourcePosition getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

