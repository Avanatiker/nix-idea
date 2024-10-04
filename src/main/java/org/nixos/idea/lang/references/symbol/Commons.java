package org.nixos.idea.lang.references.symbol;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.platform.backend.presentation.TargetPresentation;
import com.intellij.platform.backend.presentation.TargetPresentationBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

@SuppressWarnings("UnstableApiUsage")
final class Commons {

    static final @NotNull Icon ICON_ATTRIBUTE = AllIcons.Nodes.Property;
    static final @NotNull Icon ICON_BUILTIN = AllIcons.Nodes.Padlock;
    static final @NotNull Icon ICON_PARAMETER = AllIcons.Nodes.Parameter;
    static final @NotNull Icon ICON_VARIABLE = AllIcons.Nodes.Variable;

    private Commons() {} // Cannot be instantiated

    static @NotNull TargetPresentationBuilder buildPresentation(@NotNull String name, @NotNull Icon icon, @NotNull TextAttributesKey textAttributesKey) {
        EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getSchemeForCurrentUITheme();
        return TargetPresentation.builder(name)
                .icon(icon)
                .presentableTextAttributes(colorsScheme.getAttributes(textAttributesKey));
    }
}
