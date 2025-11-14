FROM eclipse-temurin:17-jre

ENV TZ=Asia/Shanghai
ARG BUILD_VERSION=2025.10.22
ENV YT_DLP_VERSION=$BUILD_VERSION

# 安装系统依赖（包括 ffmpeg, python3, pip, venv 等）
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        ffmpeg \
        python3 \
        python3-pip \
        python3-venv \
        python3-dev \
        libffi-dev \
        wget \
        curl \
        ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 创建虚拟环境并安装 f2
RUN python3 -m venv /opt/venv && \
    /opt/venv/bin/pip install --upgrade pip && \
    /opt/venv/bin/pip install --no-cache-dir f2

# 添加虚拟环境路径到环境变量中
ENV PATH="/opt/venv/bin:$PATH"

# 安装 yt-dlp
RUN wget -O /usr/local/bin/yt-dlp https://github.com/yt-dlp/yt-dlp/releases/download/${YT_DLP_VERSION}/yt-dlp && \
    chmod a+rx /usr/local/bin/yt-dlp
ENV YT_DLP_PATH=/usr/local/bin/yt-dlp


# 准备目录结构
VOLUME ["/tmp", "/app"]
COPY db /home/app/db/
COPY script /home/app/script/
RUN mkdir -p /app/resources

ADD StreamVault-0.0.1-SNAPSHOT.jar app.jar

# 启动命令
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar", "--spring.profiles.active=docker"]
