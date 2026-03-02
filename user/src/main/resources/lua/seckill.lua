---@diagnostic disable: undefined-globa
-- KEYS[1] = seckill:stock:{activityId}
-- KEYS[2] = seckill:bought:{activityId}
-- ARGV[1] = userId

if redis.call('SISMEMBER', KEYS[2],ARGV[1]) == 1 then
    return -1
end

local stock = redis.call('DECR', KEYS[1])
if stock < 0 then
    redis.call('INCR', KEYS[1])
    return 0
end
redis.call('SADD', KEYS[2], ARGV[1])
return 1