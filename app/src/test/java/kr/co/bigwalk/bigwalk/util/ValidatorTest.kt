package kr.co.bigwalk.app.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorTest {

    @Test
    fun nicknameValidatorTest() {
        //given
        val validNickname = "빅워크루"
        val invalidNickname = ""
        //when
        val result = nicknameValidator(validNickname)
        val invalidResult = nicknameValidator(invalidNickname)
        //then
        assertTrue(result)
        assertFalse(invalidResult)
    }
}