package jflowsim.model.numerics.lbm;

public class LbEQ {

    // 9 discrete velocity directions of the D2Q9 model
    public static final int ZERO = 0;
    public static final int E = 1;
    public static final int W = 2;
    public static final int N = 3;
    public static final int S = 4;
    public static final int NE = 5;
    public static final int SW = 6;
    public static final int NW = 7;
    public static final int SE = 8;
    public static final double c4o9 = 4. / 9.;
    public static final double c1o3 = 1. / 3.;
    public static final double c1o6 = 1. / 6.;
    public static final double c1o9 = 1. / 9.;
    public static final double c1o36 = 1. / 36.;
    // first and last velocity direction of the D2Q9 model
    public static final int STARTDIR = 0;
    public static final int ENDDIR = 8;
    // velocity vectors
    public static final int ex[] = {0, 1, -1, 0, 0, 1, -1, -1, 1};
    public static final int ey[] = {0, 0, 0, 1, -1, 1, -1, 1, -1};
    // inverse directory
    public static final int invdir[] = {0, 2, 1, 4, 3, 6, 5, 8, 7};
    // weighting factors D2Q9
    public static double w[] = {4. / 9., 1. / 9., 1. / 9., 1. / 9., 1. / 9., 1. / 36., 1. / 36., 1. / 36., 1. / 36.};
    // weighting factors D2Q5
    public static double wT[] = {1. / 3., 1. / 6., 1. / 6., 1. / 6., 1. / 6.};

    public static int[] getE(int dir) {
        int e[] = new int[2];
        e[0] = ex[dir];
        e[1] = ey[dir];

        return e;
    }

    //forcing
    public static double getForcingForDirection(int direction, double forcingX1, double forcingX2) {
        switch (direction) {
            case ZERO:
                return 0.0;
            case E:
                return 1. / 3. * forcingX1;
            case W:
                return -1. / 3. * forcingX1;
            case N:
                return 1. / 3. * forcingX2;
            case S:
                return -1. / 3. * forcingX2;
            case NE:
                return 1. / 12. * (forcingX1 + forcingX2);
            case SW:
                return 1. / 12. * (-forcingX1 - forcingX2);
            case SE:
                return 1. / 12. * (forcingX1 - forcingX2);
            case NW:
                return 1. / 12. * (-forcingX1 + forcingX2);
            default:
                return -999.9;
        }
    }

    public static void getBGKEquilibrium(double rho, double vx, double vy, double f[]) {
        f[ZERO] = c4o9 * rho * (1. - 1.5 * (vx * vx + vy * vy));
        f[E] = c1o9 * rho * (1. + 3.0 * vx + 4.5 * vx * vx - 1.5 * (vx * vx + vy * vy));
        f[N] = c1o9 * rho * (1. + 3.0 * vy + 4.5 * vy * vy - 1.5 * (vx * vx + vy * vy));
        f[W] = c1o9 * rho * (1. - 3.0 * vx + 4.5 * vx * vx - 1.5 * (vx * vx + vy * vy));
        f[S] = c1o9 * rho * (1. - 3.0 * vy + 4.5 * vy * vy - 1.5 * (vx * vx + vy * vy));
        f[NE] = c1o36 * rho * (1. + 3.0 * (vx + vy) + 4.5 * (vx + vy) * (vx + vy) - 1.5 * (vx * vx + vy * vy));
        f[NW] = c1o36 * rho * (1. + 3.0 * (-vx + vy) + 4.5 * (-vx + vy) * (-vx + vy) - 1.5 * (vx * vx + vy * vy));
        f[SW] = c1o36 * rho * (1. - 3.0 * (vx + vy) + 4.5 * (vx + vy) * (vx + vy) - 1.5 * (vx * vx + vy * vy));
        f[SE] = c1o36 * rho * (1. - 3.0 * (-vx + vy) + 4.5 * (-vx + vy) * (-vx + vy) - 1.5 * (vx * vx + vy * vy));
    }

    public static void getBGKEquilibriumTemperature(double T, double vx, double vy, double g[]) {
        g[0] = c1o3 * T;
        g[1] = c1o6 * T * (1. + 3. * vx);
        g[2] = c1o6 * T * (1. - 3. * vx);
        g[3] = c1o6 * T * (1. + 3. * vy);
        g[4] = c1o6 * T * (1. - 3. * vy);
    }

    public static void getBGKEquilibriumShallowWater(double h, double vx, double vy, double feq[], double v_scale, double gravity) {
        vx = vx * v_scale;
        vy = vy * v_scale;

        double hdurchE = h / v_scale;
        double hdurchEE = h / v_scale / v_scale;
        // Gleichgewichtsverteilungen berechnen
        double square = hdurchEE * (vx * vx + vy * vy);
        double dummy = 1. / 6. * gravity * hdurchE * hdurchE;

        feq[LbEQ.ZERO] = h - 5.0 * dummy - 2. / 3. * square;
        feq[LbEQ.E] = dummy + 1. / 3. * hdurchE * vx + 0.5 * hdurchEE * vx * vx - 1. / 6. * square;
        feq[LbEQ.W] = feq[LbEQ.E] - 2. / 3. * hdurchE * vx;

        feq[LbEQ.N] = dummy + 1. / 3. * hdurchE * vy + 0.5 * hdurchEE * vy * vy - 1. / 6. * square;
        feq[LbEQ.S] = feq[LbEQ.N] - 2. / 3. * hdurchE * vy;

        dummy *= 0.25;
        double produkt = vx + vy;
        feq[LbEQ.NE] = dummy + 1. / 12. * hdurchE * produkt + 0.125 * hdurchEE * produkt * produkt - 1. / 24. * square;
        feq[LbEQ.SW] = feq[LbEQ.NE] - 1. / 6. * hdurchE * produkt;

        produkt = -vx + vy;
        feq[LbEQ.NW] = dummy + 1. / 12. * hdurchE * produkt + 0.125 * hdurchEE * produkt * produkt - 1. / 24. * square;
        feq[LbEQ.SE] = feq[LbEQ.NW] - 1. / 6. * hdurchE * produkt;
    }
}
