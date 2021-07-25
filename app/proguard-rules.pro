# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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

-repackageclasses
-obfuscationdictionary obfuscation_dictionary.txt
-classobfuscationdictionary obfuscation_dictionary.txt
-packageobfuscationdictionary obfuscation_dictionary.txt

-assumenosideeffects class android.util.Log {
    public static java.lang.String getStackTraceString(...);
    public static boolean isLoggable(...);
    public static int println(...);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
    public static int wtf(...);
}

-keepclasseswithmembers class * { @retrofit2.http.* <methods>; }