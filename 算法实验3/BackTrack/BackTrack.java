package BackTrack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public  class BackTrack {
    int nodeNum;
    int colorNum;

    int count;
    Area alreadyArea[];
    Area areas[];
    int usedColor[];
    int canDrawColorNum[][];
    int hui=0;
    int degree[];
    Set<Integer> alreadyAreaId=new HashSet<>();
    static class StopMsgException extends RuntimeException {

    }
    BackTrack(int n,int m){
        nodeNum=n;colorNum=m;
        areas=new Area[nodeNum];
        degree=new int[nodeNum+1];
       alreadyArea=new Area[nodeNum];
        usedColor=new int[colorNum+1];
       canDrawColorNum=new int[nodeNum+1][colorNum+1];
        for(int i=0;i<nodeNum;i++){
            areas[i]=new Area(i+1);
        }

    }
    public static  void main(String[] args){
//       BackTrack backTrack=new BackTrack(9,4);
//       backTrack.readFile("src/BackTrack/data/le9_4.col");

//        BackTrack backTrack=new BackTrack(450,5);
//        backTrack.readFile("src/BackTrack/data/le450_5a.col");

        BackTrack backTrack=new BackTrack(450,15);
        backTrack.readFile("src/BackTrack/data/le450_15b.col");

//        BackTrack backTrack=new BackTrack(450,25);
//        backTrack.readFile("src/BackTrack/data/le450_25a.col");

//        BackTrack backTrack=new BackTrack(20,4);
//        backTrack.readFile("src/BackTrack/data/len_4.col");

        long startTime=System.currentTimeMillis();
        try  {
            backTrack.backtracking(0);
        }  catch  (StopMsgException e) {

        }

        long endTime=System.currentTimeMillis();
       System.out.println(endTime-startTime+"ms");
       System.out.println(backTrack.hui);
       System.out.println(backTrack.count);
    }
    public int getFactorial(int colorNum){
        int factorial=1;
        for (int i=2;i<=colorNum;i++){
            factorial*=i;
        }
        return factorial;
    }
    public void getSortAreas(int step){
        int max=0;
        int maxStep=0;
        for(int i=step;i<nodeNum;i++){

            int sum=0;
            for(int j=1;j<=colorNum;j++){
                sum+=canDrawColorNum[areas[i].id][j]>0?1:0;
            }
            if(sum>max){
                max=sum;
                maxStep=i;
            }
//            if(sum==max&&degree[areas[i].id]>degree[areas[maxStep].id]){
//                maxStep=i;
//            }
            if(sum==max&&areas[i].nearAreaId.size()>areas[maxStep].nearAreaId.size()){
                maxStep=i;
            }

        }
        if(step!=maxStep) {
            Area a = areas[step];
            areas[step] = areas[maxStep];
            areas[maxStep] = a;
        }
    }
    public void setColor(int step,int color){
        areas[step].color = color;
       alreadyArea[step] = areas[step];
        alreadyAreaId.add(areas[step].id);
        usedColor[color]++;

        for (int nearId : areas[step].nearAreaId) {
            degree[nearId]--;
            canDrawColorNum[nearId][color]++;

        }
    }

    public void deleteColor(int step,int color) {
        hui++;
        areas[step].color = 0;
      alreadyArea[step] = null;
        alreadyAreaId.remove(areas[step].id);
       usedColor[color]--;
        for (int nearId : areas[step].nearAreaId) {
            degree[nearId]++;
            canDrawColorNum[nearId][color]--;

        }
    }

    public void backtracking(int step)
    {
        if(step>=nodeNum)
        {
         for (int i=0;i<nodeNum;i++){
             if(!checkNode(i,alreadyArea[i])){
                 System.out.println("false");
             }
         }

           count++;
            if  (count ==  1 ) {
                System.out.println(Arrays.toString(alreadyArea));
                // 跳出
                throw  new  StopMsgException();
            }
            return;
        }
         getSortAreas(step);

            int id=areas[step].id;
            for (int color = 1; color <= colorNum; color++) {
                if(canDrawColorNum[id][color]>0) continue;
                setColor(step,color);
//                if (checkNode(step, areas[step]))//如果该节点填这个颜色符合条件
                if(true)
                {//检查该节点相邻的节点能否填色
                    if(usedColor[color]==1){
                        for (int i=1;i<=colorNum;i++){
                            if(usedColor[i]==0)
                            canDrawColorNum[id][i]++;
                        }
                    }
                        backtracking(step + 1);//递归
                }

                deleteColor(step,color);
            }

    }
    public boolean checkNode(int step,Area nowArea){
        for(int beforeStep=0;beforeStep<step;beforeStep++)
        {
            int id=alreadyArea[beforeStep].id;
            if(nowArea.nearAreaId.contains(id)
                    &&nowArea.color==alreadyArea[beforeStep].color)
                return false;
        }
        //检测相邻可填颜色数
//        for (int nearId:nowArea.nearAreaId){
//            if(alreadyAreaId.contains(nearId)) continue;
//            int sum=0;
//            for(int j=1;j<=colorNum;j++){
//                sum+=canDrawColorNum[nearId][j]>0?1:0;
//            }
//            if(sum==colorNum){
//                return false;
//            }
//        }
        return true;
    }
    public  void readFile(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            int line=0;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                line++;
                if(line>=35) {
                    getAreas(tempString);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public void getAreas(String tempString){

        String[] strs = tempString.split("\\s+");
        // System.out.println(strs[1]);
        int one=Integer.parseInt(strs[1]);
        int another=Integer.parseInt(strs[2]);
        areas[one-1].nearAreaId.add(another);
        areas[another-1].nearAreaId.add(one);
       degree[one]=areas[one-1].nearAreaId.size();
        degree[another]=areas[another-1].nearAreaId.size();
    }
}
