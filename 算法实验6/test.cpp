#include<bits/stdc++.h>
#pragma GCC optimize(2)
using namespace std;

const int N = 1e6 + 10, M = 2e7 + 10, INF = 1e8;

int boss, paper, judge, need;       //评审,论文,评阅数量,需要评审数

int S = 0, T = 100001;
int paper_pt = S + 1, boss_pt;
int n, res;

//邻接表
int h[N], e[M], f[M], ne[M], idx;   //表头，边，流量，邻边，指针
int q[N], d[N], pre[N], cur[N];     //队列，层数，前向边，当前弧
bool st[N];                         //访问状态

//往流网络中添加边，EK, Dinic使用
void add(int a, int b, int c)
{
    e[idx] = b, f[idx] = c, ne[idx] = h[a], h[a] = idx ++ ; //正向边
    e[idx] = a, f[idx] = 0, ne[idx] = h[b], h[b] = idx ++ ; //反向边
}

//bfs寻找增广路径
bool EK_bfs()
{
    int hh = 0, tt = 0;
    memset(st, false, sizeof st);
    q[0] = S, st[S] = true, d[S] = INF;
    while(hh <= tt)
    {
        int t = q[hh ++ ];                  //入队
        for(int i = h[t]; ~i; i = ne[i])
        {
            int ver = e[i];
            if(!st[ver] && f[i])            //未被访问且有流量
            {
                st[ver] = true;
                d[ver] = min(d[t], f[i]);   //记录增广路上容量最小值
                pre[ver] = i;               //记录前向边
                if(ver == T) return true;
                q[ ++ tt] = ver;
            }
        }
    }
    return false;
}

void EK()
{
    int r = 0;
    while(EK_bfs())
    {
        r += d[T];
        for(int i = T; i != S; i = e[pre[i] ^ 1])       //增广
            f[pre[i]] -= d[T], f[pre[i] ^ 1] += d[T];   
    }
    res = r;
}

//bfs寻找增广路径,并标记层数
bool Dinic_bfs()
{
    int hh = 0, tt = 0;
    memset(d, -1, sizeof d);    //初始化层数
    q[0] = S, d[S] = 0, cur[S] = h[S];
    while (hh <= tt)
    {
        int t = q[hh ++ ];
        for (int i = h[t]; ~i; i = ne[i])
        {
            int ver = e[i];
            if (d[ver] == -1 && f[i])
            {
                d[ver] = d[t] + 1;          //层数
                cur[ver] = h[ver];          //记录当前弧
                if (ver == T)  return true; //找到增广路
                q[ ++ tt] = ver;
            }
        }
    }
    return false;
}

//dfs多路增广
int find(int u, int limit)
{
    if (u == T) return limit;
    int flow = 0;
    for (int i = cur[u]; ~i && flow < limit; i = ne[i])
    {
        cur[u] = i;                 // 当前弧优化
        int ver = e[i];
        if (d[ver] == d[u] + 1 && f[i])
        {
            int t = find(ver, min(f[i], limit - flow));
            if (!t) d[ver] = -1;    //剪枝，去掉增广过的点
            f[i] -= t, f[i ^ 1] += t, flow += t;
        }
    }
    return flow;
}

void Dinic()
{
    int r = 0, flow;
    while (Dinic_bfs()) 
        while ((flow = find(S, INF))) 
            r += flow;

    res = r;
}

//结构体，存储网络中的边
struct qxx
{
    int nex, t, v;
};

qxx edge[M * 2];
int head[N], cnt = 1;

void add_path(int f, int t, int v)
{
    edge[ ++ cnt] = (qxx){head[f], t, v};
    head[f] = cnt;
}

void add_flow(int f, int t, int v)
{
    add_path(f, t, v);
    add_path(t, f, 0);
}

int ht[N], ex[N], gap[N];                   // 高度；超额流, gap优化

bool bfs_init()
{
    memset(ht, 0x3f, sizeof(ht));
    queue<int> q;
    q.push(T), ht[T] = 0;
    while (q.size())                        // 反向 BFS, 遇到没有访问过的结点就入队
    {
        int u = q.front();
        q.pop();
        for (int i = head[u]; i; i = edge[i].nex)
        {
            const int &v = edge[i].t;
            if (edge[i ^ 1].v && ht[v] > ht[u] + 1) ht[v] = ht[u] + 1, q.push(v);
        }
    }
    return ht[S] != INF;                    // 如果图不连通，返回 0
}

struct cmp                                 // 仿函数
{
    bool operator()(int a, int b) const 
    { 
        return ht[a] < ht[b]; 
    }
};                                         

priority_queue<int, vector<int>, cmp> pq;  // 将需要推送的结点以高度高的优先
bool vis[N];                               // 是否在优先队列中

// 尽可能通过能够推送的边推送超额流
int push(int u)
{  
    for (int i = head[u]; i; i = edge[i].nex)
    {
        const int &v = edge[i].t, &w = edge[i].v;
        if (!w || ht[u] != ht[v] + 1) continue;
        int k = min(w, ex[u]);              // 取到剩余容量和超额流的最小值
        ex[u] -= k, ex[v] += k, edge[i].v -= k, edge[i ^ 1].v += k;  // push
        if (v != S && v != T && !vis[v])
            pq.push(v), vis[v] = 1;         // 推送之后，v 必然溢出，则入堆，等待被推送
        if (!ex[u]) return 0;               // 如果已经推送完就返回
    }
    return 1;
}

