package BackTrack;

import java.io.*;
import java.util.Random;

import static java.lang.Math.sqrt;

public class WriteData {
    public static int nodeNum=20;
    public static int matrix[][]=new int[nodeNum+1][nodeNum+1];
    public static int degree[]=new int[nodeNum+1];
    public static void main(String[] args) throws IOException {

        String writeFile1="src/BackTrack/data/len_4.col";
        WriteData.readFile(writeFile1);
    }
    public static void readFile( String writeFile1) throws IOException {

        FileWriter fw1 =new FileWriter(writeFile1);//创建一个读取流对象和文件相关联

        BufferedWriter writer1;
        try {

            writer1 = new BufferedWriter(fw1);
            int line=(int)(nodeNum*1.3);
            Random rd = new Random();
            while (line>0) {
                int index1 = rd.nextInt(nodeNum) + 1;
                int index2 = rd.nextInt(nodeNum) + 1;
                if(index1==index2||matrix[index1][index2]==1||degree[index1]>sqrt(nodeNum)+1||degree[index2]>sqrt(nodeNum)+1)continue;
                matrix[index1][index2]=1;
                matrix[index2][index1]=1;
                degree[index1]++;degree[index2]++;
                String e = "e " + index1 + " " + index2;
                line--;
                writer1.write(e);
                writer1.newLine();
                writer1.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}