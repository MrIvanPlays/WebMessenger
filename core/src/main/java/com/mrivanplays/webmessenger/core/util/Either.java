package com.mrivanplays.webmessenger.core.util;

import java.util.Optional;

public final class Either<L, R> {

  public static <L, R> Either<L, R> left(L left) {
    return new Either<>(left, null);
  }

  public static <L, R> Either<L, R> right(R right) {
    return new Either<>(null, right);
  }

  private final L left;
  private final R right;
  private final Type type;

  private Either(L left, R right) {
    if (left == null && right == null) {
      throw new IllegalArgumentException("Cannot instantiate Either as both values are null");
    }
    this.left = left;
    this.right = right;
    this.type = left == null ? Type.RIGHT : Type.LEFT;
  }

  public Optional<L> left() {
    return Optional.ofNullable(this.left);
  }

  public Optional<R> right() {
    return Optional.ofNullable(this.right);
  }

  public Type type() {
    return type;
  }

  public enum Type {
    LEFT,
    RIGHT;

    public boolean isLeft() {
      return this == LEFT;
    }
  }
}
