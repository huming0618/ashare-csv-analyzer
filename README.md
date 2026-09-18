# A股行情 CSV 分析器

基于 **Kotlin + Jetpack Compose + Material 3** 的 Android 应用，用于分析 [ashare-spot-fetcher](https://github.com/huming0618/ashare-spot-fetcher) 导出的 A 股日行情 CSV。

## 三步流程

1. **导入** — 通过系统文件选择器（SAF）选择 CSV，或加载内置示例数据  
2. **分析** — 解析 CSV，计算涨跌家数、平均/中位涨跌幅、总成交额、排行榜与涨跌幅直方图  
3. **呈现** — Material 3 中文界面：摘要卡片、排行列表、柱状直方图  

## 环境要求

- Android Studio Ladybug / Koala 及以上（或等价 AGP 8.7+）
- JDK 17+
- Android SDK：compileSdk / targetSdk **35**，minSdk **26**

## 用 Android Studio 打开

1. `File` → `Open…`，选择本仓库根目录  
2. 等待 Gradle Sync 完成  
3. 连接模拟器或真机，点击 Run  

## 命令行构建 Debug APK

```bash
# 需已配置 ANDROID_HOME，并安装 platform-35、build-tools
chmod +x ./gradlew
./gradlew assembleDebug
```

产物路径：

```
app/build/outputs/apk/debug/app-debug.apk
```

安装到设备：

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## CSV 列（UTF-8，可带 BOM）

```
代码全称,代码,名称,最新价,涨跌额,涨跌幅%,今开,最高,最低,昨收,
成交量(股),成交额,换手率%,市盈率,市净率,总市值(万),流通市值(万),
行情时间,交易日期,数据来源
```

内置示例：`app/src/main/assets/sample_a_share_spot.csv`

## 分析指标

| 指标 | 说明 |
|------|------|
| 涨/跌/平 | 按 `涨跌幅%` 正/负/零计数 |
| 平均 / 中位涨跌幅 | 全市场 `涨跌幅%` |
| 总成交额 / 成交量 | 求和 |
| 涨幅榜 / 跌幅榜 | 按涨跌幅排序 TOP 10 |
| 成交额 / 换手率榜 | TOP 10 |
| 直方图 | 涨跌幅分桶：≤-5、-5~-3、-3~-1、-1~0、0~1、1~3、3~5、≥5 |

## 项目结构

```
app/
  src/main/
    java/com/huming/asharecsvanalyzer/
      data/          # CSV 解析与统计分析
      ui/            # Compose 界面与主题
      MainActivity.kt
      AppViewModel.kt
    assets/          # 内置示例 CSV
```

## 许可证

MIT
