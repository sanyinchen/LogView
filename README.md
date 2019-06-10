# LogView
##Describe  
Hi,guys ,this is a android logcat library based on [Lynx](https://github.com/pedrovgs/Lynx),so you can show your Android logcat in a float view .But it’s different. I think it is more
useful for hybrid app.  Because it's very simple to import it and don't need other jars.  
Firstly,I am so sorry for my screenshots gif.It's too big , so I have to translate them to the end.  


##Add it to your project 

There are two ways to use this lib.
  
1. Just import this model to your project , but do't forget change your build.gradle and setting.gradle.  
2. Cd the list of my logmodel, and execute "./gradlew makeJar"(if it does't work , please use gradle makeJar) on the console.There will be build a jar(log.jar) in the libs list.

## ~~USE GRADLE DEPENDENCIES~~ （Discard）

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
	
	dependencies {
	        compile 'com.github.sanyinchen:LogView:v1.0'
	}

## USAGES  
It's very simple to use this lib.You should add two line codes to your activity.  
such as:  
	
	LogWindow logWindow = new LogWindow(this, getApplication());
    logWindow.creatLogView();
    
 Please be careful this , you need a promise to execute your application:  
 
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    
 Yes,it's onlely need one piece of promise and two code lines.No more other jars.  
 I think it's very import for a finished project.Because we usually don't want to import too much jars that we don't need in release.
 
## Summary  
* Thanks for [Lynx](https://github.com/pedrovgs/Lynx),it‘s a very nice job.
* If you have any question,please email to me(My email:sanyinchen@gmail.com)
* Welcome to subscribe my [google+](https://plus.google.com/u/0/100465464266192894461)  

## Screenshots
![](https://github.com/sanyinchen/LogView/blob/master/source/instance1.gif)  
![](https://github.com/sanyinchen/LogView/blob/master/source/instance2.gif)
