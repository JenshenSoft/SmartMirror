package com.jenshen.smartmirror.ui.mvp.view.signIn

import com.jenshen.compat.base.view.BaseMvpView
import com.jenshen.smartmirror.model.User
import com.jenshen.smartmirror.util.validation.ValidationResult

interface SignInView : BaseMvpView {
    fun onUserPreviousLoaded(user: User?)
    fun onEmailValidated(result: ValidationResult<String>)
    fun onPasswordValidated(result: ValidationResult<String>)
    fun setLoginButtonState(isEnabled: Boolean)
    fun onLoginClicked()
    fun onLoginSuccess()
}