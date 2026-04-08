package lasori.komp.data.extension

import lasori.komp.data.CodingKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExceptionExtensionTest {

    @Test
    fun `extractPath returns path from serialization error with dotted path`() {
        val exception = Exception("Unexpected JSON token at offset 42: something at path: \$.field1.field2")
        val result = exception.extractPath()

        assertEquals(listOf(CodingKey("field1"), CodingKey("field2")), result)
    }

    @Test
    fun `extractPath returns single element path`() {
        val exception = Exception("Unexpected JSON token at offset 10: something at path: \$.name")
        val result = exception.extractPath()

        assertEquals(listOf(CodingKey("name")), result)
    }

    @Test
    fun `extractPath returns empty list when no path keyword in message`() {
        val exception = Exception("Some error without path info")
        val result = exception.extractPath()

        assertEquals(emptyList(), result)
    }

    @Test
    fun `extractPath returns null when message is null`() {
        val exception = Exception(null as String?)
        val result = exception.extractPath()

        assertNull(result)
    }

    @Test
    fun `extractType detects type before literal keyword`() {
        val exception = Exception("Unexpected JSON token at offset 42: Expected string literal but 'null' literal was found")
        val result = exception.extractType()

        assertEquals("string", result)
    }

    @Test
    fun `extractType detects type before for input keyword`() {
        val exception = Exception("Failed to parse type 'Int' for input")
        val result = exception.extractType()

        assertEquals("Int", result)
    }

    @Test
    fun `extractType detects array type from bracket`() {
        val exception = Exception("Expected start of the array '[', something")
        val result = exception.extractType()

        assertEquals("array", result)
    }

    @Test
    fun `extractType detects object type from brace`() {
        val exception = Exception("Expected start of the object '{', something")
        val result = exception.extractType()

        assertEquals("object", result)
    }

    @Test
    fun `extractType returns null when no type info in message`() {
        val exception = Exception("Some generic error")
        val result = exception.extractType()

        assertNull(result)
    }

    @Test
    fun `extractType returns null when message is null`() {
        val exception = Exception(null as String?)
        val result = exception.extractType()

        assertNull(result)
    }
}
