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
package net.kyori.adventure.text.serializer.legacy;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LegacyComponentSerializerTest {
  @Test
  void testSimpleFrom() {
    final TextComponent component = TextComponent.of("foo");
    assertEquals(component, LegacyComponentSerializer.legacy().deserialize("foo"));
  }

  @Test
  void testFromColor() {
    final TextComponent component = TextComponent.builder("")
      .append(TextComponent.of("foo").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
      .append(TextComponent.of("bar").color(NamedTextColor.BLUE))
      .build();

    assertEquals(component, LegacyComponentSerializer.legacy('&').deserialize("&a&lfoo&9bar"));
  }

  @Test
  void testFromColorOverride() {
    final TextComponent component = TextComponent.of("foo").color(NamedTextColor.BLUE);

    assertEquals(component, LegacyComponentSerializer.legacy('&').deserialize("&a&9foo"));
  }

  @Test
  void testResetOverride() {
    final TextComponent component = TextComponent.builder("")
      .append(TextComponent.of("foo").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
      .append(TextComponent.of("bar").color(NamedTextColor.DARK_GRAY))
      .build();

    assertEquals(component, LegacyComponentSerializer.legacy('&').deserialize("&a&lfoo&r&8bar"));
  }

  @Test
  void testCompound() {
    final TextComponent component = TextComponent.builder()
      .content("hi there ")
      .append(TextComponent.builder("this bit is green ")
        .color(NamedTextColor.GREEN)
        .build())
      .append(TextComponent.of("this isn't ").style(Style.empty()))
      .append(TextComponent.builder("and woa, this is again")
        .color(NamedTextColor.GREEN)
        .build())
      .build();

    assertEquals("hi there &athis bit is green &rthis isn't &aand woa, this is again", LegacyComponentSerializer.legacy('&').serialize(component));
  }

  @Test
  void testToLegacy() {
    final TextComponent c1 = TextComponent.builder("hi")
      .decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
      .append(
        TextComponent.of("foo")
          .color(NamedTextColor.GREEN)
          .decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)
      )
      .append(
        TextComponent.of("bar")
          .color(NamedTextColor.BLUE)
      )
      .append(TextComponent.of("baz"))
      .build();
    assertEquals("§lhi§afoo§9§lbar§r§lbaz", LegacyComponentSerializer.legacy().serialize(c1));

    final TextComponent c2 = TextComponent.builder()
      .content("")
      .color(NamedTextColor.YELLOW)
      .append(TextComponent.builder()
        .content("Hello ")
        .append(
          TextComponent.builder()
            .content("world")
            .color(NamedTextColor.GREEN)
            .build()
        )
        .append(TextComponent.of("!")) // Should be yellow
        .build()
      )
      .build();
    assertEquals("§eHello §aworld§e!", LegacyComponentSerializer.legacy().serialize(c2));

    final TextComponent c3 = TextComponent.builder()
      .content("")
      .decoration(TextDecoration.BOLD, true)
      .append(
        TextComponent.builder()
          .content("")
          .color(NamedTextColor.YELLOW)
          .append(TextComponent.builder()
            .content("Hello ")
            .append(
              TextComponent.builder()
                .content("world")
                .color(NamedTextColor.GREEN)
                .build()
            )
            .append(TextComponent.of("!"))
            .build()
          )
          .build())
      .build();
    assertEquals("§e§lHello §a§lworld§e§l!", LegacyComponentSerializer.legacy().serialize(c3));
  }

  @Test
  void testToLegacyWithHexColor() {
    final TextComponent c0 = TextComponent.of("Kittens!", TextColor.of(0xffefd5));
    assertEquals("§#ffefd5Kittens!", LegacyComponentSerializer.builder().hexColors().build().serialize(c0));
  }

  @Test
  void testToLegacyWithHexColorDownsampling() {
    final TextComponent comp = TextComponent.of("purr", TextColor.of(0xff0000));
    assertEquals("§4purr", LegacyComponentSerializer.builder().build().serialize(comp));
  }

  @Test
  void testFromLegacyWithHexColor() {
    final TextComponent component = TextComponent.builder("")
      .append(TextComponent.of("pretty").color(TextColor.fromHexString("#ffb6c1")))
      .append(TextComponent.of("in").color(TextColor.fromHexString("#ff69b4")).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
      .append(TextComponent.of("pink").color(TextColor.fromHexString("#ffc0cb")))
      .build();
    assertEquals(component, LegacyComponentSerializer.builder().character('&').hexColors().build().deserialize("&#ffb6c1pretty&#ff69b4&lin&#ffc0cbpink"));
  }

  @Test
  void testToLegacyWithHexColorTerribleFormat() {
    final TextComponent c0 = TextComponent.of("Kittens!", TextColor.of(0xffefd5));
    assertEquals("§x§f§f§e§f§d§5Kittens!", LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build().serialize(c0));
  }

  @Test
  void testFromLegacyWithHexColorTerribleFormat() {
    final TextComponent expected = TextComponent.of("Kittens!", TextColor.of(0xffefd5));
    assertEquals(expected, LegacyComponentSerializer.builder().hexColors().build().deserialize("§x§f§f§e§f§d§5Kittens!"));
  }

  @Test
  void testFromLegacyWithHexColorTerribleFormatMixed() {
    final TextComponent expected = TextComponent.builder("")
      .append(TextComponent.of("Hugs and ", NamedTextColor.RED))
      .append(TextComponent.of("Kittens!", TextColor.of(0xffefd5)))
      .build();
    assertEquals(expected, LegacyComponentSerializer.builder().hexColors().build().deserialize("§cHugs and §x§f§f§e§f§d§5Kittens!"));
  }

  @Test
  void testFromLegacyWithHexColorTerribleFormatEnsureProperLookahead() {
    final TextComponent expected = TextComponent.builder("")
      .append(TextComponent.of("Hugs and ", NamedTextColor.RED))
      .append(TextComponent.of("Kittens!", NamedTextColor.DARK_PURPLE))
      .build();
    assertEquals(expected, LegacyComponentSerializer.builder().hexColors().build().deserialize("§cHugs and §f§f§e§f§d§5Kittens!"));
  }

  @Test
  void testFromLegacyWithHexColorTerribleFormatEnsureMultipleColorsWork() {
    final TextComponent expected = TextComponent.builder("Happy with ")
      .append(TextComponent.of("Lavender and ", TextColor.of(0x6b4668)))
      .append(TextComponent.of("Cyan!", TextColor.of(0xffefd5)))
      .build();
    assertEquals(expected, LegacyComponentSerializer.builder().hexColors().build().deserialize("Happy with §x§6§b§4§6§6§8Lavender and §x§f§f§e§f§d§5Cyan!"));
  }

  @Test
  void testFromLegacyWithHexColorTerribleFormatHangingCharacter() {
    final TextComponent expected = TextComponent.of("Kittens!", NamedTextColor.YELLOW);
    assertEquals(expected, LegacyComponentSerializer.builder().hexColors().build().deserialize("§x§eKittens!"));
  }

  @Test
  void testStackedFormattingFlags() {
    // https://github.com/KyoriPowered/adventure/issues/92
    final String input = "§r§r§c§k||§e§lProfile§c§k||";
    final TextComponent output = TextComponent.builder().append(
      TextComponent.of("||", Style.of(NamedTextColor.RED, TextDecoration.OBFUSCATED)),
      TextComponent.of("Profile", Style.of(NamedTextColor.YELLOW, TextDecoration.BOLD)),
      TextComponent.of("||", Style.of(NamedTextColor.RED, TextDecoration.OBFUSCATED))
    ).build();
    assertEquals(output, LegacyComponentSerializer.legacy().deserialize(input));
  }

  @Test
  void testResetClearsColorInSameBlock() {
    final String input = "§c§rCleared";
    final TextComponent output = TextComponent.of("Cleared");
    assertEquals(output, LegacyComponentSerializer.legacy().deserialize(input));
  }
}
