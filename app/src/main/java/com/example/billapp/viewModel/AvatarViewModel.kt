package com.example.billapp.viewModel

import AvatarRepository
import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AvatarViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    private val repository = AvatarRepository(FirebaseStorage.getInstance(), context)

    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: StateFlow<String?> = _avatarUrl.asStateFlow()

    private val _groupAvatarUrl = MutableStateFlow<String?>(null)
    val groupAvatarUrl: StateFlow<String?> = _groupAvatarUrl.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadAvatar()
    }

    fun uploadAvatar(imageUri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            try {
                val url = currentUserId?.let { repository.uploadAvatar(imageUri, it) }
                _avatarUrl.value = url
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadGroupAvatar(imageUri: Uri, groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val url = repository.uploadGroupAvatar(imageUri, groupId)
                _groupAvatarUrl.value = url
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun usePresetAvatar(presetResourceId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            try {
                val presetUrl = "android.resource://${context.packageName}/$presetResourceId"
                currentUserId?.let { repository.updateUserImage(it, presetUrl) }
                _avatarUrl.value = presetUrl
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAvatar() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                val avatarUrl = currentUserId?.let { repository.getUserAvatarUrl(it) }
                _avatarUrl.value = avatarUrl
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun loadAvatar(userId: String): String? {
        _isLoading.value = true
        return try {
            repository.getUserAvatarUrl(userId)
        } catch (e: Exception) {
            // Handle error
            null
        } finally {
            _isLoading.value = false
        }
    }


    fun loadGroupAvatar(groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val groupAvatarUrl = repository.getGroupAvatarUrl(groupId)
                _groupAvatarUrl.value = groupAvatarUrl
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun usePresetGroupAvatar(presetResourceId: Int, groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val presetUrl = "android.resource://${context.packageName}/$presetResourceId"
                repository.updateGroupImage(groupId, presetUrl)
                _groupAvatarUrl.value = presetUrl
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}