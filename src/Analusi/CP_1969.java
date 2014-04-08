package Analusi;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation Class.
 * Some words about the implementation and some notices. Running this program
 * through a profiler I found out, that the function distance is taking way too
 * long, almost most of the run time of the program. So I found a better
 * implementation of the hypot function 
 * at http://stackoverflow.com/a/3768943/1150636
 * Using that implementation i
 * decreased the execution time for 1,000,000 points from 8 seconds to 1 second
 * of which was most build time. Trying for 10,000,000 points the program took
 * 25 seconds to complete of which 4 seconds was generating the random points.
 * I intenionally left the implementation in the ClosestPair class intenionally
 * so you can check it out if you wish. Of course it is commented out so I don't
 * break the rule of "no changing the ClosestPair.java file"
 * 
 * Also there seems to be possibility of improvement in the sorting algorithm
 * using something more custom instead of the java default Collection.sort which
 * uses merge sort.
 *
 * @author Avraam Tsantekidis
 * @aem 1969
 * @email avraamt@csd.auth.gr
 */
public class CP_1969 extends ClosestPair {

    /**
     * This is a recursive function that uses a modified divide and conquer
     * algorithm.
     *
     * (similar to the one explained {
     *
     * @here
     * http://en.wikipedia.org/wiki/Closest_pair_of_points_problem#Planar_case}
     * It begins by checking the size of the points array. if the array is less
     * than 4 points then it bruteforces for the solution since divide and
     * conquer is an overkill and not feasible in this recursive implementation.
     * If the size is more than 4 points then the functions splits the points
     * array by the median point (middle element in the array since the array is
     * sorted) and then the two parts get recursively sorted by this function.
     * The two results from the left side(Lmin) of the array and the right
     * side(Rmin) of the array are compared and the pair with the least distance
     * is considered the current closestpair. Taking advantage of the fact that
     * the current closestpair should have less than min(Lmin,Rmin) distance, we
     * create a subList that contains elements that have maximum x-axis distance
     * min(Lmin,Rmin) for the median and then use a clone of this function that
     * uses the y-axis to find the closest pair. If the points around the median
     * are less than 6 then there is a chance that the y-axis analysis will not
     * help in finding the median so we just bruteforce the 6 or less points
     * sublist for the closest pair. In the end, compare the LRmin with the
     * current closestpair and return the smallest distance.
     *
     * @param points list of points
     * @return closest pair of points from the given list
     */
    private Pair findClosestPairbyX(List<Point> points) {

//        sortByX(points);
        Pair closestpair = new Pair();
        // If the array is more that 3 then go use divide and conquer
        if (points.size() > 3) {

            // Split the array into two parts in a way that it is guaranteed none
            // of them is going to have only one point.
            int split = (points.size() + points.size() % 2) / 2 - points.size() % 2;
            double xsplit = points.get(split).getX();

            // Recursively find the closest pair from the left and 
            // from the right of the meadian xsplit
            Pair Lmin = findClosestPairbyX(points.subList(0, split));
            Pair Rmin = findClosestPairbyX(points.subList(split, points.size()));

            // find the closest pair between Lmin and Rmin
            closestpair = Lmin;
            if (Rmin.getDistance() < closestpair.getDistance()) {
                closestpair = Rmin;
            }

            // Use dmin, which is the closest pair from the left and the right
            // side and get all the points that are at most that distance away
            // (in the x-axis) from the median 
            double dmin = closestpair.getDistance();
            int startd = 0;
            int endd = points.size();
            int i = 0;

            // Starting from left (in the sorted x-axis array find the first
            // points that is dmin distance at most away from median on the
            // left side of the x-axis
            for (; i < points.size(); ++i) {
                if (xsplit - points.get(i).getX() <= dmin) {
                    startd = i;
                    break;
                }
            }

            // Continuing from the previous variable i look for the furthest point
            // from the median on the right side of the x-axis
            for (; i < points.size(); ++i) {
                if (points.get(i).getX() - xsplit > dmin) {
                    endd = i;
                    break;
                }
            }

            // If there is only one point return the closest pair
            // from the two separate sides
            if (endd - startd == 1) {
                return closestpair;
            }

            Pair LRmin = null;
            // If there are 6 or less points then bruteforce it since it is possible
            // that we won't benefit from splitting the points on the y-axis.
            if (points.size() <= 6) {
                LRmin = bruteForce(points);
            } // If the points are more than six then split them by the y-axis and
            // try to do the same divide and conquer as with did in this whole function
            else {
                LRmin = findClosestPairbyY(points.subList(startd, endd));
            }

            // Check if it closest pair is closer that the closest pair 
            // from the left and the right of the median
            if (LRmin != null && LRmin.getDistance() < closestpair.getDistance()) {
                closestpair = LRmin;
            }
            return closestpair;
        } // If the array is 3 points then just bruteforce for the closest pair
        else if (points.size() == 3) {
            closestpair = bruteForce(points);
            return closestpair;
        } // If the array is 2 points then just return the pair of the two points
        // (since it is the closest pair in a group of one pair)
        else if (points.size() == 2) {
            return new Pair(points.get(0), points.get(1));
        }

        return closestpair;
    }

