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
package net.kyori.adventure.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;

import static java.util.Objects.requireNonNull;

/* package */ final class ShowEntitySerializer extends TypeAdapter<HoverEvent.ShowEntity> {
  static final String TYPE = "type";
  static final String ID = "id";
  static final String NAME = "name";

  private final TypeAdapter<Key> keyAdapter;
  private final TypeAdapter<Component> componentAdapter;

  /* package */ ShowEntitySerializer(final Gson instance) {
    this.keyAdapter = instance.getAdapter(Key.class);
    this.componentAdapter = instance.getAdapter(Component.class);
  }

  @Override
  public void write(final JsonWriter out, final HoverEvent.ShowEntity value) throws IOException {
    out.beginObject();
    
    out.name(TYPE);
    this.keyAdapter.write(out, value.type());

    out.name(ID).value(value.id().toString());
    final /* @Nullable */ Component name = value.name();
    if(name != null) {
      out.name(NAME);
      this.componentAdapter.write(out, name);
    }
    out.endObject();
  }

  @Override
  public HoverEvent.ShowEntity read(final JsonReader in) throws IOException {
    Key type = null;
    UUID id = null;
    Component name = null;
    
    in.beginObject();
    while(in.peek() != JsonToken.END_OBJECT) {
      final String key = in.nextName();
      if(key.equals(TYPE)) {
        type = this.keyAdapter.read(in);
      } else if(key.equals(ID)) {
        id = UUID.fromString(in.nextString());
      } else if(key.equals(NAME)) {
        name = this.componentAdapter.read(in);
      }
    }
    in.endObject();

    requireNonNull(type , "type is required to deserialize a hover event");
    requireNonNull(id, "uuid is required to deserialize a hover event");
    return new HoverEvent.ShowEntity(type, id, name);

  }
}
