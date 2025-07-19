#!/bin/bash

echo "=== GoldenBowl SSL 部署腳本 ==="

# 檢查 Docker 是否運行
if ! docker info > /dev/null 2>&1; then
    echo "錯誤：Docker 未運行，請先啟動 Docker Desktop"
    exit 1
fi

# 生成 SSL 憑證
echo "1. 生成 SSL 憑證..."
if [ ! -f "ssl/nginx-selfsigned.crt" ]; then
    chmod +x ssl/generate-ssl.sh
    ./ssl/generate-ssl.sh
else
    echo "SSL 憑證已存在，跳過生成"
fi

# 停止現有容器
echo "2. 停止現有容器..."
docker-compose down

# 建立並啟動容器
echo "3. 建立並啟動容器..."
docker-compose -f docker-compose-ssl.yml up --build -d

# 等待服務啟動
echo "4. 等待服務啟動..."
sleep 30

# 檢查服務狀態
echo "5. 檢查服務狀態..."
docker-compose -f docker-compose-ssl.yml ps

echo ""
echo "=== 部署完成 ==="
echo "應用程式網址："
echo "  - 客戶端：https://localhost"
echo "  - 商家端：https://localhost/store"
echo "  - 管理端：https://localhost/admin"
echo "  - API：https://localhost/api"
echo "  - Jenkins：http://localhost:8081"
echo ""
echo "注意：由於使用自簽名憑證，瀏覽器會顯示安全警告，這是正常的。"
echo "在開發環境中可以點擊「進階」→「繼續前往」來訪問網站。" 