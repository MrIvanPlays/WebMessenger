/*
 * Copyright (c) Ivan Pekov 2026
 * Copyright (c) Contributors 2026
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
