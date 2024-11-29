package com.dicoding.rupismart_app.data.pref


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "session")
class RupiSmartPreference private constructor(private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>) {
    suspend fun saveDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode
        }
    }
    fun getDarkMode(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: RupiSmartPreference? = null
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

        fun getInstance(dataStore: DataStore<androidx.datastore.preferences.core.Preferences>): RupiSmartPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = RupiSmartPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}