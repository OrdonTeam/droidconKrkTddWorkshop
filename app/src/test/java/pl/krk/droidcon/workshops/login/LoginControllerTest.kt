package pl.krk.droidcon.workshops.login

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify
import rx.Observable

class LoginControllerTest {

    private val api = mock<Login.Api>()
    private val view = mock<Login.View>()
    private val controller = LoginController(api, view)

    @Before
    fun setUp() {
        whenever(api.login(any(), any())).thenReturn(Observable.never())
    }

    @Test
    fun shouldShowErrorWhenLoginIsEmpty() {
        loginWithCredentials(login = "")
        verify(view, times(1)).showEmptyCredentialError()
    }

    @Test
    fun shouldNotCallApiIfLoginIsEmpty() {
        loginWithCredentials(login = "")
        verify(api, never()).login(any(), any())
    }

    @Test
    fun shouldShowErrorWhenPasswordIsEmpty() {
        loginWithCredentials(password = "")
        verify(view, times(1)).showEmptyCredentialError()
    }

    @Test
    fun shouldNotCallApiWhenPasswordIsEmpty() {
        loginWithCredentials(password = "")
        verify(api, never()).login(any(), any())
    }

    @Test
    fun shouldShowErrorWhenApiCallFails() {
        stubApiToReturnOnCredentials(login = "login", password = "password", returnValue = Observable.error(RuntimeException()))
        loginWithCredentials(login = "login", password = "password")
        verify(view, times(1)).showLoginFailedError()
    }

    @Test
    fun shouldOpenNextScreenIfApiCallSucceed() {
        stubApiToReturnOnCredentials(login = "login123", password = "password123", returnValue = Observable.just(Unit))
        loginWithCredentials(login = "login123", password = "password123")
        verify(view, times(1)).openNextScreen()
    }

    @Test
    fun shouldShowLoaderOnApiCallStart() {
        loginWithCredentials()
        verify(view, times(1)).showLoader()
    }

    @Test
    fun shouldHideLoaderOnApiCallEnd() {
        stubApiToReturnOnCredentials(returnValue = Observable.just(Unit))
        loginWithCredentials()
        verify(view, times(1)).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfCallIsStillInProgress() {
        stubApiToReturnOnCredentials(returnValue = Observable.never())
        loginWithCredentials()
        controller.onDestroy()
        verify(view, times(1)).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderOnDestroyIfCallHasNotBeenStarted() {
        controller.onDestroy()
        verify(view, never()).hideLoader()
    }

    private fun stubApiToReturnOnCredentials(login: String = any(), password: String = any(), returnValue: Observable<Unit>) {
        whenever(api.login(login, password)).thenReturn(returnValue)
    }

    private fun loginWithCredentials(login: String = "login", password: String = "password") {
        controller.onLogin(login, password)
    }

}