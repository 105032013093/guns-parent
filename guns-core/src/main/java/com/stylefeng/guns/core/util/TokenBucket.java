package com.stylefeng.guns.core.util;

public class TokenBucket {
    private int bucketNums = 100;   // 桶的容量
    private int rate = 1;           // 流入速度
    private int nowTokens;          // 当前令牌数
    private long timestamp = getNowTime();         // 时间

    private long getNowTime() {
        return System.currentTimeMillis();
    }

    private int min(int tokens) {
        if (bucketNums > tokens) {
            return tokens;
        } else {
            return bucketNums;
        }
    }

    public boolean getToken() {
        // 记录来拿令牌时间
        long nowTime = getNowTime();
        // 判断该有多少令牌
        nowTokens = nowTokens + (int) ((nowTime - timestamp) * rate);
        // 比较令牌数和桶大小数
        nowTokens = min(nowTokens);

        timestamp = nowTime;
        System.out.println("现在token有：" + nowTokens);
        if (nowTokens >= 1) {
            nowTokens--;
            return true;

        } else {
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TokenBucket tokenBucket = new TokenBucket();
        for (int i = 0; i < 100; i++) {
            if (i == 10) {
                Thread.sleep(500);
            }
            System.out.println("第" + i + "请求结果=" + tokenBucket.getToken());
        }
    }
}
