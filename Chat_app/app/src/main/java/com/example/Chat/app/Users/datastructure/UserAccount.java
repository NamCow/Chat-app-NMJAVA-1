package com.example.Chat.app.Users.datastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.net.Socket;

public class UserAccount {
    private int userID;
    private String userName;
    private String password;
    private String email;
    private String address;
    private String fullName;
    private String birthday;
    private String gender;
    private Timestamp createdAt;
    private String status;
    private String role;
    private int lock;


    public Socket clienSocket;
    public PrintWriter pw;
    public BufferedReader br;
    // Constructor với tất cả các trường
    public UserAccount( String userName, String password, String email, String address, String fullName,String birthday, String gender, Timestamp createdAt, String status, String role, int lock) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.address = address;
        this.fullName = fullName;
        this.birthday = birthday;
        this.gender = gender;
        this.createdAt = createdAt;
        this.status = status;
        this.role = role;
        this.lock = lock;
    }
    public UserAccount( int userid,String userName, String password, String email, String address, String fullName,String birthday, String gender, Timestamp createdAt, String status, String role, int lock) {
        this.userID = userid;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.address = address;
        this.fullName = fullName;
        this.birthday = birthday;
        this.gender = gender;
        this.createdAt = createdAt;
        this.status = status;
        this.role = role;
        this.lock = lock;
    }

    // Constructor mặc định
    public UserAccount() {
    }

    // Getters và setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int isLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }
    public void sendPacket(String packet){
        pw.println(packet);
    }
    public String receivePacket() throws IOException{
        String packet = br.readLine();
        return packet;
    }


    public Socket getClienSocket() {
        return this.clienSocket;
    }

    public void setClienSocket(Socket clienSocket) {
        this.clienSocket = clienSocket;
    }

    public PrintWriter getPw() {
        return this.pw;
    }

    public void setPw(PrintWriter pw) {
        this.pw = pw;
    }


    public BufferedReader getBr() {
        return this.br;
    }

    public void setBr(BufferedReader br) {
        this.br = br;
    }
    public String toString() {
        return String.format(
            "UserAccount {userID=%d, userName='%s', password='%s', email='%s', address='%s', fullName='%s', birthday='%s', gender='%s', createdAt=%s, status='%s', role='%s', lock=%d}",
            this.userID,
            this.userName,
            this.password,
            this.email,
            this.address,
            this.fullName,
            this.birthday,
            this.gender,
            this.createdAt != null ? this.createdAt.toString() : "null",
            this.status,
            this.role,
            this.lock
        );
    }
}
