### 更新记录

```
2025/11/18  Tagged the version as 251118 on Docker Hub. Merged the 251118 tag into the latest tag.
升级内置的yt-dlp至最新版本 版本号为 2025.11.12
对于app源码 优化登录 新增记住密码功能  新增新的播放模式 由于部分功能有缺陷 暂未打包
创建一个基础的docker-compose
优化检查更新时  支持选择是否使用代理或者直连更新
可能优化了 抖音在收藏监控下 图文类型不计数 不发送webhook的问题
新增yt-dlp 在webui中添加追加参数功能
在新增视频时 新增保存作者名称  后台列表及api新增videoauthor字段
```

```
2025/10/29  Tagged the version as 251029 on Docker Hub. Merged the 251029 tag into the latest tag.
fix 自动检查cookie 查询异常 检查失败的问题
```


```
2025/10/28  Tagged the version as 251028 on Docker Hub. Merged the 251028 tag into the latest tag.
升级内置的yt-dlp至最新版本 版本号为 2025.10.22
添加抖音微博cookie状态检查 每天九点定时任务(必须在对应功能里使用过一次)
将rednote 从yt-dlp中剥离
rednote支持混杂模式  
    当笔记中仅有一个视频 且视频时长超6s 入视频库
    当笔记中有多个视频/多个图片 入图文库
bilibili 弹幕下载是否开启相关可在webui中单独设置
cookie管理中新增rednote 平台填写
```

```
2025/10/11  Tagged the version as 251017 on Docker Hub. Merged the 251017 tag into the latest tag.
新增bilibili cookie状态检查(251013版本)
新增bilibili cookie自动刷新(251013版本)
修复因在webui界面在扫码登录后 点击保存按钮而造成的刷新token丢失
yt-dlp 替换为源库版本  版本号为 2025.10.14

```

```
2025/10/11  Tagged the version as 251011 on Docker Hub. Merged the 251011 tag into the latest tag.
优化视频列表页面
优化刷新弹幕返回提示
新增yt-dlp内置检查更新功能
新增直链解析模块-后端菜单 目前仅支持抖音详见功能页面使用说明
同步内置yt-dlp至最新

```

```
2025/09/28  Tagged the version as 250928 on Docker Hub. Merged the 250928 tag into the latest tag.
更新小程序及APP端  优化提交页面  支持历史记录 本地模式 显示最近提交十条
优化视频列表界面 新增骨架屏懒加载
APP及小程序端新增收藏类任务 单次执行任务功能
APP端提交链接新增参数校验，防止错误数据提交
同步 yt-dlp 最新源码
新增消息通知通道：Server酱(测试)
收藏类模块新增bilibili合集类下载及监控
收藏类中抖音无法下载图文问题
bilibili 收藏/合集/投稿预埋加载弹幕参数
新版下载的视频可通过视频列表刷新弹幕按钮下载弹幕  当前不支持在下载时同步下载弹幕
优化bilibili 标题选择器问题
其他常规问题优化
```


```
2025/09/23  Tagged the version as 250923 on Docker Hub. Merged the 250923 tag into the latest tag.
拆分了 **NFO** 和 **弹幕下载** 功能：
弹幕下载功能现已独立设置，默认处于关闭状态。
新增了 **视频列表中刷新弹幕** 功能。
移除了 **Webhook** 配置中的 **钉钉** 配置项。
```

```
2025/09/22  Tagged the version as 250922 on Docker Hub. Merged the 250922 tag into the latest tag.
同步 yt-dlp 最新源码
引入java protobuf  
下载弹幕功能(测试) 仅 bilibili
bilibili单链接模式下在开启NFO的模式下支持下载弹幕 ASS格式
目前暂不支持仅开启NFO并关闭弹幕下载
目前仅支持单链接 后续稳定后考虑合并到收藏夹和作品下
```


```
2025/09/05  Tagged the version as 250905 on Docker Hub. Merged the 250905 tag into the latest tag.
新增登录成功之后 发送webhook通知
同步 yt-dlp 最新源码
从yt-dlp中剥离微博平台的支持
适配微博平台支持 划入图文模块管理
微博平台 支持图文类型/视频类型 推文
```

```
2025/08/22  Tagged the version as 250822 on Docker Hub. Merged the 250822 tag into the latest tag.
已知问题修复
spring boot 升级至3.5.5
升级java8至java17
更换docker镜像为openjdk:17-alpine
移除服务端中无用依赖
下版本预计引入gallery-dl 首先适配instagram

```

```
2025/08/18  fix 补丁
优化video.html页面 axios cdn源 加载问题
优化video.html页面 服务器地址问题  可为空  为空是直接使用自身地址
```


