package com.spacex.philosopher.script;

public class DistributedLockScript {
    public static String getReleaseDistributedLockScript() {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        return script;
    }
}
