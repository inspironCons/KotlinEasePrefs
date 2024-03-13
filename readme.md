# KotlinEasePrefs

KotlinEasePrefs is a Kotlin library for easy and secure SharedPreferences management in Android applications.

## Installation

Add the following dependency to your `build.gradle` file:

**groovy**
```groovy
implementation 'twentyfourdeveloper:kotlineaseprefs:1.0.0'
```
**Kotlin**
```kotlin-
implementation('twentyfourdeveloper:kotlineaseprefs:1.0.0')
```
## Usage

Initialize the KotlinEasePrefs in your `Application class` onCreate method :

```
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Prefs.Builder()
            .setContext(this)
            .setPrefsName("my_prefs")
            .setMigration(true, "old_prefs") //optional
            .encryptedPref() // optional: enable encrypted preferences
            .build()
    }
}
```

Use KotlinEasePrefs methods to save, retrieve, and manage preferences
```
// Save preferences
Prefs.set("key", value)

// Retrieve preferences
val value: String = Prefs.getString("key")
val value: Int = Prefs.getInt("key", defaultValue)
val value: Boolean = Prefs.getBoolean("key", defaultValue)
val value: Long = Prefs.getLong("key", defaultValue)
val value: Float = Prefs.getFloat("key", defaultValue)

// Check if a preference exists
val exists: Boolean = Prefs.contains("key")

// Remove a preference
Prefs.remove("key")

// Clear all preferences
Prefs.clear()
```

## Terms of Use for KotlinEasePrefs

**Acceptance of Terms:** By using the KotlinEasePrefs library, you agree to be bound by the terms and conditions outlined below. If you do not agree to these terms, please do not use this Library.

**Permitted Use:** You are permitted to use this Library for personal, commercial, or software development purposes. You are allowed to modify and distribute this Library in accordance with the license specified below.

**Limitation of Liability:** The author of this Library is not liable for any misuse or abuse of this Library. Users are solely responsible for the use of this Library and agree not to use this Library for illegal, malicious, or harmful purposes.

**License Terms:** This Library is provided under the `MIT License`, which governs the use, distribution, and modification of this Library. Please refer to the [LICENSE file](https://opensource.org/license/mit) for full details of the applicable license.

**Changes to Terms:** The author of this Library reserves the right to change or update these Terms of Use from time to time without prior notice. Users are expected to regularly check this page for updates.

By using this Library, you confirm that you have read, understood, and agreed to these Terms of Use

## License

This library is licensed under the `MIT License`. See the [LICENSE](https://opensource.org/license/mit) file for details.