# Jenkins CI/CD 自動化部署指南

## 概述

本專案使用 Jenkins 進行自動化建置和部署，支援 SSL 加密和完整的 CI/CD 流程。

## 快速開始

### 1. 啟動完整環境（包含 SSL 和 Jenkins）

```bash
# 給予執行權限
chmod +x deploy-ssl.sh

# 執行部署
./deploy-ssl.sh
```

### 2. 訪問 Jenkins

- 網址：http://localhost:8081
- 初始密碼：查看容器日誌獲取

```bash
docker-compose -f docker-compose-ssl.yml logs jenkins
```

### 3. 設定 Jenkins

1. **安裝建議的插件**
2. **建立管理員帳號**
3. **建立新的 Pipeline 專案**

## Jenkins Pipeline 設定

### 建立新的 Pipeline 專案

1. 點擊「建立新工作」
2. 選擇「Pipeline」
3. 專案名稱：`GoldenBowl-Deploy`

### Pipeline 設定

```groovy
// 在 Pipeline 設定中選擇 "Pipeline script from SCM"
// SCM: Git
// Repository URL: 您的 Git 倉庫 URL
// Script Path: jenkins/Jenkinsfile
```

### 觸發建置

- **手動觸發**：點擊「立即建置」
- **自動觸發**：設定 Webhook 或輪詢
- **Git 觸發**：推送到特定分支時自動建置

## 部署流程

### 建置階段

1. **Checkout**：從 Git 倉庫拉取最新程式碼
2. **Build Backend**：使用 Maven 建置 Spring Boot 後端
3. **Build Frontend**：使用 npm 建置三個 Vue.js 前端
4. **Build Docker Images**：建立 Docker 映像檔
5. **Deploy**：部署到生產環境

### 部署後檢查

- 後端 API：https://localhost/api/health
- 前端應用：https://localhost
- Jenkins：http://localhost:8081

## SSL 憑證管理

### 自簽名憑證

- 位置：`ssl/` 目錄
- 生成：執行 `ssl/generate-ssl.sh`
- 有效期：365 天

### 生產環境憑證

對於生產環境，建議使用 Let's Encrypt 或商業 SSL 憑證：

1. **Let's Encrypt 憑證**

```bash
# 安裝 certbot
sudo apt-get install certbot

# 生成憑證
sudo certbot certonly --standalone -d yourdomain.com

# 複製憑證到專案
sudo cp /etc/letsencrypt/live/yourdomain.com/fullchain.pem ssl/
sudo cp /etc/letsencrypt/live/yourdomain.com/privkey.pem ssl/
```

2. **更新 nginx-ssl.conf**

```nginx
ssl_certificate /etc/nginx/ssl/fullchain.pem;
ssl_certificate_key /etc/nginx/ssl/privkey.pem;
```

## 監控和日誌

### 查看容器日誌

```bash
# 所有服務
docker-compose -f docker-compose-ssl.yml logs

# 特定服務
docker-compose -f docker-compose-ssl.yml logs backend
docker-compose -f docker-compose-ssl.yml logs nginx
docker-compose -f docker-compose-ssl.yml logs jenkins
```

### 健康檢查

```bash
# 後端健康檢查
curl -f https://localhost/api/health

# 前端檢查
curl -f https://localhost
```

## 故障排除

### 常見問題

1. **SSL 憑證錯誤**

   - 重新生成憑證：`./ssl/generate-ssl.sh`
   - 重新啟動 nginx：`docker-compose restart nginx`

2. **Jenkins 無法啟動**

   - 檢查 Docker 權限
   - 查看 Jenkins 日誌：`docker-compose logs jenkins`

3. **建置失敗**
   - 檢查 Maven 和 npm 依賴
   - 查看 Jenkins 建置日誌
   - 確認 Git 倉庫權限

### 重新部署

```bash
# 完全重新部署
docker-compose -f docker-compose-ssl.yml down
docker system prune -f
./deploy-ssl.sh
```

## 安全注意事項

1. **生產環境**

   - 使用強密碼
   - 定期更新 SSL 憑證
   - 設定防火牆規則
   - 監控系統日誌

2. **Jenkins 安全**
   - 定期更新 Jenkins 和插件
   - 使用 HTTPS 訪問 Jenkins
   - 設定適當的權限
   - 備份 Jenkins 資料

## 備份和恢復

### 備份資料

```bash
# 資料庫備份
docker exec goldenbowl-db-1 /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P YourStrong@Passw0rd -Q "BACKUP DATABASE GoldenBowl TO DISK = '/var/opt/mssql/backup.bak'"

# Jenkins 備份
docker cp goldenbowl-jenkins-1:/var/jenkins_home jenkins_backup
```

### 恢復資料

```bash
# 恢復資料庫
docker exec goldenbowl-db-1 /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P YourStrong@Passw0rd -Q "RESTORE DATABASE GoldenBowl FROM DISK = '/var/opt/mssql/backup.bak'"

# 恢復 Jenkins
docker cp jenkins_backup goldenbowl-jenkins-1:/var/jenkins_home
```
