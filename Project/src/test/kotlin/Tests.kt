import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.NumberFormatException

internal class Tests {

    fun setUpTests(): JSON_Value {

        val numbersFrom1to5 = JSON_Object()

        val string1 = JSON_String("ONE")
        val string2 = JSON_String("TWO")
        val string3 = JSON_String("THREE")
        val string4 = JSON_String("FOUR")
        val string5 = JSON_String("FIVE")

        val number1 = JSON_Number(1.0)
        val number2 = JSON_Number(2.0)
        val number3 = JSON_Number(3.0)
        val number4 = JSON_Number(4.0)
        val number5 = JSON_Number(5.0)

        val even = JSON_String("EVEN NUMBERS")
        val evenNumbers = JSON_Array()
        evenNumbers.addToArray(number2)
        evenNumbers.addToArray(number4)

        val odd = JSON_String("ODD NUMBERS")
        val oddNumbers = JSON_Array()
        oddNumbers.addToArray(number1)
        oddNumbers.addToArray(number3)
        oddNumbers.addToArray(number5)

        val zero = JSON_String("IS ZERO IN HERE?")
        val isZero = JSON_Boolean(false)
        val string0 = JSON_String("ZERO")
        val number0 = JSON_Null()

        numbersFrom1to5.addToObject(string1, number1)
        numbersFrom1to5.addToObject(string2, number2)
        numbersFrom1to5.addToObject(string3, number3)
        numbersFrom1to5.addToObject(string4, number4)
        numbersFrom1to5.addToObject(string5, number5)

        numbersFrom1to5.addToObject(even, evenNumbers)
        numbersFrom1to5.addToObject(odd, oddNumbers)

        numbersFrom1to5.addToObject(zero, isZero)
        numbersFrom1to5.addToObject(string0, number0)

        return numbersFrom1to5

    }

    @Test
    fun testSerialize() {

        val test = setUpTests()

        val serialized = "{\n" +
                "\t\"ONE\" : 1,\n" +
                "\t\"TWO\" : 2,\n" +
                "\t\"THREE\" : 3,\n" +
                "\t\"FOUR\" : 4,\n" +
                "\t\"FIVE\" : 5,\n" +
                "\t\"EVEN NUMBERS\" : [\n" +
                "\t                 \t2,\n" +
                "\t                 \t4\n" +
                "\t                 ],\n" +
                "\t\"ODD NUMBERS\" : [\n" +
                "\t                \t1,\n" +
                "\t                \t3,\n" +
                "\t                \t5\n" +
                "\t                ],\n" +
                "\t\"IS ZERO IN HERE?\" : false,\n" +
                "\t\"ZERO\" : null\n" +
                "}"

        assertEquals(serialized, test.serialize())

    }

    @Test
    fun testSearch() {

        val test = setUpTests()

        val searchForStrings: (JSON_Value) -> Boolean = { value -> value is JSON_String }

        val s1 = JSON_String("ONE")
        val s2 = JSON_String("TWO")
        val s3 = JSON_String("THREE")
        val s4 = JSON_String("FOUR")
        val s5 = JSON_String("FIVE")
        val evenN = JSON_String("EVEN NUMBERS")
        val oddN = JSON_String("ODD NUMBERS")
        val is0 = JSON_String("IS ZERO IN HERE?")
        val s0 = JSON_String("ZERO")

        val searched = mutableListOf<JSON_Value>()
        searched.add(s1)
        searched.add(s2)
        searched.add(s3)
        searched.add(s4)
        searched.add(s5)
        searched.add(evenN)
        searched.add(oddN)
        searched.add(is0)
        searched.add(s0)

        var s = 0
        while (s < searched.size) {
            assertEquals(searched[s].serialize(), test.search(searchForStrings)[s].serialize())
            s++
        }

        val searchForArraysWithMoreThan2Values: (JSON_Value) -> Boolean = { value -> value is JSON_Array && value.arrayList.size > 2 }

        val n1 = JSON_Number(1.0)
        val n3 = JSON_Number(3.0)
        val n5 = JSON_Number(5.0)
        val odd = JSON_Array()
        odd.addToArray(n1)
        odd.addToArray(n3)
        odd.addToArray(n5)

        searched.clear()
        searched.add(odd)

        var a = 0
        while (a < searched.size) {
            assertEquals(searched[a].serialize(), test.search(searchForArraysWithMoreThan2Values)[a].serialize())
            a++
        }

    }

    @Test
    fun testToJSON() {

        val test = setUpTests()

        @Identifier("TEST DATA CLASS")
        data class NumbersFrom1To5(
            @Index(1)
            val ONE: Int,
            @Index(2)
            val TWO: Int,
            @Index(3)
            val THREE: Int,
            @Index(4)
            val FOUR: Int,
            @Index(5)
            val FIVE: Int,
            @Index(6)
            @Identifier("EVEN NUMBERS")
            val EVEN: MutableList<Int>,
            @Index(7)
            @Identifier("ODD NUMBERS")
            val ODD: MutableList<Int>,
            @Index(8)
            @Identifier("IS ZERO IN HERE?")
            val IS_ZERO: Boolean,
            @Index(9)
            val ZERO: Int?,
            @Index(10)
            @Ignore
            val TOTAL_SUM: Int
        )

        val testDataClass = NumbersFrom1To5(1, 2, 3, 4, 5, mutableListOf(2, 4), mutableListOf(1, 3, 5), false, null, 15)

        val testJSONGenerator = Generator()
        assertEquals(test.serialize(), testJSONGenerator.toJSON(testDataClass).serialize())

    }

}