```
2025/08/15  Tagged the version as 250815 on Docker Hub. Merged the 250815 tag into the latest tag.
同步 yt-dlp 最新代码
视频新增标签字段  可自定义视频标签 
视频列表新增videotag搜索字段可搜索标签
新增视频列表隐私模式  设置为隐私模式后  视频封面 名称 将隐藏 但不影响播放 输入只读token后解锁显示
```

```
2025/07/31  Tagged the version as 250801 on Docker Hub. Merged the 250801 tag into the latest tag.
同步 yt-dlp 最新代码
将定时任务从 Spring Task 迁移至 Spring Boot Starter Quartz

需要调整任务队列最大容量的 请frok之后 修改application-docker.properties  中的
spring.quartz.properties.org.quartz.threadPool.threadCount=2
自行编译

新收藏任务支持独立 cron 表达式 在添加任务时填写  支持Quartz和常规spring类型表达式
支持非监控类任务手动触发执行
剥离提交时创建任务和执行任务的逻辑  现在创建任务之后不再默认执行一次,请手动执行
后台新增分片下载设置，目前支持 Douyin 平台分片下载
增强内置 HTTP 下载函数
其他已知问题修复
如本次变动出现任务类型错误 请提交对应错误信息  然后将版本先回滚动标签250724版本  理论上没有问题 没有长时间测试
```


```
2025/07/24  Tagged the version as 250724 on Docker Hub. Merged the 250724 tag into the latest tag.
优化 bilibili douyin 单链接 NFO生成参数  actor  下新增profile 项 为作者主页
优化 单链接 NFO 视频简介生成参数问题
优化后台视频列表  视频首页 点击播放 移动端适配的问题
yt-dlp合并上游最新代码
```


```
2025/07/21  Tagged the version as 250721 on Docker Hub. Merged the 250721 tag into the latest tag.
### ✨ New Features
* **New Backend Homepage**: Replaced the old homepage with a redesigned dashboard showing platform-wide statistics and daily data trends.
* **Running Status Monitor**: Added a new status page to view the state of all running threads in the system.
* **Media Homepage**: The previous homepage has been moved to the media library and renamed as the "Media Home".
### 🛠 Optimizations
* **Menu Text Standardization**: All menu labels have been adjusted to a uniform four-character format for consistency.
* **Icon Redesign**: Updated and unified all menu icons for better visual clarity.
* **Mobile Adaptation**: Enhanced compatibility of image-text features on mobile devices.

```


```
2025/07/17  Tagged the version as 250717 on Docker Hub. Merged the 250717 tag into the latest tag.
优化CommandUtil类的资源释放问题
调整单链固定线程为动态线程  最小值1 最大值5 减少冷占用
Fix fixed session issue
抖音图文新增提交记录
修复yt-dlp在全量模式下  获取通用平台 未使用代理配置导致不正常的问题
```

```
2025/07/10  Tagged the version as 250710 on Docker Hub. Merged the 250710 tag into the latest tag.
Merged the latest upstream code of yt-dlp.
Added support for parsing Douyin (TikTok China) note-type content.
Added a "Graphic Library" feature in the backend.

```


```
2025/07/04   docker hub 标记版本为250704  合并到latest
yt-dlp合并上游最新代码
播放主页问题优化 
修复无法动态加载下一页问题
修复分类筛选无效问题
添加视频卡片点击平台筛选功能

```


```
2025/06/30   docker hub 标记版本为250630  合并到latest
yt-dlp合并上游最新代码
移除无用js库 css库
优化js引入问题
dev版本主页配置
```


```
2025/06/12   docker hub 标记版本为050612  合并到latest
yt-dlp合并上游最新代码
```

```
2025/05/23   docker hub 标记版本为250523  合并到latest
修复在开启NFO的情况下  抖音发布者头像 位置下载存放错误的问题
```

```
2025/05/20   docker hub 标记版本为250520  合并到latest
新增jre17版本镜像 理论比java8内存占用低
优化哔哩哔哩取封面图逻辑  
yt-dlp合并上游最新代码
```


```
2025/05/13   docker hub 标记版本为250513  合并到latest
新增版本号显示
新增抖音分享链接模式下 写入作者头像
新增抖音收藏 作品 模式下 写入作者头像
其他已知问题修复

```

```
2025/05/12   docker hub 标记版本为250512  合并到latest
优化提交链接问题  防止提交空连接进入线程  添加地址长度判断>5
分享链接模式新增通过链接判断视频是否已经下载过 如已经在则跳过
分享链接模式  哔哩哔哩的NFO新增up主头像使用网络地址模式

```


