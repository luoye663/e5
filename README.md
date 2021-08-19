# E5续订程序
此项目为该网址的源代码(后端) https://e5.qyi.io/
(前端) https://github.com/luoye663/e5-html

#### 计划:
- [ ] 基础数据使用轻量级的h2存储
- [x] 日志使用 TDengine/influxdb 存储
- [ ] 去除mysql依赖
- 
### 2021-08-19
1、取消启动清空redis  
2、将调用日志放到influx，减轻mysql压力
### 2021-07-28
去除rabbitMQ依赖，使用线程池实现更简单(执行时间颗粒度降低)

### 2020-12-20
前端框架更改为Angular,同时支持多应用，每个账户最多支持5个应用。
## 说明
此项目为我的新手练手作，代码辣鸡，目前已经从2020年3月运行到至今。  
如果要自己搭建的话得自己研究下了，不提供技术支持(懒)，记得修改配置文件 
src/main/resources/application-online.properties 
```
user.admin.githubId  - 自己的github id  
数据库配置  
redis配置  
Rabbit配置  
github.client_id  
github.client_secret  
(这两个在https://github.com/settings/developers 申请一个apps就行了。)
```
## 注意事项


## 用到技术或框架
### spring boot  

### Redis
默认用1库，可自行在配置文件修改  

### Mysql
自行导入sql  
没有写清空日志功能，后面加上。  
按道理说日志因该存到MongoDB里，所以？
### Mybatis Plus

### Spring Security
权限配置由于就那么几个，所以就没写到mysql里面。
### log4j2
日志框架

## 鸣谢

> [IntelliJ IDEA](https://www.jetbrains.com/zh-cn/idea/buy/#personal?billing=yearly) 是一个在各个方面都最大程度地提高开发人员的生产力的 IDE，适用于 JVM 平台语言。

特别感谢 [JetBrains](https://www.jetbrains.com/?from=) 为开源项目提供免费的 [IntelliJ IDEA](https://www.jetbrains.com/idea/?from=) 等 IDE 的授权  
[<img src=".github/jetbrains-variant-3.png" width="200"/>](https://www.jetbrains.com/)
