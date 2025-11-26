# StreamVault 🎬

<div align="center">

> 🚀 多平台视频下载整合方案  通过小程序 快捷指令 API WEB 等提交单链快速下载视频 并支持收藏夹模式及监控


</div>

> ⚠️ **注意事项**:
>
> 1. 如Docker Hub镜像版本不包含环境变量需要使用的参数（例如：代理、掩码、时区等），建议自行修改Dockerfile并编译。编译文件位于`backstage/src/main/docker/buildx`，已包含所有必要文件,编译后的jar文件不在上传,建议使用actions编译。
> 2. 更多详细部署方式和配置说明，请查看[项目Wiki](https://github.com/Haoqi7/StreamVault/wiki)和[更新日志](doc/updaterecords.md)。
> 3. 已经借助AI之力完成了本项目的wiki，具体使用可以参考[项目Wiki](https://github.com/Haoqi7/StreamVault/wiki)
> 4. 所有配套客户端及源码可在 `app` 文件夹下找到
> 5. 本项目相对于其他项目来说  使用的java语言  docker部署时建议添加内存限制
> 6. 本项目没有做项目拆分为多个仓库  所以本仓库包含 uniapp tauri electron java 浏览器扩展 每个平台源码
> 7. `backstage为后台服务端 java`  `app/uniapp  手机端源码`   `app/extend  浏览器扩展`  `app/desktop  桌面端客户端源码`


## ⚠️ 声明

* 请严格遵守爬虫规范，不要使用此项目进行任何违法行为。
* 本项目及其全部代码仅供学习与研究使用，不用于任何商业目的或非法用途。
* 不出售、共享、加密、上传、研究和传播任何个人信息。
* 项目及其相关代码仅供学习与研究使用，不构成任何明示或暗示的保证。
* 使用者因使用此项目及其代码可能造成的任何形式的损失，使用者应当自行承担一切风险。
* 使用者在使用本项目的代码和功能时，必须自行研究相关法律法规，并确保其使用行为合法合规；任何因违反法律法规而导致的法律责任和风险，均由使用者自行承担。
* 使用者不得使用本工具从事任何侵犯知识产权的行为，包括但不限于未经授权下载、传播受版权保护的内容；开发者不参与、不支持、不认可任何非法内容的获取或分发。
* 本项目不对使用者涉及的数据收集、存储、传输等处理活动的合规性承担责任；使用者应自行遵守相关法律法规，确保处理行为合法正当；因违规操作导致的法律责任由使用者自行承担。
* 本项目不对任何第三方依赖库、插件或服务的稳定性、安全性、兼容性或持续可用性做出保证，使用者需自行查阅并遵守相关开源或商业许可协议。
* 作者不对因第三方组件带来的合规风险、数据风险或软件风险承担责任。
* 本项目的作者不会提供 StreamVault 项目的付费版本，也不会提供与 StreamVault 项目相关的任何商业服务。
* 基于本项目进行的任何二次开发、修改或编译的程序均与原创作者无关，原创作者不承担与二次开发行为或其结果相关的任何责任；使用者应自行对因二次开发可能带来的各种情况负全部责任。
* 使用者不得将本项目作者与使用者的使用行为关联，也不得要求作者对使用行为造成的任何损失或后果承担责任。
  # 如果使用者使用此项目及其代码，即代表使用者同意遵守上述全部规定。

## 🌟 项目简介

StreamVault（原名：spirit）是一个视频资源管理与下载平台，支持多平台视频解析和下载，提供便捷的资源管理功能。支持API提交视频地址等

## ✨ 主要特性

### 🚀 核心功能

- 🎥 API推送单视频地址下载
- ⬇️ 多种下载方式支持（HTTP、Aria2）
- 📚 哔哩哔哩收藏夹下载与监控
- ❤️ 抖音作品与喜欢列表下载与监控 
- 📋 NFO元数据生成(由于目前机制问题 目前测试仅仅jellyfin支持显示演员头像)
- 💾 视频资源缓存管理
- 📢 下载完成Webhook通知（支持企业微信群机器人/飞书）
- 🏞️ 部分平台支持图文

### 📝 已计划内容

- 🔧 yt-dlp内置更新方式
- Twitter主页或收藏监控
- bilibili 合集类支持（已实现，测试中）
- bilibili 弹幕支持（已实现，测试中）

### 🎯 平台支持

状态说明：

- ✅ 支持
- ❌ 不支持
- 🤔 考虑中
- 🔨 开发中
- 🚀 未来会做


| 平台      | 单链接 | 收藏/作品/主页 | 下载类型     | 备注                                              |
| --------- | ------ | -------------- | ------------ | ------------------------------------------------- |
| 抖音      | ✅     | ✅             | HTTP/Aria2   |                                                   |
| 哔哩哔哩  | ✅     | ✅             | HTTP/Aria2   |                                                   |
| YouTube   | ✅     | 🔨             | 仅支持yt-dlp | 备注①  |
| Twitter   | ✅     | 🔨             | 仅支持yt-dlp | 备注① |
| Instagram | ✅     | 🤔             | 仅支持yt-dlp | 备注① |
| TikTok    | ✅      | 🤔             | 暂时通过yt-dlp|                                                   |
| 快手      | ✅     | 🤔             | HTTP/Aria2   |备注② |
| 微博      | ✅     | 🤔             | HTTP  |  备注④                                                   |
| 红薯      | ✅     | 🤔             | HTTP |                                                   |
| 通用平台  | ✅     | ❌            |  仅支持yt-dlp| 备注③ |

* **通用平台**：除抖音、哔哩哔哩、快手外的所有平台，包括但不限于YouTube、Twitter、Instagram、微博、小红书等，均通过yt-dlp处理。详细支持列表请参考[yt-dlp官方支持站点文档](https://github.com/yt-dlp/yt-dlp/blob/master/supportedsites.md),[自带文档](doc/supportedsites.md)。
* 这里通用平台只是代表没有适配生成NFO 等一些小细节问题
* 由于红薯 微博 TikTok 未添加处理 所以 暂时走通用平台进行处理
* 通用平台支持什么具体请自测
* 备注①： docker版自带 避免产生过多ts文件 还需要合并 麻烦
* 备注②： 未测试Aria2,同时如果出现captcha 自行去APP或web验证后在测试 本处不处理captcha
* 备注③： docker版自带 不接受issues 具体支持地址参考yt-dlp仓库
* 备注④:  由于部分平台属于社交平台 视频并不属于长视频 所以目前一律划入图文模块  图文模块支持视频

### 💻 技术栈

- 🛠️ 后端：Spring Boot 3.5.x + JPA + SQLite
- 📱 前端：UniApp（支持小程序、APP等多端）
- 🐳 容器化：Docker多架构支持（AMD64/ARM64）

## 🔧 部署指南

### 🐳 Docker部署（推荐）

```bash
# 拉取镜像
docker pull haoqi7/stream

# 运行容器
docker run --name stream-vault -d -p 28083:28081 \
  -v d:/home/spirit:/app \
  -v d:/home/spirit/tmp:/tmp \
  qingfeng2336/stream-vault
```

[Docker Hub](https://hub.docker.com/r/haoqi7/stream) | [使用文档](https://github.com/Haoqi7/StreamVault/wiki)

### 🚀 快速开始

1. 🔗 访问 http://your-ip:28083/admin/login
2. 🔑 使用默认账号密码登录
3. ⚙️ 在设置中删除admin并重新新建账号
4. 🎉 开始使用

### 📦 手动部署

- 要求：Java 17
- 详细部署文档待完善

## 📝 更新日志

查看 [更新日志](doc/updaterecords.md) 了解详细更新内容。

## 📱 客户端使用

### 🔗 访问方式

- 🌐 Web后台：http://your-ip:28081/admin/login
- 👤 默认账号：admin
- 🔑 默认密码：123456

### 📱 移动端支持

- 🤖 Android APP
- 💬 微信小程序（开发者模式）
- 🌍 其他UniApp支持的平台

## 🔌 API接口

### 📤 推送接口

```http
POST http://ip:port/api/processingVideos
参数：
- token: 后台设置的token
- video: 链接或分享口令
```

### 📋 获取视频列表

```http
POST http://ip:port/api/findVideos
参数：
- token: 后台设置的token（必填）
- pageNo: 页数（必填）
- pageSize: 每页数量（必填）
- videodesc: 视频描述（选填）
- videoname: 视频名称（选填）
- videoplatform: 视频平台（选填）
```

### 📝 书签提交方式

```javascript
javascript:(function(){
    var token = "你的token";  
    var url = window.location.href; 
    fetch("http://ip:port/api/processingVideos", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: "token=" + encodeURIComponent(token) + "&video=" + encodeURIComponent(url)
    }).then(response => response.json())
      .then(data => alert("请求成功: " + JSON.stringify(data)))
      .catch(error => alert("请求失败: " + error));
})();
```

> ⚠️ **注意**: 通过接口获取的视频播放链接或缩略图，访问时需追加 `?apptoken=xxxx` 参数，否则无法访问。


## 🙏 致谢

项目参考及使用了以下优秀的开源项目：

- [bilibili-API-collect](https://github.com/SocialSisterYi/bilibili-API-collect)
- [parsing-tiktok-video](https://toscode.gitee.com/zong_zh/parsing-tiktok-video)
- [f2](https://github.com/Johnserf-Seed/f2)
- [Light-Year-Admin-Template](https://gitee.com/yinqi/Light-Year-Admin-Template)
- [yt-dlp](https://github.com/yt-dlp/yt-dlp)
- [danmu2ass](https://github.com/gwy15/danmu2ass)

---

## 📄 LICENSE

本项目主体代码采用 [Apache License 2.0](./LICENSE) 授权。

> **注意**：
> - 由于 `bilibili-API-collect` 代码的非商业限制（CC BY-NC 4.0 协议），包含该部分代码的版本 **禁止商业使用**。  
> - 使用本项目时，需同时遵守上述第三方项目的许可证条款。

<div align="center">
感谢所有开源项目的贡献者！
</div>

