package com.fdt.appserver.bean;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "USERS")
public class User implements Cloneable, Serializable {
    @Id
    @Column(name = "USER_ID")
    private String id;
    @Column(name = "USER_NAME")
    private String name;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "SALT")
    private String salt;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "CREATED")
    private Date created;
    @Column(name = "LAST_LOGIN")
    private Date lastLogin;
    @Column(name = "BEHALF")
    private String behalf;
    @Column(name = "ACCOUNT_TYPE")
    private int accountType;
    @Column(name = "ACCOUNT")
    private String accountId;

    public User() {
    }

    public User(String id, String password) {
        this();
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getBehalf() {
        return behalf;
    }

    public void setBehalf(String behalf) {
        this.behalf = behalf;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public User clone() throws CloneNotSupportedException {
        return (User) super.clone();
    }
}