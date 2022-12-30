package io.github.arcaneplugins.entitylabellib.bukkit.nms.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;

/**
 * This class contains utilities for using components, such as
 * converting chat component objects to those in different APIs.
 *
 * @author <a href="https://www.spigotmc.org/members/dman16.941198/">DMan16</a>
 */
public class ComponentUtils {

    private ComponentUtils() throws IllegalAccessException {
        throw new IllegalAccessException("Unable to instantiate utility class");
    }

    /**
     * Converts an Adventure Component object to an NMS MutableComponent object.
     * <p>
     * Written by DMan16 with slight modifications by lokka30.
     *
     * @param advComp Adventure Component object
     * @return NMS MutableComponent object
     * @since 1.0.0
     */
    public static @Nonnull MutableComponent adventureToNmsComponent(
        final @Nullable net.kyori.adventure.text.Component advComp
    ) {
        if(advComp == null) {
            return Component.empty();
        }

        final ComponentContents contents;
        MutableComponent nmsComp;
        String textContent = null;

        if(advComp instanceof final TextComponent textComponent) {
            textContent = textComponent.content();
            contents = new LiteralContents(textContent);
        } else if(advComp instanceof final TranslatableComponent translatableComponent) {
            if(translatableComponent.args().isEmpty()) {
                contents = new TranslatableContents(translatableComponent.key());
            } else {
                contents = new TranslatableContents(
                    translatableComponent.key(),
                    translatableComponent.args().stream()
                        .map(ComponentUtils::adventureToNmsComponent)
                        .toArray()
                );
            }
        } else {
            return Component.empty();
        }

        nmsComp = MutableComponent.create(contents);

        net.kyori.adventure.text.format.TextColor advColor = advComp.color();
        TextColor nmsColor = null;
        if(advColor != null) {
            if(advColor instanceof final NamedTextColor named) {
                nmsColor = TextColor.fromLegacyFormat(
                    ChatFormatting.getByName(named.toString())
                );
            } else {
                nmsColor = TextColor.fromRgb(advColor.value());
            }
        }
        Style nmsStyle = Style.EMPTY;
        if(nmsColor != null)
            nmsStyle = nmsStyle.withColor(nmsColor);

        final Function<TextDecoration, Boolean> hasDecoration = (decoration) ->
            advComp.decoration(decoration) == State.TRUE;

        if(hasDecoration.apply(TextDecoration.BOLD))
            nmsStyle = nmsStyle.withBold(true);
        if(hasDecoration.apply(TextDecoration.ITALIC))
            nmsStyle = nmsStyle.withItalic(true);
        if(hasDecoration.apply(TextDecoration.OBFUSCATED))
            nmsStyle = nmsStyle.withObfuscated(true);
        if(hasDecoration.apply(TextDecoration.STRIKETHROUGH))
            nmsStyle = nmsStyle.withStrikethrough(true);
        if(hasDecoration.apply(TextDecoration.UNDERLINED))
            nmsStyle = nmsStyle.withUnderlined(true);

        nmsComp.setStyle(nmsStyle);

        if(advComp.children().isEmpty())
            return nmsComp;

        List<MutableComponent> nmsChildren = advComp.children().stream()
            .map(ComponentUtils::adventureToNmsComponent)
            .collect(Collectors.toList());

        if(textContent != null && textContent.isEmpty()) {
            if(nmsChildren.isEmpty())
                return Component.empty();

            nmsComp = nmsChildren.get(0);
            nmsChildren.remove(0);
        }

        nmsChildren.forEach(nmsComp::append);
        return nmsComp;
    }

}
