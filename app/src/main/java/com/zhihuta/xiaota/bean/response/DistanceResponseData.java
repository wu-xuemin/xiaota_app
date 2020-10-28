package com.zhihuta.xiaota.bean.response;

import com.zhihuta.xiaota.bean.basic.DistanceData;

import java.util.ArrayList;
import java.util.List;

public class DistanceResponseData {

        private List<DistanceData> distance_qrs = new ArrayList<>();

        public List<DistanceData> getDistance_qrs() {
            return distance_qrs;
        }

}
