/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2020 KyoriPowered
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.adventure.nbt;

import java.io.IOException;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a compound binary tag.
 */
public interface CompoundBinaryTag {
  /*
   * Instead of including an entire NBT implementation in adventure-api, we have decided to
   * use this "empty" interface instead, allowing for either our own NBT API to be used, or
   * one from a specific platform (when possible).
   */

  static <T> @NonNull CompoundBinaryTag of(final @NonNull T nbt, final @NonNull Codec<T> codec) throws IOException {
    return of(codec.encode(nbt));
  }

  static @NonNull CompoundBinaryTag of(final @NonNull String nbtData) {
    return new CompoundBinaryTagImpl(nbtData);
  }

  <T> @NonNull T decode(final @NonNull Codec<T> codec) throws IOException;

  @NonNull String encodeToString();

  /**
   * Something that can read and write a compound binary tag from a {@link String}.
   */
  interface Codec<T> {
    /**
     * Reads a compound binary tag from a {@link String}.
     *
     * @param string the string
     * @return the compound binary tag
     * @throws IOException if an error occurred while reading
     */
    @NonNull T decode(final @NonNull String string) throws IOException;

    /**
     * Writes a compound binary tag to a {@link String}.
     *
     * @param nbt the compound binary tag
     * @return the string
     * @throws IOException if an error occurred while reading
     */
    @NonNull String encode(final @NonNull T nbt) throws IOException;
  }
}
