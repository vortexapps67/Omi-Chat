# R8 / ProGuard rules for the release build.
#
# Most of what this app uses ships its own consumer rules, so this file is
# deliberately short. An earlier version of it kept entire library packages -
# androidx.compose.**, kotlinx.coroutines.**, okhttp3.** and so on - which
# silently disables shrinking for everything Compose-related and inflates the
# release APK. Compose, Room, Hilt, Coil, OkHttp, Retrofit, Moshi, Timber and
# kotlinx-serialization all declare their own requirements, and the Kotlin
# serialization compiler plugin is applied, so R8 already knows how to keep
# serializable classes.

# --- Logging ---------------------------------------------------------------
-dontwarn org.slf4j.impl.**
-dontwarn org.slf4j.**

# --- App code ---------------------------------------------------------------
-keep class com.popchat.data.model.** { *; }
-keep class com.popchat.data.supabase.model.** { *; }
-keep class com.popchat.data.db.** { *; }
-keep class com.popchat.data.repository.** { *; }
-keep class com.popchat.ui.** { *; }
-keep class com.popchat.di.** { *; }

# --- Dagger / Hilt & ViewModel ----------------------------------------------
-keep class dagger.hilt.** { *; }
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.ViewModelProvider$Factory { *; }
-keepclasseswithmembers class * {
    @javax.inject.Inject <init>(...);
}
-keep class javax.inject.** { *; }
-dontwarn dagger.hilt.**
-dontwarn javax.inject.**

# --- Supabase & Ktor & OkHttp ------------------------------------------------
-keep class io.github.jan.supabase.** { *; }
-keep class io.ktor.** { *; }
-dontwarn io.github.jan.supabase.**
-dontwarn io.ktor.**
-dontwarn okhttp3.**

# --- Room Database -----------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.migration.Migration { *; }
-keep class * extends androidx.room.RoomDatabase$Callback { *; }
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# --- Kotlinx Serialization --------------------------------------------------
-keepattributes *Annotation*, InnerClasses, Signature, EnclosingMethod
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Compose & Coil ----------------------------------------------------------
-keep class coil.** { *; }
-dontwarn coil.**
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# --- Line Numbers & Source Files ---------------------------------------------
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

-dontwarn javax.annotation.**
-dontwarn jakarta.annotation.**