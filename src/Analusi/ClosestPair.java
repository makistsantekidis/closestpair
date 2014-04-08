package Analusi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public abstract class ClosestPair {

    /**
     * The class Point represents points to the Cartesian plane.
     */
    public static class Point {

        private final double x;
        private final double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        public Point(Point p) {
            this.x = p.getX();
            this.y = p.getY();
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        @Override
        public boolean equals(Object obj) {
            Point otherPoint = (Point) obj;
            return this.x == otherPoint.getX() && this.y == otherPoint.getY();
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    /**
     * The class Pair represents a pair of points including their L2 distance.
     * It should be used to store the closest pair of points.
     */
    public static class Pair {

        private Point point1;
        private Point point2;
        private double distance;

        public Pair() {
        }

        public Pair(Point point1, Point point2) {
            this.point1 = point1;
            this.point2 = point2;
            this.distance = distance(point1, point2);
        }

        public void update(Point point1, Point point2, double distance) {
            this.point1 = point1;
            this.point2 = point2;
            this.distance = distance;
        }

        public Point getPoint1() {
            return point1;
        }

        public void setPoint1(Point point1) {
            this.point1 = point1;
        }

        public Point getPoint2() {
            return point2;
        }

        public void setPoint2(Point point2) {
            this.point2 = point2;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        @Override
        public boolean equals(Object obj) {
            Pair otherPair = (Pair) obj;
            return ( (this.distance == otherPair.getDistance()) && 
                    (( (this.point1.equals(otherPair.getPoint1())) && (this.point2.equals(otherPair.getPoint2())) ) 
                    || ((this.point1.equals(otherPair.getPoint2())) && (this.point2.equals(otherPair.getPoint1())) )) );
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 59 * hash + Objects.hashCode(this.point1);
            hash = 59 * hash + Objects.hashCode(this.point2);
            hash = 59 * hash + (int) (Double.doubleToLongBits(this.distance) ^ (Double.doubleToLongBits(this.distance) >>> 32));
            return hash;
        }

        @Override
        public String toString() {
            return point1 + "-" + point2 + " : " + distance;
        }
    }
    
    /**
     * The distance() method computes the L2 distance between two points.
     * 
     * @param p1 the first point
     * @param p2 the second point
     * @return the L2 distance between the two given points (sqrt(x^2 +y^2))
     */
//    public static double distance(Point p1, Point p2) {
//        double xdist = p2.x - p1.x;
//        double ydist = p2.y - p1.y;
//        return Math.hypot(xdist, ydist);
//    }
    
    public static double distance(Point p1, Point p2) {
        double xdist = p2.x - p1.x;
        double ydist = p2.y - p1.y;
        return hypot(xdist, ydist);
    }
    
    
    private static final double TWO_POW_450 = Double.longBitsToDouble(0x5C10000000000000L);
    private static final double TWO_POW_N450 = Double.longBitsToDouble(0x23D0000000000000L);
    private static final double TWO_POW_750 = Double.longBitsToDouble(0x6ED0000000000000L);
    private static final double TWO_POW_N750 = Double.longBitsToDouble(0x1110000000000000L);
    public static double hypot(double x, double y) {
        x = Math.abs(x);
        y = Math.abs(y);
        if (y < x) {
            double a = x;
            x = y;
            y = a;
        } else if (!(y >= x)) { // Testing if we have some NaN.
            if ((x == Double.POSITIVE_INFINITY) || (y == Double.POSITIVE_INFINITY)) {
                return Double.POSITIVE_INFINITY;
            } else {
                return Double.NaN;
            }
        }
        if (y-x == y) { // x too small to substract from y
            return y;
        } else {
            double factor;
            if (x > TWO_POW_450) { // 2^450 < x < y
                x *= TWO_POW_N750;
                y *= TWO_POW_N750;
                factor = TWO_POW_750;
            } else if (y < TWO_POW_N450) { // x < y < 2^-450
                x *= TWO_POW_750;
                y *= TWO_POW_750;
                factor = TWO_POW_N750;
            } else {
                factor = 1.0;
            }
            return factor * Math.sqrt(x*x+y*y);
        }
    }
    
    
    /**
     * The generatePoints() method generates random double precision points.
     * 
     * @param numPoints defines the number of points to be generated
     * @return a List of random points 
     */
    public static List<Point> generatePoints(int numPoints){
        List<Point> points = new ArrayList<Point>();
        Random r = new Random();

        for (int i = 0; i < numPoints; i++) {
            points.add(new Point(r.nextDouble(), r.nextDouble()));
        }
//        System.out.println("Generated " + numPoints + " random points");
        return points;
    }
    
    /**
     * Executes both bruteForce and D&C approach and checks whether their results are equal
     * 
     * @param list of points
     * @return 
     */
    public boolean evaluateResult(List<Point> points){
        Pair bfClosestPair = bruteForce(points);
        Pair dcCLosestPair = initializeDivideAndConquer(points);
//        System.out.println(bfClosestPair);
//        System.out.println(dcCLosestPair);
        return bfClosestPair.equals(dcCLosestPair);
    }

    /**
     * The straightforward solution is a O(n^2) algorithm (which we can call brute-force algorithm).
     * 
     * @param points the set of points to be examined
     * @return a Pair of the closest points
     */
    public static Pair bruteForce(List<Point> points) {
        int numPoints = points.size();
        if (numPoints < 2) {
            return null;
        }
        Pair pair = new Pair(points.get(0), points.get(1));
        if (numPoints > 2) {
            for (int i = 0; i < numPoints - 1; i++) {
                Point point1 = points.get(i);
                for (int j = i + 1; j < numPoints; j++) {
                    Point point2 = points.get(j);
                    double distance = distance(point1, point2);
                    if (distance < pair.getDistance()) {
                        pair.update(point1, point2, distance);
                    }
                }
            }
        }
        return pair;
    }
    
    /**
     * The method sortByX sorts the points according to their x coordinate. 
     *  
     * @param points the set of points to be sorted
     */
    public void sortByX(List<Point> points) {
        
        Collections.sort(points, new Comparator<Point>() {
            @Override
            public int compare(Point point1, Point point2) {
                if (point1.getX() < point2.getX()) {
                    return -1;
                }
                if (point1.getX() > point2.getX()) {
                    return 1;
                }
                return 0;
            }
        });
    }
    
    /**
     * The method sortByY sorts the points according to their y coordinate. 
     * 
     * @param points the set of points to be sorted
     */
    public void sortByY(List<Point> points) {
        Collections.sort(points, new Comparator<Point>() {
            @Override
            public int compare(Point point1, Point point2) {
                if (point1.getY() < point2.getY()) {
                    return -1;
                }
                if (point1.getY() > point2.getY()) {
                    return 1;
                }
                return 0;
            }
        });
    }
    
    /**
     * Initializes the divideAndConquer method to begin the recursion.
     * 
     * @param points the set of points to be examined
     * @return a Pair of the closest points
     */
    public Pair initializeDivideAndConquer(List<Point> points) {
        List<Point> pointsSortedByX = new ArrayList<>(points);
        sortByX(pointsSortedByX);
        List<Point> pointsSortedByY = new ArrayList<Point>(points);
        sortByY(pointsSortedByY);
        return divideAndConquer(pointsSortedByX, pointsSortedByY);
    }
    
    /**
     * The recursive divide&conquer approach, as explained also at <a href="http://en.wikipedia.org/wiki/Closest_pair_of_points_problem#Planar_case">Wikipedia</a>, which SHOULD be a O(n*log(n)) algorithm
     * 
     * @param points the set of points to be examined
     * @return a Pair of the closest points
     */
    public abstract Pair divideAndConquer(List<Point> pointsSortedByX, List<Point> pointsSortedByY);
}
