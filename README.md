# 归物+（GoodsManager）

面向精细化生活管理的个人物品管理 APP，灵感来自「归物」的分层收纳理念，聚焦“登记-定位-借用-洞察”完整闭环。项目满足移动互联网开发大作业的核心要求：至少 3 个 Activity、Room 数据持久化、Retrofit 网络请求、WorkManager 借用提醒、Material Design 3 深色/浅色主题等。

## 核心功能

- **主控面板**：展示资产总览、最新入库、远程获取的整理灵感卡片，可快速跳转各业务模块。
- **物品档案**：支持搜索、分类、标签、位置与价值记录，生成 QR 数据串方便贴标；详情页可查看/分享二维码。
- **借用管理**：记录借出人、联系方式、应还日期；每日 WorkManager 检查即将到期并通过通知提醒。
- **数据洞察**：基于 MPAndroidChart 展示分类占比、重点物品数量与资产估值。
- **个性化偏好**：暗色主题切换、借用提醒开关、关于信息等。

## 技术栈

- **语言**：Java（JDK 21 编译；适配 minSdk 24）
- **UI**：Material 3 + ViewBinding + RecyclerView + MPAndroidChart
- **架构**：MVVM（ViewModel + LiveData）+ Repository + Room
- **网络**：Retrofit + OkHttp Logging（接口：`https://api.adviceslip.com/advice`）
- **数据持久化**：Room（物品、借用记录）、SharedPreferences（偏好）
- **通知与后台任务**：WorkManager + NotificationCompat
- **其他**：Glide（图片占位）、ZXing（二位码生成）

## 快速开始

1. 使用 Android Studio Hedgehog 以上版本，JDK 21。
2. `git clone` 或直接导入该目录。
3. 同步 Gradle（AGP 8.6.1，compileSdk/targetSdk 35）。
4. 运行 `app` 模块即可体验；默认启用每日借用提醒任务。

## 测试建议

- **功能**：新增/编辑物品、搜索、生成借用记录、查看统计、切换主题。
- **异常**：关闭网络查看灵感卡片的错误提示；拒绝通知权限验证兼容性。
- **真机**：Android 8.0~15，确保通知与 WorkManager 正常。

## 设计说明书

完整设计说明请见 `docs/设计说明书.md`，包含需求分析、功能实现、技术架构、创新点与心得。

