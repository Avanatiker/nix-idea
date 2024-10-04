package org.nixos.idea.lang.references.symbol;

import com.intellij.model.Pointer;
import com.intellij.platform.backend.presentation.TargetPresentation;
import org.jetbrains.annotations.NotNull;
import org.nixos.idea.lang.builtins.NixBuiltin;
import org.nixos.idea.lang.highlighter.NixTextAttributes;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
final class NixBuiltinSymbol extends NixSymbol
        implements Pointer<NixBuiltinSymbol> {

    private final @NotNull NixBuiltin myBuiltin;

    NixBuiltinSymbol(@NotNull NixBuiltin builtin) {
        myBuiltin = builtin;
    }

    @Override
    public @NotNull String getName() {
        return myBuiltin.name();
    }

    @Override
    public @NotNull Pointer<NixBuiltinSymbol> createPointer() {
        return this;
    }

    @Override
    public @NotNull NixBuiltinSymbol dereference() {
        return this;
    }

    @Override
    public @NotNull Collection<NixSymbol> resolve(@NotNull String attributeName) {
        NixBuiltin builtin = myBuiltin == NixBuiltin.ROOT ? NixBuiltin.resolveBuiltin(attributeName) : null;
        return List.of(builtin == null ? new NixAdHocSymbol(this, attributeName) : new NixBuiltinSymbol(builtin));
    }

    @Override
    public @NotNull TargetPresentation presentation() {
        return Commons.buildPresentation(myBuiltin.name(), Commons.ICON_BUILTIN, NixTextAttributes.BUILTIN)
                .presentation();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NixBuiltinSymbol builtin = (NixBuiltinSymbol) o;
        return Objects.equals(myBuiltin, builtin.myBuiltin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(myBuiltin);
    }
}
