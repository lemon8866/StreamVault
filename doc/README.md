```
请勿滥用，本项目仅用于学习和测试！请勿滥用.
请勿滥用，本项目仅用于学习和测试！请勿滥用.
```

### 更新预发布提示
```
老用户更新容器时 请务必查看更新记录
请注意 从下一个大版本起 将优化以下两个目录
/app/resources/video
/app/resources/cover

优化成
/app/resources/{platform}/{yyyy}/{MM}/{name}/{name}.mp4
/app/resources/{platform}/{yyyy}/{MM}/{name}/{name}.jpg

也就是说将 图片和视频 放在一个文件夹下 方便手工管理




没有wallpaper需求不要使用steam分支 

旧版本目录路径最后版本号为0.0.3 

dev steam分支 已合并到lastest分支

steam分支下载wallpaper 请参照部署文档或者steam分支介绍
```
### 测试方式
```
下载视频 各一个  下载方式 Aria2和http 内建各一次
下载收藏夹/作品(大小100个以内) 各一个  下载方式 Aria2和http 内建各一次

steam 分支
下载壁纸一次
```

### docker cli
#### 旧版本 0.0.3
```
docker run --name spirit -d -p 28081:28081 -v /home/spirit/log:/app/log -v /home/spirit/video:/app/resources/video -v /home/spirit/cover:/app/resources/cover -v /home/spirit/db:/app/db -v /home/spirit/tmp:/tmp qingfeng2336/spirit:latest
```
#### 新版本(dev,steam) 
```
docker run --name spirit -d -p 28081:28081 -v /home/spirit/log:/app/log -v /home/spirit:/app/resources -v /home/spirit/db:/app/db -v /home/spirit/tmp:/tmp qingfeng2336/spirit:latest
```

目前latest 分支版本还是0.0.3
如果想从旧版本 升级到最新版的话  不要取消video  和cover 目录 

0.0.3 版本会存在一些bug 会划分到latest 分支进行修复 

0.0.3 已知遗留问题 由于未做源码分支管理 故不再修复
当Aria2下载目录与本镜像挂载目录不一致时 下载B站视频需要ffmpeg进行合并的 会造成合并失败
当B站视频下载时候 单个视频会出现清晰度选择错误 造成无法下载

### steamn分支介绍

