package com.example.amphibians.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.amphibians.AmphibiansInfoApplication
import com.example.amphibians.data.AmphibianInfoRepository
import com.example.amphibians.model.AmphibianInfo
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface AmphibianUiState {
    data class Success(val info: List<AmphibianInfo>) : AmphibianUiState
    object Error : AmphibianUiState
    object Loading : AmphibianUiState
}

class AmphibianViewModel(private val amphibianInfoRepository: AmphibianInfoRepository) : ViewModel() {

    var amphibianUiState: AmphibianUiState by mutableStateOf(AmphibianUiState.Loading)
        private set

    init {
        getAmphibianInfo()
    }

    fun getAmphibianInfo() {
        viewModelScope.launch {
            amphibianUiState = AmphibianUiState.Loading
            amphibianUiState = try {
                AmphibianUiState.Success(amphibianInfoRepository.getAmphibianInfo())
            } catch (e: IOException) {
                AmphibianUiState.Error
            } catch (e: HttpException) {
                AmphibianUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AmphibiansInfoApplication)
                val amphibianInfoRepository = application.container.amphibianInfoRepository
                AmphibianViewModel(amphibianInfoRepository = amphibianInfoRepository)
            }
        }
    }
}