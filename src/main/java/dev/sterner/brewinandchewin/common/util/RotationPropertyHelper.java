package dev.sterner.brewinandchewin.common.util;

import net.minecraft.util.math.Direction;

import java.util.Optional;

public class RotationPropertyHelper {
    private static final RotationCalculator CALCULATOR = new RotationCalculator(4);
    private static final int MAX;
    private static final int NORTH = 0;
    private static final int EAST = 4;
    private static final int SOUTH = 8;
    private static final int WEST = 12;

    public RotationPropertyHelper() {
    }

    public static int getMax() {
        return MAX;
    }

    public static int fromDirection(Direction direction) {
        return CALCULATOR.toRotation(direction);
    }

    public static int fromYaw(float yaw) {
        return CALCULATOR.toClampedRotation(yaw);
    }

    public static Optional<Direction> toDirection(int rotation) {
        Direction var10000;
        switch (rotation) {
            case 0:
                var10000 = Direction.NORTH;
                break;
            case 4:
                var10000 = Direction.EAST;
                break;
            case 8:
                var10000 = Direction.SOUTH;
                break;
            case 12:
                var10000 = Direction.WEST;
                break;
            default:
                var10000 = null;
        }

        Direction direction = var10000;
        return Optional.ofNullable(direction);
    }

    public static float toDegrees(int rotation) {
        return CALCULATOR.toWrappedDegrees(rotation);
    }

    static {
        MAX = CALCULATOR.getMax();
    }
}
