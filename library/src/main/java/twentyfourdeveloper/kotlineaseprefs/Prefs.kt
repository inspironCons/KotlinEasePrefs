package twentyfourdeveloper.kotlineaseprefs

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object Prefs{
    private var prefs: SharedPreferences? = null

    private fun initPrefs(context:Context,prefName:String,oldPrefs:String?,isMigrate:Boolean,encrypted:Boolean){
        prefs = initializeEncryptedSharedPreferencesManager(context,prefName,encrypted)
        if(oldPrefs != null){
            val nonEncryptedPreferences: SharedPreferences = context.getSharedPreferences(oldPrefs, Context.MODE_PRIVATE)
            if(nonEncryptedPreferences.all.isNotEmpty() && isMigrate){
                nonEncryptedPreferences.copyTo(prefs!!)
                nonEncryptedPreferences.clear()
            }
        }
    }

    private fun initializeEncryptedSharedPreferencesManager(context:Context,prefName:String,encrypted:Boolean): SharedPreferences {
        return if(encrypted){
            try {
                val msKey = MasterKey.Builder(context,MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
                EncryptedSharedPreferences.create(
                    context,
                    prefName,
                    msKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e: Exception) {
                context.getSharedPreferences(prefName,Context.MODE_PRIVATE)
            }
        }else{
            context.getSharedPreferences(prefName,Context.MODE_PRIVATE)
        }

    }

    fun <T> set(key: String, value: T) {
        prefs?.set(key, value)
    }

    fun getString(key: String, defaultValue: String = ""): String {
        val value = getValue(key, defaultValue)
        return value as String
    }

    fun getInt(key: String, defaultValue: Int): Int {
        val value = getValue(key, defaultValue)
        return value as Int
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val value = getValue(key, defaultValue)
        return value as Boolean
    }

    fun getLong(key: String, defaultValue: Long): Long {
        val value = getValue(key, defaultValue)
        return value as Long
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        val value = getValue(key, defaultValue)
        return value as Float
    }

    fun contains(key: String): Boolean{
        if(prefs != null){
           return prefs!!.contains(key)
        }
        throw RuntimeException(
            "Prefs class not correctly instantiated. Please call Builder.setContext().build() in the Application class onCreate."
        )
    }

    fun remove(key: String) {
        if(prefs != null){
            prefs!!.remove(key)
        }else{
            throw RuntimeException(
                "Prefs class not correctly instantiated. Please call Builder.setContext().build() in the Application class onCreate."
            )
        }
    }

    fun clear() {
        if(prefs != null){
            prefs!!.clear()
        }else{
            throw RuntimeException(
                "Prefs class not correctly instantiated. Please call Builder.setContext().build() in the Application class onCreate."
            )
        }
    }

    /**
     * Returns the underlying SharedPreference instance
     *
     * @return an instance of the SharedPreference
     * @throws RuntimeException if SharedPreference instance has not been instantiated yet.
     */
    val preferences: SharedPreferences?
        get() {
            if (prefs != null) {
                return prefs
            }
            throw RuntimeException(
                "Prefs class not correctly instantiated. Please call Builder.setContext().build() in the Application class onCreate."
            )
        }

    /**
     * @return Returns a map containing a list of pairs key/value representing
     * the preferences.
     * @see android.content.SharedPreferences.getAll
     */
    val all: Map<String, *>
        get() = prefs!!.all

    private fun getValue(key: String, defaultValue: Any): Any {
        if(prefs != null) {
            val values = prefs?.all?.get(key)
            return values ?: defaultValue
        }

        throw RuntimeException(
            "Prefs class not correctly instantiated. Please call Builder.setContext().build() in the Application class onCreate."
        )
    }

    private fun SharedPreferences.copyTo(dest: SharedPreferences) {
        for (entry in all.entries) {
            val key = entry.key
            val value: Any? = entry.value
            dest.set(key, value)
        }
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    private fun SharedPreferences.set(key: String, value: Any?) {
        when (value) {
            is String? -> edit { it.putString(key, value) }
            is Int -> edit { it.putInt(key, value.toInt()) }
            is Boolean -> edit { it.putBoolean(key, value) }
            is Float -> edit { it.putFloat(key, value.toFloat()) }
            is Long -> edit { it.putLong(key, value.toLong()) }
            else -> {
                throw IllegalArgumentException("tidak ada tipe yang dimaksud")
            }
        }
    }

    private fun SharedPreferences.clear() {
        edit { it.clear() }
    }

    private fun SharedPreferences.remove(key: String) {
        edit { it.remove(key) }
    }

    /**
     * Builder class for the RekanesiaPrefs instance. You only have to call this once in the Application
     * onCreate. And in the rest of the code base you can call Prefs.method name.
     */
    class Builder {
        private var mContext: Context? = null
        private var mKey: String? = null
        private var migrate:Boolean = false
        private var oldMKey:String?=null
        private var encrypted:Boolean=false

        /**
         * Set the Context used to instantiate the SharedPreferences
         *
         * @param context the application context
         * @return the [com.pixplicity.easyprefs.library.Prefs.Builder] object.
         */
        fun setContext(context: Context?): Builder {
            mContext = context
            return this
        }

        /**
         * Set the filename of the SharedPreference instance. Usually this is the application's
         * packagename.xml but it can be modified for migration purposes or customization.
         *
         * @param prefsName the filename used for the SharedPreference
         * @return the [com.pixplicity.easyprefs.library.Prefs.Builder] object.
         */
        fun setPrefsName(prefsName: String?): Builder {
            mKey = prefsName
            return this
        }

        fun setMigration(areMigration:Boolean = false,oldPrefs:String = ""): Builder{
            migrate = areMigration
            oldMKey = oldPrefs
            return this
        }

        fun encryptedPref():Builder{
            encrypted = true
            return this
        }

        /**
         * Initialize the SharedPreference instance to used in the application.
         *
         * @throws RuntimeException if Context has not been set.
         * @throws RuntimeException if name pref has not been set.
         * @throws RuntimeException if migrate true but old prefs has not been set.
         */
        fun build() {
            if(mContext == null){
                throw RuntimeException("Context not set, please set context before building the Prefs instance.")
            }
            if (mKey == null) {
                throw RuntimeException("pref not set, pleas set the pref name before building the prefs instance.")
            }

            if(migrate && oldMKey == null){
                throw RuntimeException("old name prefs not set, pleas set the old pref name for migrate before building the prefs instance.")
            }

            initPrefs(mContext!!,mKey!!,oldMKey, migrate,encrypted)
        }
    }
}