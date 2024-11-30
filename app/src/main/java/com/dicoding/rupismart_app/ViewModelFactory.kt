package com.dicoding.rupismart_app


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.rupismart_app.di.Injection
import com.dicoding.rupismart_app.ui.help.HelpViewModel
import com.dicoding.rupismart_app.ui.history.HistoryViewModel
import com.dicoding.rupismart_app.ui.scan.ScanViewModel
import com.dicoding.rupismart_app.ui.setting.SettingViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory private constructor(
    private val rupiSmartRepository: RupiSmartRepository,
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ScanViewModel::class.java) -> ScanViewModel(rupiSmartRepository) as T
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> HistoryViewModel(rupiSmartRepository) as T
            modelClass.isAssignableFrom(HelpViewModel::class.java) -> HelpViewModel(rupiSmartRepository) as T
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> SettingViewModel(rupiSmartRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
        }
    }
}