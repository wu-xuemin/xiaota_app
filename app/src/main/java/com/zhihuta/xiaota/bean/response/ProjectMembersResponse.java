package com.zhihuta.xiaota.bean.response;

import com.zhihuta.xiaota.User;
import com.zhihuta.xiaota.bean.basic.MemberData;
import com.zhihuta.xiaota.bean.basic.ProjectData;

import java.util.List;

public class ProjectMembersResponse extends PageOffsetResponse
{
    public List<MemberData> accounts;
}