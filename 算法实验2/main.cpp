#include <iostream>
#include <cmath>
#include <cstdlib>
#include <ctime>
#include <algorithm>
#include <vector>
#define MAX 99999
#pragma GCC optimize(3,"Ofast","inline")
using namespace std;
class Point{
public:
    double x,y;
    Point(){x=0;y=0;}
    void setXY(double x_value,double y_value){x=x_value;y=y_value;}
    void print(){
        cout<<"("<<x<<","<<y<<")"<<endl;
    }
};
bool cmp(Point a,Point b){
    if(a.x!=b.x) return a.x<b.x;
    else if(a.y!=b.y) return  a.y<b.y;
    else return 0;
}
vector<Point> getPointArray(int N){
    vector<Point> Points;
    srand((unsigned)(time(0)));

    for (int i = 0; i < N; ++i) {
        Point p;
        double x=(double)(rand()) / RAND_MAX * MAX;
        double y=(double)(rand()) / RAND_MAX * MAX;
        p.setXY(x,y);
        Points.push_back(p);
    }

    sort(Points.begin(),Points.end(),cmp);
//    for (int i = 0; i < N; ++i) {
//        Points[i].print();
//    }
    return Points;
}
double Distance(Point a,Point b){
    double dis;
    dis=sqrt((a.x-b.x)*(a.x-b.x)+ (a.y-b.y)*(a.y-b.y));
    return dis;
}
void BruteForce(vector<Point>P,int N){
    double minDist=MAX;
    int index1,index2;
    for (int i = 0; i < N-1; ++i) {
        for (int j = i+1; j < N; ++j) {
            double distance= Distance(P[i],P[j]);
            if(distance<minDist){
                minDist=distance;
                index1=i;
                index2=j;
                if(distance==0)break;
            }
        }
    }
    //cout<<"("<<index1<<","<<index2<<") "<<minDist<<endl;
}
bool cmp_x(const Point & A, const Point & B)  // 比较x坐标
{
    return A.x < B.x;
}

bool cmp_y(const Point & A, const Point & B)  // 比较y坐标
{
    return A.y < B.y;
}
double merge(vector<Point> & points, double dis, int mid)
{
    vector<Point> left, right;
    for (int i = 0; i < points.size(); ++i)  // 搜集左右两边符合条件的点
    {
        if (points[i].x - points[mid].x <= 0 && points[i].x - points[mid].x > -dis)
            left.push_back(points[i]);
        else if (points[i].x - points[mid].x > 0 && points[i].x - points[mid].x < dis)
            right.push_back(points[i]);
    }
    sort(right.begin(), right.end(), cmp_y);
//    for (int i = 0, index; i < left.size(); ++i)  // 遍历左边的点集合，与右边符合条件的计算距离
//    {
//        for (index = 0; index < right.size() && left[i].y < right[index].y; ++index);
//        for (int j = 0; j < 7 && index + j < right.size(); ++j)  // 遍历右边坐标上界的6个点
//        {
//            if (Distance(left[i], right[j + index]) < dis)
//                dis = Distance(left[i], right[j + index]);
//        }
//    }
    for (int i = 0; i < left.size(); ++i)  // 遍历左边的点集合，与右边符合条件的计算距离
    {
        for (int j = 0; j < right.size(); ++j)
        {
            if (Distance(left[i], right[j ]) < dis)
                dis = Distance(left[i], right[j]);
        }
    }
    return dis;
}


double Dichotomy(vector<Point> & points)
{
    if (points.size() == 2) return Distance(points[0], points[1]);  // 两个点
    if (points.size() == 3) return min(Distance(points[0], points[1]), min(Distance(points[0], points[2]),
                                                                           Distance(points[1], points[2])));  // 三个点
    int mid = (points.size() >> 1) - 1;
    double d1, d2, d;
    vector<Point> left(mid + 1), right(points.size() - mid - 1);
    copy(points.begin(), points.begin() + mid + 1, left.begin());  // 左边区域点集合
    copy(points.begin() + mid + 1, points.end(), right.begin());  // 右边区域点集合
    d1 = Dichotomy(left);
    d2 = Dichotomy(right);
    d = min(d1, d2);
    return merge(points, d, mid);
}

int main()
{   int t=12;
    while (t--) {
        int N = 500;
        clock_t start, end;
        vector<Point> P = getPointArray(N);

        start = clock();
        // BruteForce(P,N);
        // cout<<Dichotomy(P)<<endl;
        Dichotomy(P);
        end = clock();
        cout <<"分治："<< (end - start) << "ms" << endl;
        start = clock();
        BruteForce(P,N);

        end = clock();
        cout << "蛮力："<<(end - start) << "ms" << endl;
    }
    return 0;
}
