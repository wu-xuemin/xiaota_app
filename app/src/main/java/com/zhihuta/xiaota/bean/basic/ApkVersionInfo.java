package com.zhihuta.xiaota.bean.basic;

import java.util.Date;

public class ApkVersionInfo {
    /**
     * id
     */
    private Integer id;

    /**
     * app名称
     */
    private String apkName;

    /**
     * app的版本号
     */
    private String apkVersion;

    /**
     * 上传时间
     */
    private Date createTime;

    /**
     * 是否正在使用
     */
    private Boolean isDeleted;

    private Date updateTime;

    /**
     * app当前版本升级日志
     */
    private String apkUpgradeLog;

    /**
     * 获取id
     * @return id - id

     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置id
     *
     * @param id id

     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取app名称
     *
     * @return apk_name - app名称
     */
    public String getApkName() {
        return apkName;
    }

    /**
     * 设置app名称
     *
     * @param apkName app名称
     */
    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    /**
     * 获取app的版本号
     *
     * @return apk_version - app的版本号
     */
    public String getApkVersion() {
        return apkVersion;
    }

    /**
     * 设置app的版本号
     *
     * @param apkVersion app的版本号
     */
    public void setApkVersion(String apkVersion) {
        this.apkVersion = apkVersion;
    }

    /**
     * 获取上传时间
     *
     * @return create_time - 上传时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置上传时间
     *
     * @param createTime 上传时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取是否正在使用
     *
     * @return is_deleted - 是否正在使用
     */
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    /**
     * 设置是否正在使用
     *
     * @param isDeleted 是否正在使用
     */
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取app当前版本升级日志
     *
     * @return apk_upgrade_log - app当前版本升级日志
     */
    public String getApkUpgradeLog() {
        return apkUpgradeLog;
    }

    /**
     * 设置app当前版本升级日志
     *
     * @param apkUpgradeLog app当前版本升级日志
     */
    public void setApkUpgradeLog(String apkUpgradeLog) {
        this.apkUpgradeLog = apkUpgradeLog;
    }
}