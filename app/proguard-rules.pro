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

# --- Optional bindings that are legitimately absent -------------------------
#
# supabase-kt depends on slf4j-api, whose LoggerFactory looks up a backend
# binding (org.slf4j.impl.StaticLoggerBinder) that Android apps never ship -
# SLF4J falls back to a no-op logger there. R8 reports the unresolved lookup as
# a missing class and fails the build; the classes genuinely are not needed.
-dontwarn org.slf4j.impl.**
-dontwarn org.slf4j.**

# --- App code ---------------------------------------------------------------

# Room entities and the @Serializable wire models travel between Room, the
# Supabase client and the UI. Keeping this (small) package costs little and
# avoids the class of failure where a field is renamed in release but not in
# debug, because nothing in the debug build went through a serializer.
-keep class com.popchat.data.model.** { *; }
-keep class com.popchat.data.supabase.model.** { *; }

# Room instantiates Migration and DatabaseCallback subclasses reflectively.
-keep class * extends androidx.room.migration.Migration { *; }
-keep class * extends androidx.room.RoomDatabase.Callback { *; }

# Enum values()/valueOf() are used by Room type converters and by anything that
# round-trips an enum through a name string.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# kotlinx.serialization resolves serializers through generated Companion
# objects, and needs the annotation metadata and generic signatures intact.
-keepattributes *Annotation*, InnerClasses, Signature, EnclosingMethod
-keepclassmembers class com.popchat.data.supabase.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.popchat.data.supabase.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Line numbers and the source file name are kept so release stack traces from
# Play Console and any crash reporter stay readable.
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# --- Annotations that are only present for tooling --------------------------
-dontwarn javax.annotation.**
-dontwarn jakarta.annotation.**