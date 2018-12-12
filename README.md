# philosopher-stone
some basic tools and daily tests on distributed system


## Rate Limiter with redis lua script
```
It's safe in distributed environment
```
RateLimiterController#acquire

## DistributeLock Controller
```
DistributeLock implemented with redis#set NX PX option and simple concurrent test is provided. 
Reddisson also provide a Distribute Lock tool
```


## Lua script
```
lua script path: com/spacex/philosopher/script
```
