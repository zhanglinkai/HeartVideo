# HeartVideo

封装HeartVideo的初衷是简单应用开发用不到第三方过大的库，从而增加包的体积，同时在使用第三方的时候一些地方会有一定的局限性，所以封装此库，后续会慢慢优化，此库封装了Mediaplayer+TextureView，满足市场上大部分视频格式

#查看是否是你需要的功能：

1.可单独播放视频

2.可列表播放视频

3.可以保存播放进度

4.横竖屏随心所欲的切换

5.支持单视频源，支持多视频源（标清-高清-超清）

6.支持自定义控制界面

7.普通的视频应用肯定能满足，功能不一一列举了


#使用




#说明

1.权限

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
    
2.activity属性

            android:configChanges="keyboardHidden|orientation|screenSize"
            android:screenOrientation="portrait"
            
            
3.包中已经添加recycle人view，如果应用也使用可不必添加，只是使用，或者修改包体中的recycle人view，作用于选择播放源弹出界面
