package org.nixos.idea.interpretation;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nixos.idea.psi.NixAttr;
import org.nixos.idea.psi.NixAttrPath;
import org.nixos.idea.psi.NixBindInheritVar;
import org.nixos.idea.psi.NixExpr;
import org.nixos.idea.psi.NixExprSelect;
import org.nixos.idea.psi.NixExprVar;
import org.nixos.idea.psi.NixInheritedName;
import org.nixos.idea.psi.NixPsiElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Usage of a variable. This class is shallowly immutable.
 *
 * @see NixPsiElement#getUsages()
 */
public final class VariableUsage {
    private final @NotNull NixPsiElement myElement;
    private final @NotNull AttributePath myUsedPath;
    private final @NotNull NixPsiElement[] myAttributeElements;

    private VariableUsage(@NotNull Builder builder) {
        assert !builder.myAttributes.isEmpty();
        assert builder.myAttributeElements.size() == builder.myAttributes.size();
        myElement = builder.myElement;
        myUsedPath = AttributePath.of(builder.myAttributes);
        myAttributeElements = builder.myAttributeElements.toArray(NixPsiElement[]::new);
    }

    /**
     * The element which uses the variable.
     * The returned element may be one of the following types:
     * <ul>
     *     <li>{@link NixExprSelect}
     *     <li>{@link NixExprVar} when it is not part of {@link NixExprSelect}
     *     <li>{@link NixInheritedName} as part of {@link NixBindInheritVar}
     * </ul>
     *
     * @return the element which uses the variable.
     */
    @Contract(pure = true)
    public @NotNull NixPsiElement element() {
        assert myElement instanceof NixExprSelect || myElement instanceof NixExprVar ||
                myElement instanceof NixInheritedName && myElement.getParent() instanceof NixBindInheritVar
                : "element type does not match Javadoc: " + myElement.getClass();
        return myElement;
    }

    /**
     * The variable name and the attributes used on the variable.
     * The returned attribute path has exactly one attribute if {@link #element()} is not {@link NixExprSelect}.
     *
     * @return The accessed attribute path, including the variable name as the first attribute.
     */
    @Contract(pure = true)
    public @NotNull AttributePath path() {
        return myUsedPath;
    }

    /**
     * The elements defining the individual attributes ot the {@linkplain #path() attribute path}.
     * The first element may have the type {@link NixExprVar} or {@link NixAttr}.
     * The remaining elements all have the type {@link NixAttr}.
     *
     * @return The elements defining the individual attributes.
     */
    @Contract(pure = true)
    public @NotNull NixPsiElement @NotNull [] attributeElements() {
        assert myAttributeElements[0] instanceof NixExprVar || myAttributeElements[0] instanceof NixAttr
                : "element type does not match Javadoc: " + myAttributeElements[0].getClass();
        assert Arrays.stream(myAttributeElements).skip(1).allMatch(el -> el instanceof NixAttr)
                : "element type does not match Javadoc";
        return myAttributeElements.clone();
    }

    /**
     * Returns the usage exerted by the given element.
     * This method will only return the usages exerted by the specific element, not by children of the element.
     * You may use {@link NixPsiElement#getUsages()} to get all usages from the element and its children.
     *
     * @param element The element which may use some variable.
     * @return The usage exerted by the given element, or {@code null}.
     */
    @Contract(pure = true)
    public static @Nullable VariableUsage by(@NotNull NixPsiElement element) {
        if (element instanceof NixExprVar && !(element.getParent() instanceof NixExprSelect)) {
            NixExprVar value = (NixExprVar) element;
            return new Builder(value)
                    .addAttribute(value)
                    .build();
        } else if (element instanceof NixExprSelect) {
            NixExprSelect expr = (NixExprSelect) element;
            NixExpr value = expr.getValue();
            NixAttrPath attrPath = expr.getAttrPath();
            if (attrPath != null && value instanceof NixExprVar) {
                Builder builder = new Builder(expr).addAttribute((NixExprVar) value);
                for (NixAttr attr : attrPath.getAttrList()) {
                    builder.addAttribute(attr);
                }
                return builder.build();
            }
            return null;
        } else if (element instanceof NixInheritedName && element.getParent() instanceof NixBindInheritVar) {
            return new Builder(element).addAttribute(((NixInheritedName) element).getAttr()).build();
        } else {
            return null;
        }
    }

    private static final class Builder {
        private final @NotNull NixPsiElement myElement;
        private final @NotNull List<Attribute> myAttributes = new ArrayList<>();
        private final @NotNull List<NixPsiElement> myAttributeElements = new ArrayList<>();

        private Builder(@NotNull NixPsiElement element) {
            myElement = element;
        }

        @Contract("_ -> this")
        private @NotNull Builder addAttribute(@NotNull NixExprVar element) {
            return addAttribute(Attribute.of(element), element);
        }

        @Contract("_ -> this")
        private @NotNull Builder addAttribute(@NotNull NixAttr element) {
            return addAttribute(Attribute.of(element), element);
        }

        @Contract("_, _ -> this")
        private @NotNull Builder addAttribute(@NotNull Attribute attribute, @NotNull NixPsiElement element) {
            myAttributes.add(attribute);
            myAttributeElements.add(element);
            return this;
        }

        @Contract(pure = true)
        private @NotNull VariableUsage build() {
            return new VariableUsage(this);
        }
    }
}