#!/bin/bash

# 建立 SSL 目錄
mkdir -p ssl

# 生成自簽名 SSL 憑證（用於開發環境）
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout ssl/nginx-selfsigned.key \
    -out ssl/nginx-selfsigned.crt \
    -subj "/C=TW/ST=Taiwan/L=Taipei/O=GoldenBowl/CN=localhost"

# 生成 Diffie-Hellman 參數（用於增強安全性）
openssl dhparam -out ssl/dhparam.pem 2048

echo "SSL 憑證已生成完成！"
echo "金鑰檔案：ssl/nginx-selfsigned.key"
echo "憑證檔案：ssl/nginx-selfsigned.crt"
echo "DH 參數：ssl/dhparam.pem" 