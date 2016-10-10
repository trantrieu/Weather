package tretnt.vn.weather;

import android.util.Log;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
//    @Test
//    public void addition_isCorrect() throws Exception {
//        double asDouble = Double.parseDouble("98.86"); // don't catch this NumberFormatException.
//        long result = (long) asDouble;
//        if (result != asDouble) { // Make sure no precision was lost casting to 'long'.
//
//        }
//        assertEquals(4, 2 + 2);
//    }
    int INCREASE = 1;
    int DECREASE = 0;
    int EQUAL = -1;
    int INIT = -999;
    @Test
    public void dynamicProgramming2(){
//        String str = "10  \n" +
//                "2\n" +
//                "4\n" +
//                "2\n" +
//                "6\n" +
//                "1\n" +
//                "7\n" +
//                "8\n" +
//                "9\n" +
//                "2\n" +
//                "1";
//        //1 2 1 2 1 2 3 4 2 1

        String str = "12 7 6 5 4 10 12 6 4 5 3 2 1";
        // 7 6 5 4 10 12 6 4 5 3 2 1
        // 4 3 2 1 2  3  2 1 4 3 2 1

        Scanner scanner = new Scanner(str);

        int N = scanner.nextInt();
        int scores[] = new int[N];
        int candies[] = new int[N];
        int firstContinueGreater = 0;
        long totalCandies = N;
        boolean isSmaller = false;
        scores[0] = scanner.nextInt();
        for(int i = 1; i < N; ++i ){
            scores[i] = scanner.nextInt();
            if((scores[i] > scores[i-1]) && (candies[i] <= candies[i-1])) {
                //update with new candies
                totalCandies += candies[i-1]+1 - candies[i];
                candies[i] = candies[i-1]+1;
            }
            else if(!isSmaller && scores[i] != scores[i-1]) {
                firstContinueGreater = i-1;
                isSmaller = true;
            }
        }

        //Reverse traversal to update with latest candies
        for(int i = N-2; i >= firstContinueGreater; i--)  {
            if((scores[i] > scores[i+1]) &&
                    (candies[i] <= candies[i+1])) {
                //update with new candies
                totalCandies += candies[i+1]+1 - candies[i];
                candies[i] = candies[i+1]+1;
            }
        }

        Log.e("", "total = "+totalCandies);

//        int N = scanner.nextInt();
//
//        int scorePre = scanner.nextInt();
//        int scoreNext;
//
//        int countCandy = 1;
//
//        int direction = INIT;
//        List<Integer> array = new ArrayList<>();
//        array.add(scorePre);
//        for(int i = 2 ; i < N ; i ++){
//            scoreNext = scanner.nextInt();
//            if(scoreNext > scorePre){
//                if(direction == INIT){
//                    direction = INCREASE;
//                    array.add(scoreNext);
//                }else{
//                    if(direction == INCREASE){
//                        array.add(scoreNext);
//                    }else{
//                        handleChangeDirection(INCREASE, direction, array);
//                        direction = INCREASE;
//                    }
//                }
//            }else if(scoreNext < scorePre){
//                if(direction == INIT){
//                    direction = DECREASE;
//                }else{
//                    if(direction == DECREASE){
//                        array.add(scoreNext);
//                    }else{
//                        direction = DECREASE;
//                    }
//                }
//            }else{
//                if(direction == INIT){
//                    direction = EQUAL;
//                }else{
//                    if(direction == EQUAL){
//                        array.add(scoreNext);
//                    }else{
//                        direction = EQUAL;
//                    }
//                }
//            }
//            scorePre = scoreNext;
//        }
        scanner.close();
    }

    private void handleChangeDirection(int newDirection, int oldDirection, List<Integer> array){
        if(oldDirection == INCREASE){
            int count = array.size();
            int totalCandyHere = 0;
            for(int i : array){
                totalCandyHere += i;
            }

        }
    }

    //@Test
    public void dynamicProgramming(){
        String str = "1 \n" +
                "7\n" +
                "2 -1 2 3 4 -5 6";
        Scanner scanner = new Scanner(str);
        int T = scanner.nextInt();

        for(int i = 0 ; i < T ; i ++){
            int N = scanner.nextInt();
            int []array = new int[N];
            for(int j = 0 ; j < N ; j ++){
                array[j] = scanner.nextInt();
            }
            doDynamicProgramming(array);
        }
        scanner.close();
    }

    private void doDynamicProgramming(int[]array){
        int maxEnding = 0;
        int maxSoFar = Integer.MIN_VALUE;
        for(int i : array){
            maxEnding = Math.max(i, maxEnding + i);
            maxSoFar = Math.max(maxEnding, maxSoFar);
        }
        System.out.print(maxSoFar);

        Arrays.sort(array);
        int sum = 0;
        if(array.length == 1){
            sum = array[0];
        }else {
            for (int i : array) {
                if (i > 0) {
                    sum += i;
                }
            }
        }
        System.out.println(" "+sum);
    }
}