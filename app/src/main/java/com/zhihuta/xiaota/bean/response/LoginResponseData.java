package com.zhihuta.xiaota.bean.response;

public class LoginResponseData {
    private String account;
    private int id;
    private String name;
    private int valid;
    private LoginGroup group;
    private LoginRole role;
    private String password;


    public String getFullName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public int getValid() {
        return valid;
    }
    public String getPassword() {
        return password;
    }

    public LoginGroup getGroup() {
        return group;
    }

    public LoginRole getRole() {
        return role;
    }

    public static class LoginGroup {
        private String groupName;
        private String type;
        private int id;

        public String getGroupName() {
            return groupName;
        }

        public int getId() {
            return id;
        }

        public String getType() {
            return type;
        }
    }

    public static class LoginRole {
        private int id;

        public int getId() {
            return id;
        }
    }
}

