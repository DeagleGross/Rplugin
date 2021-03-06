// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: service.proto

package org.jetbrains.r.rinterop;

/**
 * Protobuf type {@code rplugininterop.ChunkParameters}
 */
public final class ChunkParameters extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:rplugininterop.ChunkParameters)
    ChunkParametersOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ChunkParameters.newBuilder() to construct.
  private ChunkParameters(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ChunkParameters() {
    rmarkdownParameters_ = "";
    chunkText_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new ChunkParameters();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private ChunkParameters(
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
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            rmarkdownParameters_ = s;
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            chunkText_ = s;
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
    return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_ChunkParameters_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_ChunkParameters_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.jetbrains.r.rinterop.ChunkParameters.class, org.jetbrains.r.rinterop.ChunkParameters.Builder.class);
  }

  public static final int RMARKDOWNPARAMETERS_FIELD_NUMBER = 1;
  private volatile java.lang.Object rmarkdownParameters_;
  /**
   * <code>string rmarkdownParameters = 1;</code>
   * @return The rmarkdownParameters.
   */
  @java.lang.Override
  public java.lang.String getRmarkdownParameters() {
    java.lang.Object ref = rmarkdownParameters_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      rmarkdownParameters_ = s;
      return s;
    }
  }
  /**
   * <code>string rmarkdownParameters = 1;</code>
   * @return The bytes for rmarkdownParameters.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getRmarkdownParametersBytes() {
    java.lang.Object ref = rmarkdownParameters_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      rmarkdownParameters_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int CHUNKTEXT_FIELD_NUMBER = 2;
  private volatile java.lang.Object chunkText_;
  /**
   * <code>string chunkText = 2;</code>
   * @return The chunkText.
   */
  @java.lang.Override
  public java.lang.String getChunkText() {
    java.lang.Object ref = chunkText_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      chunkText_ = s;
      return s;
    }
  }
  /**
   * <code>string chunkText = 2;</code>
   * @return The bytes for chunkText.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getChunkTextBytes() {
    java.lang.Object ref = chunkText_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      chunkText_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
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
    if (!getRmarkdownParametersBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, rmarkdownParameters_);
    }
    if (!getChunkTextBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, chunkText_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getRmarkdownParametersBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, rmarkdownParameters_);
    }
    if (!getChunkTextBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, chunkText_);
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
    if (!(obj instanceof org.jetbrains.r.rinterop.ChunkParameters)) {
      return super.equals(obj);
    }
    org.jetbrains.r.rinterop.ChunkParameters other = (org.jetbrains.r.rinterop.ChunkParameters) obj;

    if (!getRmarkdownParameters()
        .equals(other.getRmarkdownParameters())) return false;
    if (!getChunkText()
        .equals(other.getChunkText())) return false;
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
    hash = (37 * hash) + RMARKDOWNPARAMETERS_FIELD_NUMBER;
    hash = (53 * hash) + getRmarkdownParameters().hashCode();
    hash = (37 * hash) + CHUNKTEXT_FIELD_NUMBER;
    hash = (53 * hash) + getChunkText().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.jetbrains.r.rinterop.ChunkParameters parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.r.rinterop.ChunkParameters parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.ChunkParameters parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.r.rinterop.ChunkParameters parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.ChunkParameters parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.r.rinterop.ChunkParameters parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.ChunkParameters parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.jetbrains.r.rinterop.ChunkParameters parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.ChunkParameters parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.jetbrains.r.rinterop.ChunkParameters parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.ChunkParameters parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.jetbrains.r.rinterop.ChunkParameters parseFrom(
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
  public static Builder newBuilder(org.jetbrains.r.rinterop.ChunkParameters prototype) {
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
   * Protobuf type {@code rplugininterop.ChunkParameters}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:rplugininterop.ChunkParameters)
      org.jetbrains.r.rinterop.ChunkParametersOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_ChunkParameters_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_ChunkParameters_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.jetbrains.r.rinterop.ChunkParameters.class, org.jetbrains.r.rinterop.ChunkParameters.Builder.class);
    }

    // Construct using org.jetbrains.r.rinterop.ChunkParameters.newBuilder()
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
      rmarkdownParameters_ = "";

      chunkText_ = "";

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_ChunkParameters_descriptor;
    }

    @java.lang.Override
    public org.jetbrains.r.rinterop.ChunkParameters getDefaultInstanceForType() {
      return org.jetbrains.r.rinterop.ChunkParameters.getDefaultInstance();
    }

    @java.lang.Override
    public org.jetbrains.r.rinterop.ChunkParameters build() {
      org.jetbrains.r.rinterop.ChunkParameters result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.jetbrains.r.rinterop.ChunkParameters buildPartial() {
      org.jetbrains.r.rinterop.ChunkParameters result = new org.jetbrains.r.rinterop.ChunkParameters(this);
      result.rmarkdownParameters_ = rmarkdownParameters_;
      result.chunkText_ = chunkText_;
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
      if (other instanceof org.jetbrains.r.rinterop.ChunkParameters) {
        return mergeFrom((org.jetbrains.r.rinterop.ChunkParameters)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.jetbrains.r.rinterop.ChunkParameters other) {
      if (other == org.jetbrains.r.rinterop.ChunkParameters.getDefaultInstance()) return this;
      if (!other.getRmarkdownParameters().isEmpty()) {
        rmarkdownParameters_ = other.rmarkdownParameters_;
        onChanged();
      }
      if (!other.getChunkText().isEmpty()) {
        chunkText_ = other.chunkText_;
        onChanged();
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
      org.jetbrains.r.rinterop.ChunkParameters parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.jetbrains.r.rinterop.ChunkParameters) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object rmarkdownParameters_ = "";
    /**
     * <code>string rmarkdownParameters = 1;</code>
     * @return The rmarkdownParameters.
     */
    public java.lang.String getRmarkdownParameters() {
      java.lang.Object ref = rmarkdownParameters_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        rmarkdownParameters_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string rmarkdownParameters = 1;</code>
     * @return The bytes for rmarkdownParameters.
     */
    public com.google.protobuf.ByteString
        getRmarkdownParametersBytes() {
      java.lang.Object ref = rmarkdownParameters_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        rmarkdownParameters_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string rmarkdownParameters = 1;</code>
     * @param value The rmarkdownParameters to set.
     * @return This builder for chaining.
     */
    public Builder setRmarkdownParameters(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      rmarkdownParameters_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string rmarkdownParameters = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearRmarkdownParameters() {
      
      rmarkdownParameters_ = getDefaultInstance().getRmarkdownParameters();
      onChanged();
      return this;
    }
    /**
     * <code>string rmarkdownParameters = 1;</code>
     * @param value The bytes for rmarkdownParameters to set.
     * @return This builder for chaining.
     */
    public Builder setRmarkdownParametersBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      rmarkdownParameters_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object chunkText_ = "";
    /**
     * <code>string chunkText = 2;</code>
     * @return The chunkText.
     */
    public java.lang.String getChunkText() {
      java.lang.Object ref = chunkText_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        chunkText_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string chunkText = 2;</code>
     * @return The bytes for chunkText.
     */
    public com.google.protobuf.ByteString
        getChunkTextBytes() {
      java.lang.Object ref = chunkText_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        chunkText_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string chunkText = 2;</code>
     * @param value The chunkText to set.
     * @return This builder for chaining.
     */
    public Builder setChunkText(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      chunkText_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string chunkText = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearChunkText() {
      
      chunkText_ = getDefaultInstance().getChunkText();
      onChanged();
      return this;
    }
    /**
     * <code>string chunkText = 2;</code>
     * @param value The bytes for chunkText to set.
     * @return This builder for chaining.
     */
    public Builder setChunkTextBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      chunkText_ = value;
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


    // @@protoc_insertion_point(builder_scope:rplugininterop.ChunkParameters)
  }

  // @@protoc_insertion_point(class_scope:rplugininterop.ChunkParameters)
  private static final org.jetbrains.r.rinterop.ChunkParameters DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.jetbrains.r.rinterop.ChunkParameters();
  }

  public static org.jetbrains.r.rinterop.ChunkParameters getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ChunkParameters>
      PARSER = new com.google.protobuf.AbstractParser<ChunkParameters>() {
    @java.lang.Override
    public ChunkParameters parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new ChunkParameters(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ChunkParameters> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ChunkParameters> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.jetbrains.r.rinterop.ChunkParameters getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

