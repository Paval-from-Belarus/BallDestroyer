package com.baller.game.field;

import com.baller.game.common.SquareCollider;
import org.jetbrains.annotations.NotNull;

public record BrickCollision(@NotNull BrickBlock brick, @NotNull SquareCollider.SideType type) {
}
