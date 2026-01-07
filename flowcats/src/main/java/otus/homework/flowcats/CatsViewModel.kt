package otus.homework.flowcats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CatsViewModel(
    private val catsRepository: CatsRepository
) : ViewModel() {

    private val _catsState = MutableStateFlow<Fact?>(null)
    val catsState: StateFlow<Fact?> = _catsState.asStateFlow()

    private val _catsErrors = MutableSharedFlow<String>()
    val catsError: SharedFlow<String> = _catsErrors.asSharedFlow()

    init {
        catsRepository.listenForCatFacts()
            .onEach {
                when (it) {
                    is Result.Success<*> -> _catsState.value = it.data as? Fact
                    is Result.Error -> _catsErrors.emit(it.exception.message ?: "Unknown exception")
                }
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }
}

class CatsViewModelFactory(private val catsRepository: CatsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CatsViewModel(catsRepository) as T
}
