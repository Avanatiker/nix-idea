package org.nixos.idea.lang.navigation;

import com.intellij.model.Pointer;
import com.intellij.navigation.NavigationRequest;
import com.intellij.navigation.NavigationTarget;
import com.intellij.navigation.TargetPresentation;
import com.intellij.pom.Navigatable;
import com.intellij.psi.SmartPointerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nixos.idea.psi.NixDeclarationElement;
import org.nixos.idea.psi.NixParamName;
import org.nixos.idea.psi.NixPsiElement;

@SuppressWarnings("UnstableApiUsage")
public final class NixNavigationTarget implements NavigationTarget {
    private final @NotNull NixPsiElement myTargetElement;
    private @Nullable Pointer<NavigationTarget> myPointer;

    private NixNavigationTarget(@NotNull NixPsiElement targetElement,
                                @Nullable Pointer<NavigationTarget> pointer) {
        myTargetElement = targetElement;
        myPointer = pointer;
        assert pointer == null || this.equals(pointer.dereference());
    }

    public static @NotNull NavigationTarget of(@NotNull NixParamName targetElement) {
        return new NixNavigationTarget(targetElement, null);
    }

    public static @NotNull NavigationTarget of(@NotNull NixDeclarationElement targetElement) {
        return new NixNavigationTarget(targetElement, null);
    }

    @Override
    public @NotNull Pointer<NavigationTarget> createPointer() {
        if (myPointer == null) {
            myPointer = Pointer.uroborosPointer(
                    SmartPointerManager.createPointer(myTargetElement),
                    NixNavigationTarget::new);
        }
        return myPointer;
    }

    @Override
    public @NotNull TargetPresentation presentation() {
        return TargetPresentation.builder(myTargetElement.getText())
                .presentation();
    }

    @Override
    public @Nullable NavigationRequest navigationRequest() {
        return ((Navigatable) myTargetElement.getNavigationElement()).navigationRequest();
    }
}