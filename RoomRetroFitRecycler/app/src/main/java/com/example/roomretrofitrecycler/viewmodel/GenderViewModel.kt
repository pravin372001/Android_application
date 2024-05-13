import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.roomretrofitrecycler.model.Gender
import com.example.roomretrofitrecycler.repository.GenderRepository
import com.example.roomretrofitrecycler.service.GenderApi
import com.example.roomretrofitrecycler.service.GenderApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GenderViewModel(private val repository: GenderRepository) : ViewModel() {

    private val _gender = MutableLiveData<Gender>()
    val gender:LiveData<Gender> =_gender

    fun insertGender(gender: Gender) {
        viewModelScope.launch {
            repository.insertGender(gender)
        }
    }

    fun updateGender(gender: Gender) {
        viewModelScope.launch {
            repository.updateGender(gender)
        }
    }

    fun deleteGenderById(id: Int) {
        viewModelScope.launch {
            repository.deleteGenderById(id)
        }
    }

    fun deleteAllGenders() {
        viewModelScope.launch {
            repository.deleteAllGenders()
        }
    }

    fun resetSeq(){
        viewModelScope.launch {
            repository.resetSequence()
        }
    }

    fun getAllGendersList(): LiveData<List<Gender>> {
        return repository.getAllGendersList()
    }

    fun fetchGender(name: String){
        viewModelScope.launch {
            try {
                val genderData = withContext(Dispatchers.IO){
                    GenderApi.retrofitApiServies.getData(name)
                }
                _gender.value = genderData
            } catch (e: Exception) {
                Log.i("GenderViewModel", "${e.stackTrace}")
            }
        }
    }

    // Factory class to create the ViewModel with the repository
    class Factory(private val repository: GenderRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GenderViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GenderViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
