# 餐厅菜单组件使用说明

## 概述

RestaurantMenu 组件现在已经整合了 MenuList 的功能，包含完整的购物车系统。这个组件提供了：

- 菜单分类展示
- 商品详情查看
- 购物车管理
- 订单结账功能
- 响应式设计

## 组件结构

### 主要组件

- `RestaurantMenu.vue` - 主菜单组件
- `ItemDetailModal.vue` - 商品详情模态窗口
- `CartModal.vue` - 购物车模态窗口

### 数据结构

#### 餐厅数据格式

```javascript
const restaurant = {
  id: 1,
  name: "餐厅名称",
  image: "餐厅图片URL",
  menuCategories: [
    {
      name: "分类名称",
      items: [
        {
          id: 101,
          name: "商品名称",
          description: "商品描述",
          image: "商品图片URL",
          price: 420, // 当前价格
          originalPrice: 480, // 原价（可选）
          discountPrice: 420, // 折扣价（可选）
          options: [
            // 商品选项（可选）
            {
              id: "size",
              name: "尺寸选择",
              type: "radio", // 'radio' 或 'checkbox'
              items: [
                { id: "small", name: "小", price: 0 },
                { id: "large", name: "大", price: 50 },
              ],
            },
          ],
        },
      ],
    },
  ],
};
```

## 使用方法

### 1. 基本使用

```vue
<template>
  <RestaurantMenu :restaurant="restaurantData" />
</template>

<script setup>
import RestaurantMenu from "@/restaurant-module/components/RestaurantMenu.vue";
import { getRestaurantById } from "@/restaurant-module/data/restaurants.js";

const restaurantData = getRestaurantById(1);
</script>
```

### 2. 监听结账事件

```vue
<template>
  <RestaurantMenu :restaurant="restaurantData" @checkout="handleCheckout" />
</template>

<script setup>
const handleCheckout = (orderData) => {
  console.log("订单数据:", orderData);
  // 处理结账逻辑
  // orderData 包含：
  // - restaurant: { id, name }
  // - items: 购物车商品列表
  // - totalAmount: 总金额
  // - orderTime: 订单时间
};
</script>
```

## 功能说明

### 购物车功能

- **添加商品**: 点击商品卡片或"加入购物车"按钮
- **查看详情**: 点击商品卡片打开详情模态窗口
- **数量调整**: 在购物车中调整商品数量
- **删除商品**: 在购物车中删除不需要的商品
- **结账**: 查看总金额并进行结账

### 商品选项

- **单选选项** (`type: 'radio'`): 如尺寸、熟度选择
- **多选选项** (`type: 'checkbox'`): 如额外配料
- **价格加成**: 选项可以有额外费用
- **备注功能**: 支持自定义备注

### 购物车计算

- 自动计算商品小计
- 支持外送费计算（满 300 免运费）
- 显示订单总金额

## 样式定制

组件使用 CSS 变量系统，可以通过以下变量定制主题：

```css
:root {
  --primary-color: #ff6c00;
  --primary-hover: #e55a00;
  --text-primary: #333;
  --text-secondary: #666;
  --text-muted: #999;
  --price-color: #e74c3c;
}
```

## 响应式设计

组件支持移动端和桌面端：

- 移动端: 单列布局，购物车按钮适配
- 桌面端: 多列网格布局，悬浮购物车

## 注意事项

1. **图片资源**: 确保商品图片 URL 有效
2. **商品 ID**: 每个商品需要唯一的 ID
3. **选项 ID**: 每个选项和选项项需要唯一的 ID
4. **价格格式**: 使用数字格式，组件会自动添加货币符号

## 示例数据

查看 `src/restaurant-module/data/restaurants.js` 获取完整的示例数据结构。

## 扩展功能

可以通过以下方式扩展组件：

1. **自定义结账流程**: 监听 `@checkout` 事件
2. **添加用户登录**: 在结账前检查用户状态
3. **集成支付系统**: 在结账时调用支付 API
4. **订单历史**: 保存用户的订单记录
5. **收藏功能**: 允许用户收藏喜欢的商品

## 技术支持

如有问题，请查看：

1. 组件源码注释
2. 控制台错误信息
3. 网络请求状态
