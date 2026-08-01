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

# made by EDGAR 3.0
-keep class com.nvidia.** {*;}
-keep class com.wardrumstudios.** {*;}
-keep class ru.edgar.launcher.** {*;}
-keep class ru.edgar.launcher.model.Faq {*;}
-keep class ru.edgar.launcher.model.FaqList {*;}
-keep class ru.edgar.launcher.other.Interface {*;}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontwarn com.liulishuo.**
-keep class com.liulishuo.** {*;}
-keepclassmembers  class com.nvidia.** {*;}
-keepclassmembers  class com.wardrumstudios.** {*;}
-keepclassmembers  class ru.edgar.launcher.** {*;}
-keepclassmembers  class ru.edgar.launcher.model.Faq {*;}
-keepclassmembers  class ru.edgar.launcher.model.FaqList {*;}
-keepclassmembers  class ru.edgar.launcher.other.Interface {*;}
-keepclassmembers  class retrofit2.** { *; }
-keepclassmembers  class com.liulishuo.** {*;}
-keepclasseswithmembernames class * {
     native <methods>;
}
# EDGAR 3.0

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile, LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile
#-dontobfuscate
