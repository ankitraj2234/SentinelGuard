# SentinelGuard ProGuard Rules

# Keep Room entities
-keep class com.sentinelguard.data.database.entities.** { *; }

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# SQLCipher
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.* { *; }

# JavaMail
-keep class javax.mail.** { *; }
-keep class javax.activation.** { *; }
-keep class com.sun.mail.** { *; }
-dontwarn java.awt.**
-dontwarn javax.security.sasl.**

# bcrypt
-keep class at.favre.lib.crypto.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep BuildConfig
-keep class com.sentinelguard.BuildConfig { *; }
