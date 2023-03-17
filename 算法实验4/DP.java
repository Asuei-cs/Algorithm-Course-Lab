package DP;

import java.util.*;


public class DP {
//    int GoldenPot[]={4,6,2,3};
//    int PotNum=4;
//    int dp[][]=new int[PotNum][PotNum];//差距
    int GoldenPot[];
    int PotNum;
    int dp[][];//差距
    int dpOne[];
    int sumA; int sumB;
    int maxA=0;
    Map<String,Integer> dpMap=new HashMap<>();
    Stack<Integer> Answer=new Stack<>();
    Stack<Integer> bruteAnswer=new Stack<>();
    //小问题，大问题：金罐的个数
    public static  void main(String[] args){
        int n=300000;
        DP dp=new DP();
        dp.createPots(n);

        //dp.backtracking(0,n-1);


//        long startTime1=System.currentTimeMillis();
//        dp.BruteForce(0,n-1);
//        long endTime1=System.currentTimeMillis();
//        System.out.println(endTime1-startTime1+"ms");


//        long startTime2=System.currentTimeMillis();
//        dp.dp();
//        //System.out.println(dp.dp());
//        long endTime2=System.currentTimeMillis();
//        System.out.println(endTime2-startTime2+"ms");
        //System.out.println(Arrays.deepToString(dp.dp));

//        long startTime3=System.currentTimeMillis();
//        System.out.println(dp.Map_dp());
//        long endTime3=System.currentTimeMillis();
//        System.out.println(endTime3-startTime3+"ms");

        long startTime4=System.currentTimeMillis();
        dp.One_dp();
       // System.out.println(dp.One_dp());
        long endTime4=System.currentTimeMillis();
        System.out.println(endTime4-startTime4+"ms");

    //dp.getAnswer();

    }
    public void createPots(int n){
        GoldenPot=new int[n];
        for (int i=0;i<n;i++){
            GoldenPot[i]= new Random().nextInt(n*2)+1;//1~1000
        }
        PotNum=n;

         //System.out.println(Arrays.toString(GoldenPot));
    }
    public void getAnswer(){

        Stack<Integer> answer=new Stack<>();
        int i=0,j=PotNum-1;
        while (i < j) {
             int a=BruteForce(i,j);
             int b=BruteForce(i,j-1);
             int c=BruteForce(i+1,j);
            if((GoldenPot[j]-b)!=(GoldenPot[i]- c)) {
                //往左走
                if (a == GoldenPot[j] - b) {
                    answer.push(GoldenPot[j]);
                    j--;
                } //往右走
                else if (a == GoldenPot[i] - c) {
                    answer.push(GoldenPot[i]);
                    i++;
                }
            }else{
                if ( GoldenPot[j]>GoldenPot[i] ) {
                    answer.push(GoldenPot[j]);
                    j--;
                } //往右走
                else {
                    answer.push(GoldenPot[i]);
                    i++;
                }
            }
        }
        answer.push(GoldenPot[i]);
        System.out.println("BruteForceAnswer:"+answer);

        //dp
         answer.clear();

         i=0;j=PotNum-1;
        while (i < j) {
            if((GoldenPot[j]-dp[i][j - 1])!=(GoldenPot[i]- dp[i + 1][j])) {
                //往左走
                if (dp[i][j] == GoldenPot[j] - dp[i][j - 1]) {
                    answer.push(GoldenPot[j]);
                    j--;
                } //往右走
                else if (dp[i][j] == GoldenPot[i] - dp[i + 1][j]) {
                    answer.push(GoldenPot[i]);
                    i++;
                }
            }else{
                if ( GoldenPot[j]>GoldenPot[i] ) {
                    answer.push(GoldenPot[j]);
                    j--;
                } //往右走
                else {
                    answer.push(GoldenPot[i]);
                    i++;
                }
            }
        }
        answer.push(GoldenPot[i]);
        System.out.println("DPAnswer:"+answer);
    }

    public  int BruteForce(int i,int j){
        if(i==j)
            return GoldenPot[i];
        else {

            return Math.max(GoldenPot[i]-BruteForce(i+1,j), GoldenPot[j]-BruteForce(i,j-1));
        }
    }

    public  int dp(){
        dp=new int[PotNum][PotNum];
        //初始化
        for (int i=0;i<PotNum;i++){
            dp[i][i]=GoldenPot[i];
        }
        for(int len=1;len<PotNum;len++){
            for (int start=0;start<PotNum-1;start++){
                int end=start+len;
                if(end>=PotNum) break;
                dp[start][end]=Math.max(GoldenPot[start]-dp[start+1][end], GoldenPot[end]-dp[start][end-1]);
            }
        }
        return dp[0][PotNum-1];
    }
    public  int One_dp(){
        dpOne=new int[PotNum];
        for (int i=0;i<PotNum;i++){
            dpOne[i]=GoldenPot[i];
        }
        for(int len=1;len<PotNum;len++){
            for (int start=0;start<PotNum-1;start++){
                int end=start+len;
                if(end>=PotNum) break;
                dpOne[start]=Math.max(GoldenPot[end]-dpOne[start],GoldenPot[start]-dpOne[start+1]);
            }
        }
        return dpOne[0];
    }
    //有点慢
    public  int Map_dp(){
        //初始化
        for (int i=0;i<PotNum;i++){
            String str=i+"+"+i;
            dpMap.put(str,GoldenPot[i]);
        }
        for(int len=1;len<PotNum;len++){
            for (int start=0;start<PotNum-1;start++){
                int end=start+len;
                if(end>=PotNum) break;
                String s=start+"+"+end;
                String sl=(start+1)+"+"+end;
                String sr=start+"+"+(end-1);
                int max=Math.max(GoldenPot[start]-dpMap.get(sl), GoldenPot[end]-dpMap.get(sr));
                dpMap.put(s,max);
                dpMap.remove(sr);
            }
        }
        String f="0+"+(PotNum-1);
        return dpMap.get(f);
    }
    public void backtracking(int left,int right)
    {

        int len=right-left+1;
        if(len<=0)
        {
            if(sumA>=sumB) {
                System.out.println("bruteAnswer:"+Answer);
                  // System.out.println("Answer:" + Answer + "sumA:" + sumA + "sumB:" + sumB);
               // System.out.println("bruteAnswer:" + bruteAnswer + "sumA:" + sumA + "sumB:" + sumB);
            }
            return;
        }

         //Answer
        if(len%2==0){
            //选左边
            sumA+=GoldenPot[left];
            Answer.push(GoldenPot[left]);


            backtracking(left+1,right);
            sumA-=GoldenPot[left];
            Answer.pop();

            //选右边
            sumA+=GoldenPot[right];
            Answer.push(GoldenPot[right]);

            backtracking(left,right-1);
            sumA-=GoldenPot[right];
            Answer.pop();
        }else if(len%2==1&&len!=1) {//Answer

            //选左边
            sumB+=GoldenPot[left];
            Answer.push(GoldenPot[left]);


            backtracking(left+1,right);
            sumB-=GoldenPot[left];
           Answer.pop();

            //选右边
            sumB+=GoldenPot[right];
            Answer.push(GoldenPot[right]);

            backtracking(left,right-1);
            sumB-=GoldenPot[right];
            Answer.pop();
        }else if(len==1) {
        sumB += GoldenPot[left];
        Answer.push(GoldenPot[left]);

        backtracking(left + 1, right);
        sumB -= GoldenPot[left];
        Answer.pop();
    }


    }
}
