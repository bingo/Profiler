package com.bingo;

/**
 * Created by bingo on 14/12/7.
 */
public class Idle {
    public static void main(String[] args) {
        try {
            while(true) {
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}