[部署方式](https://github.com/lemon8866/spirit/blob/main/doc/deployment.md "查看部署方式中 steam分支相关内容")

### 更新记录

```
修复抖音下载问题 如需使用抖音必须填写登录后的cookie 否则无法下载.移除远程服务器生成Xbogus
如果不填写COOKIE 单个视频将使用htmlclient方式获视频信息,然后就会有内存占用高的问题
所以建议填写Cookie 使用接口获取视频信息
```



### 抖音解析接口注释(已废弃)
```
  docker dev 分支 新增java 生成Xbogus  不再需要调用node的接口  后续稳定后会合并到latest分支  默认情况下 会先本地生成
  本地生成无效是会在调用api类型  不过应该不会 所以基本上只会走本地调用 所以 解析地址和qingfeng2336/spirit-assist-node无需在关注
```

```
   vercel函数部署的接口 由于所在服务器地址问题可自行搭建 并在系统配置中修改解析地址http://ip:port
```
```
   docker run --name spirit-assist -d -p 53123:80 qingfeng2336/spirit-assist-node:latest
```
### 版本问题

```
  后续没什么问题的话基本上也是收集问题  一个月一更新了 无法解析 解析异常 这种紧急问题会及时修复
```

### 目前应该是一个小的稳定版本了
```
   20230511一支持哔哩收藏夹下载 同时优化了系统日志 方便排错  优化了提交状态

   有点前言要说 本项目目前处于一天一更的状态,如果使用的话 尽可能的每天都看一下docker状态
   作为一个解析类项目 我觉得下载任务还是有必要完全交给下载器去做  所以在最新的版本中 Aria2也支持Bili的下载
   最新版本中已经移除Bili对htmlunit的依赖 所以只有解析抖音的时候才会增加内存开销
   20230408最新版本中 dy解析方式 已变更为优先API解析 API解析失败是才会调用htmlunit
   为了不给docker中在加一个node环境   API 为 nodejs  vercel函数部署
   后续会考虑出一个 docker 中自带node 并部署解析接口API
   有问题请提交issues
```
### 为啥要做这个

```
啊  我喜欢的抖音视频怎么被下架了！怎么失效了！可恶  手机内存又满了！
```

### 简介

```
一个视频下载汇聚类功能  
将视频下载地址推送给 应用目前已经支持的下载器 目前支持http 及外置Aria2 
并建立视频资源缓存 后台可进行管理查询查看等 
后期预计增加视频墙功能和APP端播放视频功能
```

### 有浏览器书签一键推送需求的请docker  latest分支已经合并
```
不要在使用cors 分支了 请使用latest分支  已获取最新的版本体验 
浏览器一键提交  请新建浏览器书签  书签内容为 /app/app.js 里的内容
请注意修改app.js 中第二行的ip,端口,及token 

由于目前基本上所有站点都已经是https 如果是本应用单容器的话是http的协议
需要设置浏览器对应的站点权限为允许不安全选项
具体可参照
https://blog.csdn.net/qq_17627195/article/details/129203873
后续会开发浏览器扩展解决这个问题


请注意 谷歌扩展已经初期写完了  具体指示通过了简单的测试
请通过浏览器加载已解压的扩展 /app/extend

扩展安装后的效果
```
![微信截图_20230425155144.png](https://s2.loli.net/2023/04/25/8gOtXFIYZCTVDp5.png)


### 技术框架

```
spring boot 2.7.10
spring boot jpa
sqlite
htmlunit等
```

## docker hub

[Docker](https://hub.docker.com/r/qingfeng2336/spirit)

### github

[GitHub - Confession1995/spirit: spirit](https://github.com/Confession1995/spirit)

### 功能点

- [x] 配置下载器

- [x] HTTP下载

- [x] 推送Aria2

- [x] 多用户管理

- [x] 视频资源缓存查看

- [ ] 视频墙

- [ ] 视频解析

- [x]           抖音

- [x]           哔哩

- [x] APP或小程序播放视频

- [ ] 其他功能
### 目前已开始计划的功能
- [x] 仅通过链接判断视频来源
- [ ] 增加个人收藏一键下载功能    后台模块
- [ ] 批量提交视频链接     
- [x] APP或小程序播放已缓存的视频列表
- [x] 哔哩平台新增大会员标记提升缓存画质问题
- [ ] 后台新增视频墙功能
- [x] 强化视频解析成功状态
- [x] 新增哔哩扫码获取Cookie功能
### 预计目标实现及支持平台

```
    支持平台AMD64及ARM
    可解析平台
        抖音
        B站
        待总结
```

### 部署

##### 手动部署

```
待后续完善  可在任意存在java1.8以上环境部署
```

##### docker部署

###### docker cli

```
docker run --name spirit -d -p 28081:28081 -v /home/spirit/log:/app/log -v /home/spirit/video:/app/resources/video -v /home/spirit/cover:/app/resources/cover -v /home/spirit/db:/app/db -v /home/spirit/tmp:/tmp qingfeng2336/spirit:latest
```

###### 手动方式

    

```
docker 部署下 容器端口 为28081
/app/resources/video       请注意此处  挂载目录必须与系统配置用推送给Aria2的下载目录一致   
                           如果不使用Aria2 只使用内置http 下载器  就正常挂载此目录
/app/resources/cover
/app/db
/app/log
/tmp
```

### 后台默认帐号密码

```
后台地址是 http://ip:port/admin/login
默认帐号为admin 默认密码为123456
```

### 手机配套端

```
    安卓用户可以有两种选择 一个是我打包好的apk 一个是自己发布一个小程序
    apk
      https://github.com/Confession1995/spirit/blob/main/app/release/apk/apk.apk

    apk 请注意 这个是升级版 支持查看视频的 如果没有手机查看视频的需求 直接下上边那一个就可以了
     https://github.com/Confession1995/spirit/blob/main/app/release/apk/apk_update_video_play.apk
    
    ios用户 仅有一种选择是自己发布一个小程序
    
    小程序基于uniapp开发  直接发布一个体验版本的小程序即可  不要推正式版
    并打开小程序开发模式  跳过域名校验  即可使用
```

###### 小程序界面或APP界面

服务器地址 服务器端口 服务器token 仅需要填写一次  填写后点击保存服务器

小程序或者APP监听剪贴板 会自动复制到第一个大方框里

点击推送 将分享链接推送给后台 将进入排队 进行处理  设置了线程1 所以会排队

<img src="https://s2.loli.net/2023/04/19/GlmrVTWEe8AyYR2.jpg" title="" alt="微信图片_20230419093629.jpg" width="251">

#### 关于API调用

```
接口地址为 http://ip:port/api/processingVideos
参数为
     token   自己后台设置的
     video   链接或分享口令

请求类型 http post
```


![1234.png](https://s2.loli.net/2023/04/25/9vWbFi48cG73XQk.png)
### 意见反馈
```
 个人收藏批量下载 已完成
 ```

### 更新方向

```
新增其他平台 优化整体后台 完善整体系统功能
```

## 参考资料

https://github.com/SocialSisterYi/bilibili-API-collect

https://toscode.gitee.com/zong_zh/parsing-tiktok-video

https://github.com/Johnserf-Seed/TikTokDownload


### 感谢
 [JetBrains](https://jb.gg/OpenSourceSupport/?from=https://github.com/lemon8866/spirit) 提供 [Licenses for Open Source Development](https://www.jetbrains.com/community/opensource/#support)

[![JetBrains](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)](https://www.jetbrains.com/?from=https://github.com/lemon8866/spirit)
