# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# 代码混淆压缩比，在 0~7 之间
-optimizationpasses 5
# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames
# 指定不忽略非公共库的类和类成员
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度
-dontpreverify
# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses
# 避免混淆泛型
-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
#默认保留区
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}
# Fragment
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}

#---------------------------------webview------------------------------------
#-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
#   public *;
#}
#-keepclassmembers class * extends android.webkit.WebViewClient {
#    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
#    public boolean *(android.webkit.WebView, java.lang.String);
#}
#-keepclassmembers class * extends android.webkit.WebViewClient {
#    public void *(android.webkit.WebView, jav.lang.String);
#}
#----------------------------------------------------------------------------

#---------------------------------与js互相调用的类---------------------------
##保留annotation， 例如 @JavascriptInterface 等 annotation
#-keepattributes *Annotation*
#
##保留跟 javascript相关的属性
#-keepattributes JavascriptInterface
#
##保留JavascriptInterface中的方法
#-keepclassmembers class * {
#    @android.webkit.JavascriptInterface <methods>;
#}

#----------------------实例bean类----------------------------------------------
-keep class com.rescue.hc.bean.**{*;}
-keep class com.personal.framework.http.module.** {*;}
#----------------------实例bean类----------------------------------------------

#第三方包  如果是依赖的第三方库  要按照库说明来避免混淆

# Support
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-dontwarn android.support.**

#-----------------------------------glide start-------------------------------------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#-----------------------------------glide end---------------------------------------

#-----------------------------------eventbus-----------------------------------
-keepclassmembers class * {
     @de.greenrobot.event.Subscribe <methods>;
 }
-keep enum de.greenrobot.event.ThreadMode { *; }
#-----------------------------------eventbus-----------------------------------

#--------------butterknife-----------------------------------------------------
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
#--------------butterknife-----------------------------------------------------

#--------------BaseRecyclerViewAdapterHelper-----------------------------------
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(...);
}
#--------------BaseRecyclerViewAdapterHelper------------------------------------

#----------------------------------RxJava RxAndroid-----------------------------------------
-dontwarn org.mockito.**
-dontwarn org.junit.**
-dontwarn org.robolectric.**
-keep class io.reactivex.** { *; }
-keep interface io.reactivex.** { *; }
-keep class com.squareup.okhttp.** { *; }
-dontwarn okio.**
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn io.reactivex.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class sun.misc.Unsafe { *; }
-dontwarn java.lang.invoke.*
-keep class io.reactivex.schedulers.Schedulers {
    public static <methods>;
}
-keep class io.reactivex.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class io.reactivex.schedulers.TestScheduler {
    public <methods>;
}
-keep class io.reactivex.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class io.reactivex.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class io.reactivex.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}
-keepclassmembers class io.reactivex.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    io.reactivex.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class io.reactivex.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    io.reactivex.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontwarn io.reactivex.internal.util.unsafe.**
#----------------------------------RxJava RxAndroid-----------------------------------------

#----------------------------------Retrofit相关-----------------------------------------
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions
#----------------------------------Retrofit相关-----------------------------------------

#----------------------------------okhttp3-----------------------------------------------
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okhttp3.logging.**
#org.conscrypt
-dontwarn org.conscrypt.**
-keep class org.conscrypt.** { *; }
-keep interface org.conscrypt.** { *; }
#----------------------------------okhttp3-----------------------------------------------

#----------------------------------RxLifeCycle-------------------------------------------
-keep class com.trello.rxlifecycle2.** { *; }
-keep interface com.trello.rxlifecycle2.** { *; }
-keep class javax.annotation.** { *; }
-dontwarn javax.annotation.**
#----------------------------------RxLifeCycle-------------------------------------------

#----------------------------------utilcode---------------------------------------------
-keep class com.blankj.utilcode.** { *; }
-keepclassmembers class com.blankj.utilcode.** { *; }
-dontwarn com.blankj.utilcode.**
#----------------------------------utilcode---------------------------------------------

#----------------------------------netty------------------------------------------------
-keep class io.netty.** { *; }
-keep class com.bosy.watch.netty.Message{*;}
-dontwarn io.netty.**
#----------------------------------netty------------------------------------------------

#----------------------------------timber-----------------------------------------------
-dontwarn org.jetbrains.annotations.**
#----------------------------------timber-----------------------------------------------

#----------------------------------rxpermissions----------------------------------------
-keep class com.tbruyelle.rxpermissions2.** { *; }
-keep interface com.tbruyelle.rxpermissions2.** { *; }
#----------------------------------rxpermissions----------------------------------------

#----------------------------------greendao---------------------------------------------
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use Rx:
-dontwarn rx.**
#----------------------------------greendao---------------------------------------------

#----------------------------------gson-------------------------------------------------
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
#----------------------------------gson-------------------------------------------------


#----------------------------------okusb------------------------------------------------
-keep class me.zhouzhuo810.okusb.**{*;}
-keep class com.hoho.android.usbserial.**{*;}
#----------------------------------okusb------------------------------------------------

#----------------------------------baidumap------------------------------------------------
-keep class com.baidu.** {*;}
-keep class mapsdkvi.com.** {*;}
-dontwarn com.baidu.**
#----------------------------------baidumap------------------------------------------------

#----------------------------------baiduyuyin-------------------------------------------
-keep class com.baidu.tts.**{*;}
-keep class com.baidu.speechsynthesizer.**{*;}
#----------------------------------baiduyuyin-------------------------------------------