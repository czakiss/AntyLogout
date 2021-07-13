package czakiss.antylogout.model;

import org.bukkit.util.Vector;

import java.util.ArrayList;

public class DrawParticleShere {
    private static double lengthSq(double x, double y, double z) {
        return (x * x) + (y * y) + (z * z);
    }

    public static ArrayList<Vector> makeSphere(double radiusX, double radiusY, double radiusZ, double dotsDistance, boolean filled) {
        ArrayList<Vector> pos = new ArrayList<Vector>();

        radiusX += 0.5;
        radiusY += 0.5;
        radiusZ += 0.5;

        final double invRadiusX = 1 / radiusX;
        final double invRadiusY = 1 / radiusY;
        final double invRadiusZ = 1 / radiusZ;

        final double ceilRadiusX = Math.ceil(radiusX);
        final double ceilRadiusY = Math.ceil(radiusY);
        final double ceilRadiusZ = Math.ceil(radiusZ);

        double nextXn = 0;
        forX: for (double x = 0; x <= ceilRadiusX; x += dotsDistance) {
            final double xn = nextXn;
            nextXn = (x + dotsDistance) * invRadiusX;
            double nextYn = 0;
            forY: for (double y = 0; y <= ceilRadiusY; y += dotsDistance) {
                final double yn = nextYn;
                nextYn = (y + dotsDistance) * invRadiusY;
                double nextZn = 0;
                forZ: for (double z = 0; z <= ceilRadiusZ; z += dotsDistance) {
                    final double zn = nextZn;
                    nextZn = (z + dotsDistance) * invRadiusZ;
                    double distanceSq = lengthSq(xn, yn, zn);
                    if (distanceSq > 1) {
                        if (z == 0) {
                            if (y == 0) {
                                break forX;
                            }
                            break forY;
                        }
                        break forZ;
                    }

                    if (!filled) {
                        if (lengthSq(nextXn, yn, zn) <= 1 && lengthSq(xn, nextYn, zn) <= 1 && lengthSq(xn, yn, nextZn) <= 1) {
                            continue;
                        }
                    }

                    pos.add(new Vector(x, y, z));
                    pos.add(new Vector(-x, y, z));
                    pos.add(new Vector(x, -y, z));
                    pos.add(new Vector(x, y, -z));
                    pos.add(new Vector(-x, -y, z));
                    pos.add(new Vector(x, -y, -z));
                    pos.add(new Vector(-x, y, -z));
                    pos.add(new Vector(-x, -y, -z));
                }
            }
        }
        return pos;
    }
}
