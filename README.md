# AppUpdate
app一键更新,只需简单代码实现更新及进度显示，支持强制更新。<br>不会重复下载同一版本apk,下载完成跳转安装界面。
---
## 原理<br>
  使用系统自带DownloadManager进行下载并通过contentProvider监听进度，通过DialogFragment来显示进度，<br>强制更新模式下进度框不可取消。
------
## 在代码中使用
```java
 DownloadManagerOptations downloadManagerOptations = new DownloadManagerOptations("1", false, apkUrl)
                        .setTitle("正在更新")
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                        .setDescription("正在努力更新");

 UpdateUtil.update(MainActivity.this, downloadManagerOptations);
```
DownloadManagerOptations可以不指定title、description等,如：
```java
 DownloadManagerOptations downloadManagerOptations = new DownloadManagerOptations("1", true, apkUrl);
 UpdateUtil.update(MainActivity.this, downloadManagerOptations);
```
-----
## 引用
 gradle dependency<br>
 ```xml
 dependencies {
     compile 'com.xingliuhua:lib_app_update:1.0.1'
 }
  ```
 <br>附上效果图<br>
 ![](https://github.com/xingliuhua/AppUpdate/blob/master/Screenshot_2016-07-01-14-07-01_com.xingliuhua.app.png)<br>
 ![](https://github.com/xingliuhua/AppUpdate/blob/master/Screenshot_2016-07-01-14-08-31_com.xingliuhua.app.png)<br>


