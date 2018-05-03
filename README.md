# HeartVideo

***

### HeartVideo是通过封装Mediaplayer+TextureView的视频播放器，封装此库的初衷是因为开发过程中简单的应用却要加载第三方过大的库增加了包的体积，同时在使用的过程中有一些功能的实现由局限性，故此封装此播放器，旨在用最简单的方法实现，后续会有更多的优化！

> 查看功能是否是你想要的播放器

  >> 1.横竖屏随心所欲切换
  
  >> 2.支持单视频源，支持多视频源（高清-超清-标清）
  
  >> 3.支持占位图
  
  >> 4.支持单视频播放
  
  >> 5，支持视频列表播放
  
  >> 6.支持自定义控制界面
  
  >> 7.功能不一一列举，满足普通应用的视频功能
  
# 使用

    allprojects {
        repositories {
          ...
          maven { url 'https://www.jitpack.io' }
        }
    }
    
    dependencies {
	        implementation 'com.github.zhanglinkai:HeartVideo:v1.0.0'
	  }
    
# 说明

   ## 权限
      
          <uses-permission android:name="android.permission.INTERNET" />
          <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
          <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
          
          
   ## activity属性
     
     
              android:configChanges="keyboardHidden|orientation|screenSize"
              android:screenOrientation="portrait"
              
              
   ## 其他
      
      
         包中已加载recyclerview 和  Glide  主要作用于选择播放源的列表和占位图的加载，如果使用不必另行加载，如果不需要则修改包中的代码即可