// 重贴标签（高度）
void relabel(int u)
{  
    ht[u] = INF;
    for (int i = head[u]; i; i = edge[i].nex)
        if (edge[i].v)  ht[u] = min(ht[u], ht[edge[i].t]);

    ++ ht[u];
}

void hlpp()
{                  
    if (!bfs_init()) return;  // 图不连通
    ht[S] = n;
    
    memset(gap, 0, sizeof(gap));
    for (int i = 0; i < n; i ++ )
        if (ht[i] != INF) gap[ht[i]] ++ ;                           // 初始化 gap

    for (int i = head[S]; i; i = edge[i].nex)
    {
        const int v = edge[i].t, w = edge[i].v;                     // 队列初始化
        if (!w) continue;
        ex[S] -= w, ex[v] += w, edge[i].v -= w, edge[i ^ 1].v += w; // 注意取消 w 的引用
        if (v != S && v != T && !vis[v]) pq.push(v), vis[v] = 1;    // 入队
    }

    while (pq.size())
    {
        int u = pq.top();
        pq.pop(), vis[u] = 0;
        while (push(u))
        {   // 仍然溢出
            // 如果 u 结点原来所在的高度没有结点了，相当于出现断层
            if (!--gap[ht[u]])
                for (int i = 1; i <= n; i++)
                    if (i != S && i != T && ht[i] > ht[u] && ht[i] < n + 1) ht[i] = n + 1;
            relabel(u);
            ++ gap[ht[u]];  // 新的高度，更新 gap
        }
    }
    res = ex[T];
}


//创建流网络
void CreateNetWork()
{
    //HLPP使用
    memset(head, 0, sizeof head);
    memset(ex, 0, sizeof ex);
    cnt = 1, res = 0;

    for(int i = 0; i < paper; i ++ )
        add_flow(S, paper_pt + i, need);

    for(int i = 0; i < boss; i ++ )
        add_flow(boss_pt + i, T, judge);

    for(int i =0; i < paper; i ++ )
        for(int j = 0; j < boss; j ++ )
            add_flow(paper_pt + i, boss_pt + j, 1);

    //EK,Dinic使用
    memset(h, -1, sizeof h);
    
    idx = 0;

    for(int i = 0; i < paper; i ++ )
        add(S, paper_pt + i, need);

    for(int i = 0; i < boss; i ++ )
        add(boss_pt + i, T, judge);

    for(int i = 0; i < paper; i ++ )
        for(int j = 0; j < boss; j ++ )
            add(paper_pt + i, boss_pt + j, 1);
}

//初始化参数
void Init(int i)
{
    int bos[5] = {600, 700, 800, 900, 1000};
    int pap[5] = {600, 700, 800, 900, 1000};
    int jud[5] = {2, 4, 6, 8, 10};
    int ned[5] = {2, 4, 6, 8, 10};

    boss = bos[0];
    paper = pap[0];
    judge = jud[0];
    need = ned[0];

    // cout << "评委数量:", cin >> boss;
    // cout << "论文数量:", cin >> paper;
    // cout << "每个评委最多评审的论文数量:", cin >> judge;
    // cout << "每篇论文需要的评审数量:", cin >> need;

    boss_pt = paper_pt + paper;
    T = boss_pt + boss;
    n = paper + boss + 2;

    CreateNetWork();
}

//输出最大流量，判断是否有解
void Slove()
{
    cout << "   Max_flow : " << res;
    if(res < need * paper) cout << "  无解" << endl;
    else cout << "  有解" << endl;
}

int main()
{
    ofstream oFile;
    oFile.open("network.csv",ios::app|ios::out);

    // oFile << "a" << "," << "b" << "," << "n" << "," << "m" << "," << "EK" << "," << "Dinic" << "," << "HLPP" << endl;
    oFile << "a" << "," << "b" << "," << "n" << "," << "m" << "," << "Dinic" << "," << "HLPP" << endl;

    auto start = chrono::steady_clock::now();
    auto end = chrono::steady_clock::now();
    chrono::duration<double, std::micro> elapsed;
    
    for(int i = 0; i < 5; i ++ )
    {
        Init(i);

        oFile << need << "," << judge << "," << boss << "," << paper;

        // start = chrono::steady_clock::now();
        // EK();
        // end = chrono::steady_clock::now();
        // elapsed = end - start;
        
        // cout << "EK:" << endl;
        // Slove();
        
        // printf("    %6.6lf us.\n", elapsed.count());
        // printf("    %6.6lf ms.\n\n", elapsed.count() / 1000);

        // oFile << "," << elapsed.count() / 1000;

        // CreateNetWork();

        start = chrono::steady_clock::now();
        Dinic();
        end = chrono::steady_clock::now();
        elapsed = end - start;

        cout << "Dinic:" << endl;
        Slove();
        printf("    %6.6lf us.\n", elapsed.count());
        printf("    %6.6lf ms.\n\n", elapsed.count() / 1000);

        oFile << "," << elapsed.count() / 1000;

        CreateNetWork();

        start = chrono::steady_clock::now();
        hlpp();
        end = chrono::steady_clock::now();
        elapsed = end - start;

        cout << "HLPP:" << endl;
        Slove();
        printf("    %6.6lf us.\n", elapsed.count());
        printf("    %6.6lf ms.\n\n", elapsed.count() / 1000);

        oFile << "," << elapsed.count() / 1000 << endl;
    }

    oFile.close();

    return 0;
}