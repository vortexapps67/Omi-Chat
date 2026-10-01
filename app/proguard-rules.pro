# ProGuard Rules for PopChat

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp { *; }
-keep class * extends dagger.hilt.android.HiltApplication { *; }

# Keep Room entities and DAOs
-keep class com.popchat.data.model.** { *; }
-keep class com.popchat.data.db.dao.** { *; }
-keep class com.popchat.data.db.AppDatabase { *; }

# Keep Supabase models
-keep class com.popchat.data.supabase.model.** { *; }

# Keep ViewModels
-keep class com.popchat.ui.**.*ViewModel { *; }

# Keep Compose related
-keep class androidx.compose.** { *; }
-keep class androidx.activity.compose.** { *; }
-keep class androidx.lifecycle.viewmodel.compose.** { *; }

# Keep Navigation
-keep class androidx.navigation.** { *; }

# Keep Coil
-keep class io.coil.** { *; }

# Keep Kotlinx serialization
-keep class kotlinx.serialization.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Keep OkHttp and Retrofit
-keep class okhttp3.** { *; }
-keep class retrofit2.** { *; }

# Keep Moshi
-keep class com.squareup.moshi.** { *; }

# Keep Firebase
-keep class com.google.firebase.** { *; }

# Keep Supabase
-keep class io.github.jan_tennert.supabase.** { *; }

# Keep Timber
-keep class timber.log.** { *; }

# Keep generated Hilt components
-keep class * extends dagger.hilt.android.HiltApplication { *; }

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable { *; }

# Keep Enum values
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep annotations
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes EnclosingMethod

# Keep JSR 305 annotations
-dontwarn javax.annotation.**

# Keep Room migration paths
-keep class * extends androidx.room.migration.Migration { *; }

# Keep KSP generated classes
-keep class com.popchat.hilt.** { *; }