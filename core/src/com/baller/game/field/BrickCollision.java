package com.baller.game.field;

import com.badlogic.gdx.math.Vector2;
import com.baller.game.common.SquareCollider;
import org.jetbrains.annotations.NotNull;

public record BrickCollision(@NotNull BrickBlock brick, @NotNull Vector2 direction) {
}
