package com.zhihuta.xiaota.bean.response;

import com.zhihuta.xiaota.bean.basic.ProjectData;
import com.zhihuta.xiaota.bean.basic.Wires;

import java.util.List;

public class GetProjectsResponse extends PageOffsetResponse
{
    public List<ProjectData> project_list;
}