package com.se2gruppe5.risikofrontend.game.Dialogs

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.se2gruppe5.risikofrontend.databinding.DialogMoveTroopsBinding
import com.se2gruppe5.risikofrontend.game.territory.ITerritoryVisual
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class MoveTroopDialogTest {

    @Mock
    private lateinit var fromTerritory: ITerritoryVisual

    @Mock
    private lateinit var toTerritory: ITerritoryVisual

    @Mock
    private lateinit var onTroopsSelected: (Int) -> Unit

    @Mock
    private lateinit var inputMethodManager: InputMethodManager

    @Mock
    private lateinit var binding: DialogMoveTroopsBinding

    @Mock
    private lateinit var troopsInput: EditText

    @Mock
    private lateinit var editable: android.text.Editable

    private lateinit var context: Context
    private lateinit var dialog: MoveTroopDialog

    @Before
    fun setUp() {
        // Initialize Mockito
        MockitoAnnotations.initMocks(this)

        // Set up Robolectric context (mock Activity)
        context = Robolectric.buildActivity(android.app.Activity::class.java).get()

        // Mock territory id651 43(required for constructor)
        whenever(fromTerritory.name).thenReturn("Alaska")
        whenever(toTerritory.name).thenReturn("Kamchatka")

        // Mock InputMethodManager
        whenever(context.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(inputMethodManager)

        // Initialize dialog
        dialog = MoveTroopDialog(
            context = context,
            maxTroops = 5,
            minTroops = 2,
            fromTerritory = fromTerritory,
            toTerritory = toTerritory,
        )
        // Mock binding and EditText
        whenever(binding.troopsInput).thenReturn(troopsInput)
        setPrivateField(dialog, "binding", binding)
    }

    @Test
    fun handlePositiveButtonWithValidInputInvokesCallback() {
        // Arrange
        whenever(troopsInput.text).thenReturn(editable)
        whenever(editable.toString()).thenReturn("3")

        // Act
        invokeHandlePositiveButton()

        // Assert
        verify(onTroopsSelected).invoke(3)
        verify(inputMethodManager).hideSoftInputFromWindow(any(), eq(0))
    }

    @Test
    fun handlePositiveButtonWithNonNumericInputDoesNotInvokeCallback() {
        // Arrange
        whenever(troopsInput.text).thenReturn(editable)
        whenever(editable.toString()).thenReturn("abc")

        // Act
        invokeHandlePositiveButton()

        // Assert
        verifyNoInteractions(onTroopsSelected)
        verifyNoInteractions(inputMethodManager)
    }

    @Test
    fun handlePositiveButtonWithOutOfRangeInputDoesNotInvokeCallback() {
        // Arrange
        whenever(troopsInput.text).thenReturn(editable)
        whenever(editable.toString()).thenReturn("6")

        // Act
        invokeHandlePositiveButton()

        // Assert
        verifyNoInteractions(onTroopsSelected)
        verifyNoInteractions(inputMethodManager)
    }

    @Test
    fun hideKeyboardCallsInputMethodManagerAndClearsFocus() {
        // Arrange
        whenever(troopsInput.windowToken).thenReturn(mock())
        whenever(troopsInput.hasFocus()).thenReturn(true)

        // Act
        invokeHideKeyboard()

        // Assert
        verify(inputMethodManager).hideSoftInputFromWindow(any(), eq(0))
        verify(troopsInput).clearFocus()
        assertFalse(troopsInput.hasFocus())
    }

    private fun invokeHandlePositiveButton() {
        dialog.javaClass.getDeclaredMethod("handlePositiveButton").apply {
            isAccessible = true
            invoke(dialog)
        }
    }

    private fun invokeHideKeyboard() {
        dialog.javaClass.getDeclaredMethod("hideKeyboard").apply {
            isAccessible = true
            invoke(dialog)
        }
    }

    private fun setPrivateField(obj: Any, fieldName: String, value: Any) {
        val field = obj.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(obj, value)
    }
}