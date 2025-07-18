# GoldenBowl Docker 部署指南

## 📋 系統需求

- Docker Desktop 或 Docker Engine
- Docker Compose
- 至少 4GB RAM
- 至少 10GB 可用磁碟空間

## 🚀 快速部署

### 1. 克隆專案

```bash
git clone <your-repo-url>
cd finalProject
```

### 2. 執行部署腳本

```bash
chmod +x deploy.sh
./deploy.sh
```

### 3. 手動部署 (可選)

```bash
# 建置並啟動所有服務
docker-compose up --build -d

# 查看服務狀態
docker-compose ps

# 查看日誌
docker-compose logs -f
```

## 🌐 訪問地址

部署完成後，可以通過以下地址訪問：

- **客戶端前端**: http://localhost:3000
- **店家前端**: http://localhost:3001
- **管理後台**: http://localhost:3002
- **後端 API**: http://localhost:8080
- **資料庫**: localhost:1433

## 📁 專案結構

```
finalProject/
├── GoldenBowl/                 # Spring Boot 後端
├── vue-cus/                   # 客戶端前端
├── vue-store/                 # 店家前端
├── vue-admin/                 # 管理後台
├── docker-compose.yml         # Docker Compose 配置
├── Dockerfile.backend         # 後端 Dockerfile
├── Dockerfile.frontend        # 前端 Dockerfile
├── nginx.conf                 # Nginx 配置
├── deploy.sh                  # 部署腳本
└── README-Docker.md           # 本文件
```

## 🔧 服務配置

### 資料庫 (SQL Server)

- **容器名稱**: goldenbowl-db
- **端口**: 1433
- **用戶名**: sa
- **密碼**: GoldenBowl123!
- **資料庫名**: GoldenBowl

### 後端 (Spring Boot)

- **容器名稱**: goldenbowl-backend
- **端口**: 8080
- **配置檔案**: application-docker.yml

### 前端服務

- **客戶端**: goldenbowl-frontend-cus (端口 3000)
- **店家端**: goldenbowl-frontend-store (端口 3001)
- **管理端**: goldenbowl-frontend-admin (端口 3002)

## 📝 常用命令

### 服務管理

```bash
# 啟動所有服務
docker-compose up -d

# 停止所有服務
docker-compose down

# 重啟特定服務
docker-compose restart [service-name]

# 查看服務狀態
docker-compose ps
```

### 日誌查看

```bash
# 查看所有服務日誌
docker-compose logs -f

# 查看特定服務日誌
docker-compose logs -f [service-name]

# 查看後端日誌
docker-compose logs -f backend

# 查看資料庫日誌
docker-compose logs -f db
```

### 容器操作

```bash
# 進入後端容器
docker-compose exec backend bash

# 進入資料庫容器
docker-compose exec db bash

# 進入前端容器
docker-compose exec frontend-cus sh
```

### 資料庫操作

```bash
# 連接資料庫
docker-compose exec db /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P GoldenBowl123!

# 執行 SQL 腳本
docker-compose exec db /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P GoldenBowl123! -i /docker-entrypoint-initdb.d/init.sql
```

## 🔍 故障排除

### 1. 服務無法啟動

```bash
# 檢查日誌
docker-compose logs [service-name]

# 檢查網路
docker network ls

# 重新建置
docker-compose up --build -d
```

### 2. 資料庫連接問題

```bash
# 檢查資料庫狀態
docker-compose exec db /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P GoldenBowl123! -Q "SELECT 1"

# 檢查資料庫日誌
docker-compose logs db
```

### 3. 前端無法訪問後端

```bash
# 檢查後端狀態
curl http://localhost:8080/actuator/health

# 檢查網路連接
docker-compose exec frontend-cus ping backend
```

### 4. 清理環境

```bash
# 停止並移除所有容器
docker-compose down

# 移除所有映像檔
docker system prune -a

# 移除資料庫資料
docker volume rm finalproject_db_data
```

## 🔐 環境變數

可以在 `docker-compose.yml` 中修改以下環境變數：

- `SA_PASSWORD`: SQL Server 密碼
- `SPRING_PROFILES_ACTIVE`: Spring Boot 配置檔案
- `MAIL_USERNAME`: 郵件服務用戶名
- `MAIL_PASSWORD`: 郵件服務密碼

## 📊 監控

### 資源使用

```bash
# 查看容器資源使用
docker stats

# 查看磁碟使用
docker system df
```

### 健康檢查

```bash
# 檢查所有服務健康狀態
docker-compose ps

# 檢查特定服務健康狀態
docker inspect [container-name] | grep Health -A 10
```

## 🚀 生產環境部署

### 1. 修改配置

- 更新 `nginx.conf` 中的域名
- 設定 SSL 證書
- 修改資料庫密碼
- 設定環境變數

### 2. 使用 Docker Swarm (可選)

```bash
# 初始化 Swarm
docker swarm init

# 部署服務
docker stack deploy -c docker-compose.yml goldenbowl
```

### 3. 使用 Kubernetes (可選)

- 將 `docker-compose.yml` 轉換為 Kubernetes 配置
- 使用 Helm Chart 部署

## 📞 支援

如果遇到問題，請檢查：

1. Docker 和 Docker Compose 版本
2. 系統資源使用情況
3. 網路連接
4. 防火牆設定

## 🔄 更新部署

```bash
# 拉取最新程式碼
git pull

# 重新建置並部署
./deploy.sh
```
