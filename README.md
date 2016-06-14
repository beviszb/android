<h1>SiriWaveDemo</h1>
<br>
Android平台仿siri声波震动效果，一种是仿ios9之前的效果，SiriWaveView.java，一种是仿ios9上的SiriWaveViewNine.java。
![](https://github.com/beviszb/android/blob/master/SiriWaveDemo/Screenshot_2016-06-14-16-57-28.png)
<br>
<br>
<h1>WifiControl</h1>
<br>
两个apk，一个做服务提供器，一个做控制点。分别安装在两台android设备上。通过wifi连接到同一个局域网，可以用控制点控制服务提供器的视频播放。加了把服务提供器MainActivity.java的init(Environment.getExternalStorageDirectory() + "/testvideo.mp4");路径改成需要播放的视频的路径就行了。
可以在这Demo的基础上实现各种自定义的功能。
用的是实现了upnp协议的开源框架Cling。
