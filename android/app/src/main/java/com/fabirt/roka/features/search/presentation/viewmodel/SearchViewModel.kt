package com.fabirt.roka.features.search.presentation.viewmodel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fabirt.roka.core.domain.model.Recipe
import com.fabirt.roka.core.domain.repository.RecipeRepository
import com.fabirt.roka.core.error.Failure
import kotlinx.coroutines.launch

class SearchViewModel @ViewModelInject constructor(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>>
        get() = _recipes

    private val _isSearching = MutableLiveData(true)
    val isSearching: LiveData<Boolean>
        get() = _isSearching

    private val _failure = MutableLiveData<Failure?>()
    val failure: LiveData<Failure?>
        get() = _failure

    init {
        requestRecipes()
    }

    fun requestRecipes(query: String = "") {
        viewModelScope.launch {
            _failure.value = null
            _isSearching.value = true
            val result = repository.searchRecipes(query, true)
            _isSearching.value = false
            result.fold(
                { failure ->
                    Log.e("requestRecipes", failure.toString())
                    _failure.value = failure
                },
                { recipes ->
                    _recipes.value = recipes
                }
            )
        }
    }
}