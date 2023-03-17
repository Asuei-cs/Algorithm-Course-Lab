package BackTrack;

import java.util.*;

public  class Area {
    int id;
    int color=0;
    Set<Integer> nearAreaId=new HashSet<>();
    Area(int one){
        id=one;
    }

    @Override
    public String toString() {
        return "{" +
                "id:" + id +
                ", color:" + color +",degree:"+
                nearAreaId.size()+
                '}';
    }
}