```
2025/05/09   docker hub 标记版本为250509  合并到latest
修复小程序cookie失效问题
优化小程序视频卡片问题
小程序新增分享服务器配置及导入服务器配置
后台新增一个icon及PWA模式
后台视频列表新增批量删除功能
```

```
2025/05/07   docker hub 标记版本为250507  合并到latest
请低于此版本的 不要使用删除功能 
如果从以前升级到本版本  以前的资源也不要使用删除功能
如果使用了  会造成单链接内的所有视频都被删除
修复抖音 快手 单链视频地址储存逻辑 避免全部删除问题
修复哔哩哔哩 收藏夹 视频地址储存逻辑  避免无法删除视频
如果不使用本项目的删除  则不影响
```

```
2025/05/07 
重新优化移动端 小程序UI
移动端新增管理登录 视频管理 收藏夹删除功能
```

```
2025/05/06  docker hub 标记版本为250506  合并到latest
新增哔哩哔哩收藏夹NFO使用网络地址 由本应用提供必需先设置只读token
收藏类 添加一个目录分类 放置删除时造成删除整个收藏夹父类
新增一个配置项 NFO网络地址
优化部分平台文件名处理方法  避免带 符号 . 的问题
```

```
2025/04/30  docker hub 标记版本为250430  合并到latest
优化使用yt-dlp解析单链 通用平台不携带token问题
后台新增链接检测  提交视频链接 检测通过后返回平台标识 填入cookie
优化后台cookie状态显示  仅显示已存在的
这样所有平台都可以携带cookie啦 当然这不是强制的 如果可以不填也能用 就不填
```

```
2025/04/28  docker hub 标记版本为250428  合并到latest
新增视频裁剪合并功能
调整部分打印参数

```

```
2025/04/27  docker hub 标记版本为250427  合并到latest
系统配置新增yt-dlp模式
新增链接为不支持平台时 采用yt-dlp作备选  此模式不接受issues 看心情适配 
非内置支持平台可能会出现不兼容问题.这类问题暂时不接受 可自行fork之后自己修改编译
升级哔哩哔哩模块ua头及版本号
删除一些无用代码
优化缺省图
上次版本更新有点乱得问题 没看 

```


```
2025/04/25  docker hub 标记版本为250425  合并到latest
叽叽喳喳叽叽喳喳叽叽喳喳
新增KS平台
其他细节更新
上次版本更新有点乱得问题 没看 

```

```
2025/04/24  docker hub  合并到latest
不知道更新了什么本次更新有点乱
可能会出现收藏夹下载 页面数值显示有问题
bilibli 新增 投稿类 按首次获取最大数下载 和监控数
新增收藏类任务完成发送webhook通知
修复一下参数异常
优化部分UP主无法获取头像 导致NFO逻辑错误而终止进程
优化一些传参  如首次和监控数未填 按内置计算  DY80和哔哩300
其他一些优化  本次直接合并latest了 有问题通过issues提交吧
```


```
2025/04/24  docker hub  合并到latest
优化作品点赞固定参数问题 
新增 作品点赞 本次最大获取数 和监控获取数 该功能未全量适配到所有平台
页面新增下次任务运行时间显示
其他问题下次优化
```

```
2025/04/22  docker hub 标记版本为250422 并合并到latest
修复只读token问题
优化配置界面部分保存问题
新增win桌面查看客户端
新增首页配置预留字段
修复收藏夹中抖音下载作品传参问题
优化用户添加弹窗 优化用户密码6-12 调整为6-16位  
新增哔哩哔哩投稿下载与监控(测试) 元数据可能会出现复用收藏夹的问题
目前投稿仅支持全量查询视频列表  下载数据不是全量是增量模式
预留新版增量模式
新增旧版收藏夹修复功能
优化部分列表宽度
系统配置中新增首页配置选项 预留功能
```

```
2025/04/16  docker hub 标记版本为250416 并合并到latest
重新优化B站内置下载方法 使用okhttp 拒绝手写
某些样式增加一些颜色 避免枯燥
新增预设投稿类方法  目前暂不支持  正在开发
完善飞书webhook通知
优化B站链接 id 提取逻辑
移除无用资源
修复登陆提示
优化部分登录策略及新增只读token  该token无法提交单链接  只能访问资源
```


```
2025/04/15  docker hub 标记版本为250415 并合并到latest
重新优化内置下载方法 增加重试机制和增加超时事件
修复部分元数据生成标题错误
优化部分UI
优化部分功能的jq使用cdn的问题造成页面加载异常
去除解析过程中无用参数
修复推特单链接中存在多视频下载失败的问题
部分表单页面添加 填写提示
已知问题及性能优化
```



