package com.wishland.soler.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wishland.soler.data.dto.ResponseModel
import com.wishland.soler.data.repository.AdsRepoImp
import com.wishland.soler.domain.repository.AdsRepo
import com.wishland.soler.utils.UiState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AdsViewModel: ViewModel() {

    private val repo: AdsRepo = AdsRepoImp()

    private val _urlResponse = MutableLiveData<UiState<ResponseModel>>()
    val urlResponse : LiveData<UiState<ResponseModel>>
        get() = _urlResponse

    fun getJumpUrl(packageName: String){

        viewModelScope.launch {
            repo.getJumpCodeUrl(packageName)
                .catch { err -> _urlResponse.value = UiState.Error(err) }
                .collectLatest {
                    _urlResponse.value = UiState.Success(it)
                }
        }
    }
}