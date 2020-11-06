// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: service.proto

package org.jetbrains.r.rinterop;

/**
 * Protobuf type {@code rplugininterop.PolygonFigure}
 */
public  final class PolygonFigure extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:rplugininterop.PolygonFigure)
    PolygonFigureOrBuilder {
private static final long serialVersionUID = 0L;
  // Use PolygonFigure.newBuilder() to construct.
  private PolygonFigure(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private PolygonFigure() {
    point_ = emptyLongList();
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new PolygonFigure();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private PolygonFigure(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
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
          case 9: {
            if (!((mutable_bitField0_ & 0x00000001) != 0)) {
              point_ = newLongList();
              mutable_bitField0_ |= 0x00000001;
            }
            point_.addLong(input.readFixed64());
            break;
          }
          case 10: {
            int length = input.readRawVarint32();
            int limit = input.pushLimit(length);
            if (!((mutable_bitField0_ & 0x00000001) != 0) && input.getBytesUntilLimit() > 0) {
              point_ = newLongList();
              mutable_bitField0_ |= 0x00000001;
            }
            while (input.getBytesUntilLimit() > 0) {
              point_.addLong(input.readFixed64());
            }
            input.popLimit(limit);
            break;
          }
          case 16: {

            strokeIndex_ = input.readInt32();
            break;
          }
          case 24: {

            colorIndex_ = input.readInt32();
            break;
          }
          case 32: {

            fillIndex_ = input.readInt32();
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
      if (((mutable_bitField0_ & 0x00000001) != 0)) {
        point_.makeImmutable(); // C
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_PolygonFigure_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_PolygonFigure_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            org.jetbrains.r.rinterop.PolygonFigure.class, org.jetbrains.r.rinterop.PolygonFigure.Builder.class);
  }

  public static final int POINT_FIELD_NUMBER = 1;
  private com.google.protobuf.Internal.LongList point_;
  /**
   * <code>repeated fixed64 point = 1;</code>
   */
  public java.util.List<java.lang.Long>
      getPointList() {
    return point_;
  }
  /**
   * <code>repeated fixed64 point = 1;</code>
   */
  public int getPointCount() {
    return point_.size();
  }
  /**
   * <code>repeated fixed64 point = 1;</code>
   */
  public long getPoint(int index) {
    return point_.getLong(index);
  }
  private int pointMemoizedSerializedSize = -1;

  public static final int STROKEINDEX_FIELD_NUMBER = 2;
  private int strokeIndex_;
  /**
   * <code>int32 strokeIndex = 2;</code>
   */
  public int getStrokeIndex() {
    return strokeIndex_;
  }

  public static final int COLORINDEX_FIELD_NUMBER = 3;
  private int colorIndex_;
  /**
   * <code>int32 colorIndex = 3;</code>
   */
  public int getColorIndex() {
    return colorIndex_;
  }

  public static final int FILLINDEX_FIELD_NUMBER = 4;
  private int fillIndex_;
  /**
   * <code>int32 fillIndex = 4;</code>
   */
  public int getFillIndex() {
    return fillIndex_;
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
    getSerializedSize();
    if (getPointList().size() > 0) {
      output.writeUInt32NoTag(10);
      output.writeUInt32NoTag(pointMemoizedSerializedSize);
    }
    for (int i = 0; i < point_.size(); i++) {
      output.writeFixed64NoTag(point_.getLong(i));
    }
    if (strokeIndex_ != 0) {
      output.writeInt32(2, strokeIndex_);
    }
    if (colorIndex_ != 0) {
      output.writeInt32(3, colorIndex_);
    }
    if (fillIndex_ != 0) {
      output.writeInt32(4, fillIndex_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    {
      int dataSize = 0;
      dataSize = 8 * getPointList().size();
      size += dataSize;
      if (!getPointList().isEmpty()) {
        size += 1;
        size += com.google.protobuf.CodedOutputStream
            .computeInt32SizeNoTag(dataSize);
      }
      pointMemoizedSerializedSize = dataSize;
    }
    if (strokeIndex_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, strokeIndex_);
    }
    if (colorIndex_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(3, colorIndex_);
    }
    if (fillIndex_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(4, fillIndex_);
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
    if (!(obj instanceof org.jetbrains.r.rinterop.PolygonFigure)) {
      return super.equals(obj);
    }
    org.jetbrains.r.rinterop.PolygonFigure other = (org.jetbrains.r.rinterop.PolygonFigure) obj;

    if (!getPointList()
        .equals(other.getPointList())) return false;
    if (getStrokeIndex()
        != other.getStrokeIndex()) return false;
    if (getColorIndex()
        != other.getColorIndex()) return false;
    if (getFillIndex()
        != other.getFillIndex()) return false;
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
    if (getPointCount() > 0) {
      hash = (37 * hash) + POINT_FIELD_NUMBER;
      hash = (53 * hash) + getPointList().hashCode();
    }
    hash = (37 * hash) + STROKEINDEX_FIELD_NUMBER;
    hash = (53 * hash) + getStrokeIndex();
    hash = (37 * hash) + COLORINDEX_FIELD_NUMBER;
    hash = (53 * hash) + getColorIndex();
    hash = (37 * hash) + FILLINDEX_FIELD_NUMBER;
    hash = (53 * hash) + getFillIndex();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static org.jetbrains.r.rinterop.PolygonFigure parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.r.rinterop.PolygonFigure parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.PolygonFigure parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.r.rinterop.PolygonFigure parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.PolygonFigure parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.r.rinterop.PolygonFigure parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.PolygonFigure parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.jetbrains.r.rinterop.PolygonFigure parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.PolygonFigure parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static org.jetbrains.r.rinterop.PolygonFigure parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static org.jetbrains.r.rinterop.PolygonFigure parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static org.jetbrains.r.rinterop.PolygonFigure parseFrom(
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
  public static Builder newBuilder(org.jetbrains.r.rinterop.PolygonFigure prototype) {
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
   * Protobuf type {@code rplugininterop.PolygonFigure}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:rplugininterop.PolygonFigure)
      org.jetbrains.r.rinterop.PolygonFigureOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_PolygonFigure_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_PolygonFigure_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.jetbrains.r.rinterop.PolygonFigure.class, org.jetbrains.r.rinterop.PolygonFigure.Builder.class);
    }

    // Construct using org.jetbrains.r.rinterop.PolygonFigure.newBuilder()
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
      point_ = emptyLongList();
      bitField0_ = (bitField0_ & ~0x00000001);
      strokeIndex_ = 0;

      colorIndex_ = 0;

      fillIndex_ = 0;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return org.jetbrains.r.rinterop.Service.internal_static_rplugininterop_PolygonFigure_descriptor;
    }

    @java.lang.Override
    public org.jetbrains.r.rinterop.PolygonFigure getDefaultInstanceForType() {
      return org.jetbrains.r.rinterop.PolygonFigure.getDefaultInstance();
    }

    @java.lang.Override
    public org.jetbrains.r.rinterop.PolygonFigure build() {
      org.jetbrains.r.rinterop.PolygonFigure result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public org.jetbrains.r.rinterop.PolygonFigure buildPartial() {
      org.jetbrains.r.rinterop.PolygonFigure result = new org.jetbrains.r.rinterop.PolygonFigure(this);
      int from_bitField0_ = bitField0_;
      if (((bitField0_ & 0x00000001) != 0)) {
        point_.makeImmutable();
        bitField0_ = (bitField0_ & ~0x00000001);
      }
      result.point_ = point_;
      result.strokeIndex_ = strokeIndex_;
      result.colorIndex_ = colorIndex_;
      result.fillIndex_ = fillIndex_;
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
      if (other instanceof org.jetbrains.r.rinterop.PolygonFigure) {
        return mergeFrom((org.jetbrains.r.rinterop.PolygonFigure)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(org.jetbrains.r.rinterop.PolygonFigure other) {
      if (other == org.jetbrains.r.rinterop.PolygonFigure.getDefaultInstance()) return this;
      if (!other.point_.isEmpty()) {
        if (point_.isEmpty()) {
          point_ = other.point_;
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          ensurePointIsMutable();
          point_.addAll(other.point_);
        }
        onChanged();
      }
      if (other.getStrokeIndex() != 0) {
        setStrokeIndex(other.getStrokeIndex());
      }
      if (other.getColorIndex() != 0) {
        setColorIndex(other.getColorIndex());
      }
      if (other.getFillIndex() != 0) {
        setFillIndex(other.getFillIndex());
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
      org.jetbrains.r.rinterop.PolygonFigure parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.jetbrains.r.rinterop.PolygonFigure) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private com.google.protobuf.Internal.LongList point_ = emptyLongList();
    private void ensurePointIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        point_ = mutableCopy(point_);
        bitField0_ |= 0x00000001;
       }
    }
    /**
     * <code>repeated fixed64 point = 1;</code>
     */
    public java.util.List<java.lang.Long>
        getPointList() {
      return ((bitField0_ & 0x00000001) != 0) ?
               java.util.Collections.unmodifiableList(point_) : point_;
    }
    /**
     * <code>repeated fixed64 point = 1;</code>
     */
    public int getPointCount() {
      return point_.size();
    }
    /**
     * <code>repeated fixed64 point = 1;</code>
     */
    public long getPoint(int index) {
      return point_.getLong(index);
    }
    /**
     * <code>repeated fixed64 point = 1;</code>
     */
    public Builder setPoint(
        int index, long value) {
      ensurePointIsMutable();
      point_.setLong(index, value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated fixed64 point = 1;</code>
     */
    public Builder addPoint(long value) {
      ensurePointIsMutable();
      point_.addLong(value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated fixed64 point = 1;</code>
     */
    public Builder addAllPoint(
        java.lang.Iterable<? extends java.lang.Long> values) {
      ensurePointIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, point_);
      onChanged();
      return this;
    }
    /**
     * <code>repeated fixed64 point = 1;</code>
     */
    public Builder clearPoint() {
      point_ = emptyLongList();
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }

    private int strokeIndex_ ;
    /**
     * <code>int32 strokeIndex = 2;</code>
     */
    public int getStrokeIndex() {
      return strokeIndex_;
    }
    /**
     * <code>int32 strokeIndex = 2;</code>
     */
    public Builder setStrokeIndex(int value) {
      
      strokeIndex_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 strokeIndex = 2;</code>
     */
    public Builder clearStrokeIndex() {
      
      strokeIndex_ = 0;
      onChanged();
      return this;
    }

    private int colorIndex_ ;
    /**
     * <code>int32 colorIndex = 3;</code>
     */
    public int getColorIndex() {
      return colorIndex_;
    }
    /**
     * <code>int32 colorIndex = 3;</code>
     */
    public Builder setColorIndex(int value) {
      
      colorIndex_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 colorIndex = 3;</code>
     */
    public Builder clearColorIndex() {
      
      colorIndex_ = 0;
      onChanged();
      return this;
    }

    private int fillIndex_ ;
    /**
     * <code>int32 fillIndex = 4;</code>
     */
    public int getFillIndex() {
      return fillIndex_;
    }
    /**
     * <code>int32 fillIndex = 4;</code>
     */
    public Builder setFillIndex(int value) {
      
      fillIndex_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 fillIndex = 4;</code>
     */
    public Builder clearFillIndex() {
      
      fillIndex_ = 0;
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


    // @@protoc_insertion_point(builder_scope:rplugininterop.PolygonFigure)
  }

  // @@protoc_insertion_point(class_scope:rplugininterop.PolygonFigure)
  private static final org.jetbrains.r.rinterop.PolygonFigure DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new org.jetbrains.r.rinterop.PolygonFigure();
  }

  public static org.jetbrains.r.rinterop.PolygonFigure getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<PolygonFigure>
      PARSER = new com.google.protobuf.AbstractParser<PolygonFigure>() {
    @java.lang.Override
    public PolygonFigure parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new PolygonFigure(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<PolygonFigure> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<PolygonFigure> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public org.jetbrains.r.rinterop.PolygonFigure getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