```
2025/04/11  docker hub 标记版本为250411
修复youtube、推特生成nfo之后的标题异常问题
开放instagram单链接支持 一样使用yt-dlp下载
修复推特 下载或解析式不携带cookies问题
新增下载成功webhook通知功能
新增部分平台解析可携带useragent  可在系统配置中设置
新增webhook通知配置  目前仅测试 企业微信群机器人
未来支持企业微信群机器人 钉钉 飞书  可在系统配置中查看
修复页面内部分引用问题
移除无用工具类
升级yt-dlp版本
优化资源删除逻辑
其它已知问题修复
```


```
2025/04/10  docker hub 标记版本为250410
Docker 推送调整版本号机制 yymmdd  当数字后缀的版本 比latest 新时说明为测试版本
推送250410版本  当前测试版
系统内支持配置代理  当配置代理后 可为yt-dlp设置代理
移除推特的cookie使用  后边需要的时候在添加回来
优化系统配置布局
Dockerfile.dev 加入全局代理环境变量
推送proxy-test版测试

```

```
2025/03/27
调整应用名称 变更为StreamVault
修复dy解析问题  单链接使用f2库进行解析
优化路径  按照收藏夹和单链接进行分类  
支持nfo元数据生成
修复YouTube和推特解析问题
优化自带HTTP下载内存占用问题
其他已知问题修复
```

```
2023/11/20
更新小程序版本 支持多服务器设置
新增视频查看模式
修改菜单icon
```

```
2023/11/18
暂时屏蔽htmlclient备选方案
```

```
2023/11/15
收藏夹任务新增任务名称
升级yt-dlp
优化定时任务归零问题
```

```
2023/10/27
彻底移除远程xBogus
```

```
2023/10/24
steam 分支升级基础镜像
```


```
2023/10/23
后台更换UI
新增 静态资源 添加验证 需登录 或者请求地址追加?apptoken=xxxx 参数
修复系统配置中定时器无法保存的bug
优化收藏夹监控功能
```


```
2023/10/20
Dev 分支新增 推特  YouTube ins 解析  
Dockerfile 中引入Python3
Dockerfile 中引入yt-dlp
需要特殊网络才可以解析下载
本容器可以做到解析成功 但是 不监控Aria2下载是否成功  在开发阶段 ins 视频有概率失败  但是重启Aria2容器 任务会重新开始 并下载成功 暂时不知道是为什么
后期考虑 在发送请求是给Aria2 发送yt-dlp返回的请求头尝试一下
本功能暂时为合并到 steam 分支及latest 分支 
```


```
2023/10/18
修复 bilibili下载中 分辨率写错判断 导致视频被降级而无法下载的bug
修复 bilibili中 需要ffmpeg合并时  当视频重复下载时 会造成 ffmpeg覆盖问题导致终端卡住的问题
修复 bilibili中 当视频为普通视频时 取的是dash 而不是durl 造成视频下载失败问题
```


```
2023/10/10
新增desktop-win 桌面提交工具
修复latest分支下无bash 问题导致ffmpeg合并失败的问题
优化部分存储路径
优化app 中因文件名包含中文或特殊符号无法播放视频的问题

```


```
2023/09/12
优化以下两个目录
/app/resources/video
/app/resources/cover

优化成
/app/resources/{platform}/{yyyy}/{MM}/{name}/{name}.mp4
/app/resources/{platform}/{yyyy}/{MM}/{name}/{name}.jpg

也就是说将 图片和视频 放在一个文件夹下 方便手工管理
新增steam分支

```

```
修复抖音下载问题 如需使用抖音必须填写登录后的cookie 否则无法下载.移除远程服务器生成Xbogus
如果不填写COOKIE 单个视频将使用htmlclient方式获视频信息,然后就会有内存占用高的问题
所以建议填写Cookie 使用接口获取视频信息
```


```
新增java 生成Xbogus  不再需要调用node的接口  后续稳定后会合并到latest分支  默认情况下 会先本地生成
本地生成无效是会在调用api类型  不过应该不会 所以基本上只会走本地调用 所以 解析地址和qingfeng2336/spirit-assist-node无需在关注
```

```
忘记哪天的了
vercel函数部署的接口 由于所在服务器地址问题可自行搭建 并在系统配置中修改解析地址http://ip:port
docker run --name spirit-assist -d -p 53123:80 qingfeng2336/spirit-assist-node:latest
```

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
