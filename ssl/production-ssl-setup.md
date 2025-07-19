# 生產環境 SSL 憑證設定

## 選項 1：Let's Encrypt（免費）

### 1. 安裝 Certbot

```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install certbot

# CentOS/RHEL
sudo yum install certbot
```

### 2. 生成憑證

```bash
# 停止 nginx
sudo systemctl stop nginx

# 生成憑證
sudo certbot certonly --standalone -d yourdomain.com -d www.yourdomain.com

# 重新啟動 nginx
sudo systemctl start nginx
```

### 3. 複製憑證到專案

```bash
sudo cp /etc/letsencrypt/live/yourdomain.com/fullchain.pem ssl/
sudo cp /etc/letsencrypt/live/yourdomain.com/privkey.pem ssl/
sudo chown $USER:$USER ssl/*.pem
```

## 選項 2：商業 SSL 憑證

### 1. 購買憑證

- 從 DigiCert、Comodo、GlobalSign 等購買
- 下載憑證檔案

### 2. 放置憑證

```bash
# 將憑證檔案複製到 ssl 目錄
cp your-certificate.crt ssl/
cp your-private-key.key ssl/
```

## 更新 nginx 設定

### 1. 修改 nginx-ssl.conf

```nginx
# 將自簽名憑證路徑改為正式憑證
ssl_certificate /etc/nginx/ssl/fullchain.pem;
ssl_certificate_key /etc/nginx/ssl/privkey.pem;
```

### 2. 重新啟動服務

```bash
docker-compose -f docker-compose-ssl.yml restart nginx
```

## 自動更新（Let's Encrypt）

### 1. 建立更新腳本

```bash
#!/bin/bash
certbot renew
cp /etc/letsencrypt/live/yourdomain.com/fullchain.pem /path/to/project/ssl/
cp /etc/letsencrypt/live/yourdomain.com/privkey.pem /path/to/project/ssl/
docker-compose -f docker-compose-ssl.yml restart nginx
```

### 2. 設定定時任務

```bash
# 編輯 crontab
crontab -e

# 添加每週更新任務
0 2 * * 1 /path/to/ssl-renewal.sh
```

## 安全建議

1. **定期更新憑證**
2. **監控憑證到期時間**
3. **設定憑證到期提醒**
4. **備份憑證檔案**
5. **使用強密碼保護私鑰**
