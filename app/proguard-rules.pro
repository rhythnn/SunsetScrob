-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*, InnerClasses
-keepattributes Signature, Exception
-keepnames class ** { *; }

-keep class kotlin.reflect.** { *; }
-keep interface kotlin.reflect.** { *; }

# Kotlin serialization
-dontnote kotlinx.serialization.SerializationKt
-dontnote kotlinx.serialization.Serialization
-keep,includedescriptorclasses class com.mataku.scrobscrob.**$$serializer { *; }
-keepclassmembers class com.mataku.scrobscrob.** {
    *** Companion;
}
-keepclasseswithmembers class com.mataku.scrobscrob.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Material components
-dontwarn com.google.android.material.**
-keep class com.google.android.material.** { *; }

# AndroidX
-dontwarn androidx.**
-keep class androidx.** { *; }
-keepclassmembers class androidx.** { *; }
-keep interface androidx.* { *; }

# Okhttp
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**

-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.* { *; }

# Ktor
-keep class io.ktor.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

-keep class com.facebook.jni.**  {  *;  }
-keep class com.facebook.flipper.** {  *;  }

-dontwarn com.facebook.litho.**
-dontwarn com.facebook.flipper.**
-dontwarn com.facebook.yoga.**
-dontwarn org.mozilla.**
-dontwarn com.facebook.fbui.**
