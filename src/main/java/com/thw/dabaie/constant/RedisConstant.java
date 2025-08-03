package com.thw.dabaie.constant;

public interface RedisConstant {

    String USE_SIGN_IN_REDIS_KEY_PREFIX = "user:siginis" ;

    /**
     *
     * @param year
     * @param userId
     * @return 拼接好的 Redis key
     */
   static String getUseSignInRedisKey(int year, long userId){
            return String.format("%s:%s:%s",USE_SIGN_IN_REDIS_KEY_PREFIX,year,userId);
    }
}