    /**
     * This function is a copy of the findClosestPairbyX function but gets
     * called second and only in specific cases where points are more spread out
     * in the y-axis and closer in the x-axis
     *
     * @param points2 list of points
     * @return closest pair of points from the given list
     *
     *
     *
     */
    private Pair findClosestPairbyY(List<Point> points2) {
        List<Point> points = new ArrayList<>(points2);
        sortByY(points);
        Pair closestpair = new Pair();
        if (points.size() > 3) {
            // Split the array into two parts in a way that it is guaranteed none
            // of them is going to have only one point.
            int split = (points.size() + points.size() % 2) / 2 - points.size() % 2;
            double ysplit = points.get(split).getY();

            // Recursively find the closest pair from the left and 
            // from the right of the meadian xsplit
            Pair Dmin = findClosestPairbyY(points.subList(0, split));
            Pair Umin = findClosestPairbyY(points.subList(split, points.size()));

            // find the closest pair between Lmin and Rmin
            closestpair = Dmin;
            if (Umin.getDistance() < closestpair.getDistance()) {
                closestpair = Umin;
            }

            // Use dmin, which is the closest pair from the left and the right
            // side and get all the points that are at most that distance away
            // (in the x-axis) from the median 
            double dmin = closestpair.getDistance();
            int startd = 0;
            int endd = points.size();
            int i = 0;

            // Starting from left (in the sorted x-axis array find the first
            // points that is dmin distance at most away from median on the
            // left side of the x-axis
            for (; i < points.size(); ++i) {
                if (ysplit - points.get(i).getY() <= dmin) {
                    startd = i;
                    break;
                }
            }

            // Continuing from the previous variable i look for the furthest point
            // from the median on the right side of the x-axis
            for (; i < points.size(); ++i) {
                if (points.get(i).getY() - ysplit > dmin) {
                    endd = i;
                    break;
                }
            }

            // If there is only one point return the closest pair
            // from the two separate sides
            if (endd - startd == 1) {
                return closestpair;
            }

            Pair UDmin = null;
            // If all the points are close to the median and the dmin does not
            // filter any points from stard to endd, there are probably few points
            // to calculate the minimum distance so just bruteforce it.
            if (points.size() <= 6) {
                UDmin = bruteForce(points);
            } // If there are points filtered, use recursive logic to find the 
            // closest pair.
            else {
//                LRmin = bruteForce(points.subList(startd, endd));
                UDmin = findClosestPairbyX(points.subList(startd, endd));
            }

            // Check if it closest pair is closer that the closest pair 
            // from the left and the right of the median
            if (UDmin != null && UDmin.getDistance() < closestpair.getDistance()) {
                closestpair = UDmin;
            }

            return closestpair;
        } // If the array is 3 points then just bruteforce for the closest pair
        else if (points.size() == 3) {
            closestpair = bruteForce(points);
            return closestpair;
        } // If the array is 2 points then just return the pair of the two points
        // (since it is the closest pair in a group of one pair)
        else if (points.size() == 2) {
            return new Pair(points.get(0), points.get(1));
        }
        return closestpair;
    }

    @Override
    public Pair divideAndConquer(List<Point> pointsSortedByX, List<Point> pointsSortedByY) {
        Pair closestPair = new Pair(pointsSortedByX.get(0),pointsSortedByX.get(1));
        
        // idea that did not work
//        for (int i=2;i<pointsSortedByX.size();++i)
//            if (closestPair.getDistance()>distance(pointsSortedByX.get(i-1), pointsSortedByX.get(i)))
//                closestPair = new Pair(pointsSortedByX.get(i-1), pointsSortedByX.get(i));
//        
//        for (int i=2;i<pointsSortedByY.size();++i)
//            if (closestPair.getDistance()>distance(pointsSortedByY.get(i-1), pointsSortedByY.get(i)))
//                closestPair = new Pair(pointsSortedByY.get(i-1), pointsSortedByY.get(i));
        
        closestPair = findClosestPairbyX(pointsSortedByX);
        return closestPair;
    }

    /**
     * Main function.
     *
     *
     *
     * @algorithm
     *
     *
     * @param args
     */
    public static void main(String[] args) {

        List<Point> points = generatePoints(1000);
        CP_1969 dc = new CP_1969();
        System.out.println(dc.evaluateResult(points) ? "Correct" : "Wrong");

//        List<Point> points = generatePoints(10000000);
//        CP_1969 dc = new CP_1969();
//        dc.sortByX(points);
//        System.out.println(dc.divideAndConquer(points, points));

    }

}
