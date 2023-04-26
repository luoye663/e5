# E5续订程序Docker-Compose版本

使用教程：
- `git clone`拉取本项目的地址
- 安装好docker环境
- 配置环境变量  
    - 准备好自己的服务器的域名，证书！否则只能本地运行了
    - 然后配置nginx，把`https://<your-domain.com>`映射到4200端口
    - 端口不满意自己改docker-compose文件，我就不多说了
    - 由于微软的回调https是强制要求的，所以必须要有域名、整数，把443的https访问代理到本机的4200端口，具体有很多教程的或者问GPT
    - 前往Github增加自己的OAUTH APP，配置回调为`https://<your-domain.com>/auth2/receive`，记录client secret什么的信息
    - 在config.env里面设置自己的Github OAUTH APP的clientID和client secret
    - OUTLOOK_REPLYURL配置为：`https://<your-domain.com>/outlook/auth2/%s/receive`
    - 搜索`[[[!!!changeIt!!!]]]`相关的，确保都已经被修改好了！然后执行下一步
- `docker compose up -d` 启动集群
- 如果觉得不安全的，默认密码想要改的，搜索默认的密码关键字，修改即可。或者关闭相关的数据库服务器端口。
- 使用本项目因为自行的配置失误或者其他原因造成的损失，由使用者自行负责。使用本项目代表您同意此条目。



#### 原作者介绍
此项目为该网址的源代码

- (后端) https://e5.qyi.io/

- (前端) https://github.com/luoye663/e5-html


#### 使用教程
https://qyi.io/archives/687.html

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

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=luoye663/e5&type=Date)](https://star-history.com/#luoye663/e5&Date)

## 鸣谢

> [IntelliJ IDEA](https://www.jetbrains.com/zh-cn/idea/buy/#personal?billing=yearly) 是一个在各个方面都最大程度地提高开发人员的生产力的 IDE，适用于 JVM 平台语言。

特别感谢 [JetBrains](https://www.jetbrains.com/?from=) 为开源项目提供免费的 [IntelliJ IDEA](https://www.jetbrains.com/idea/?from=) 等 IDE 的授权  
[<img src=".github/jetbrains-variant-3.png" width="200"/>](https://www.jetbrains.com/)
