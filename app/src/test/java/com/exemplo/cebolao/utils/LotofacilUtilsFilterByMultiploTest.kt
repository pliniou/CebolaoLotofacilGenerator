import com.exemplo.cebolao.utils.LotofacilUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class LotofacilUtilsFilterByMultiploTest {

    @Test
    fun `filterByMultiplo with valid multiplo`() {
        val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        val multiplo = 3
        val expected = listOf(3, 6, 9, 12, 15)
        val result = LotofacilUtils.filterByMultiplo(numbers, multiplo)
        assertEquals(expected, result)
    }

    @Test
    fun `filterByMultiplo with no multiples`() {
        val numbers = listOf(1, 2, 4, 5, 7, 8, 10, 11, 13, 14)
        val multiplo = 3
        val expected = emptyList<Int>()
        val result = LotofacilUtils.filterByMultiplo(numbers, multiplo)
        assertEquals(expected, result)
    }

    @Test
    fun `filterByMultiplo with zero multiplo`() {
        val numbers = listOf(1, 2, 3, 4, 5, 6)
        val multiplo = 0
        val expected = emptyList<Int>()
        val result = LotofacilUtils.filterByMultiplo(numbers, multiplo)
        assertEquals(expected, result)
    }

    @Test
    fun `filterByMultiplo with negative multiplo`() {
        val numbers = listOf(1, 2, 3, 4, 5, 6, -3, -6)
        val multiplo = -3
        val expected = listOf(-3, -6)
        val result = LotofacilUtils.filterByMultiplo(numbers, multiplo)
        assertEquals(expected, result)
    }

    @Test
    fun `filterByMultiplo with empty list`() {
        val numbers = emptyList<Int>()
        val multiplo = 3
        val expected = emptyList<Int>()
        val result = LotofacilUtils.filterByMultiplo(numbers, multiplo)
        assertEquals(expected, result)
    }
    @Test
    fun `filterByMultiplo with all multiples`() {
        val numbers = listOf(3, 6, 9, 12, 15)
        val multiplo = 3
        val expected = listOf(3, 6, 9, 12, 15)
        val result = LotofacilUtils.filterByMultiplo(numbers, multiplo)
        assertEquals(expected, result)
    }
    @Test
    fun `filterByMultiplo with mixed positive and negative multiples`() {
        val numbers = listOf(3, -3, 6, -6, 9, -9)
        val multiplo = 3
        val expected = listOf(3, 6, 9)
        val result = LotofacilUtils.filterByMultiplo(numbers, multiplo)
        assertEquals(expected, result)
    }
}