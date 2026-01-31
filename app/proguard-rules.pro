# Keep rules for Kotlin serialization
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class **$Companion { *; }
-keepclasseswithmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}
