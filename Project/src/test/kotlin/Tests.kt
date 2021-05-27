import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Tests {

    fun setUpTests(): JSONValue {

        val numbersFrom1to5 = JSONObject()

        val string1 = JSONString("ONE")
        val string2 = JSONString("TWO")
        val string3 = JSONString("THREE")
        val string4 = JSONString("FOUR")
        val string5 = JSONString("FIVE")

        val number1 = JSONNumber(1.0)
        val number2 = JSONNumber(2.0)
        val number3 = JSONNumber(3.0)
        val number4 = JSONNumber(4.0)
        val number5 = JSONNumber(5.0)

        val even = JSONString("EVEN NUMBERS")
        val evenNumbers = JSONArray()
        evenNumbers.addToArray(number2)
        evenNumbers.addToArray(number4)

        val odd = JSONString("ODD NUMBERS")
        val oddNumbers = JSONArray()
        oddNumbers.addToArray(number1)
        oddNumbers.addToArray(number3)
        oddNumbers.addToArray(number5)

        val zero = JSONString("IS ZERO IN HERE?")
        val isZero = JSONBoolean(false)
        val string0 = JSONString("ZERO")
        val number0 = JSONNull()

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

        val searchForStrings: (JSONValue) -> Boolean = { value -> value is JSONString }

        val s1 = JSONString("ONE")
        val s2 = JSONString("TWO")
        val s3 = JSONString("THREE")
        val s4 = JSONString("FOUR")
        val s5 = JSONString("FIVE")
        val evenN = JSONString("EVEN NUMBERS")
        val oddN = JSONString("ODD NUMBERS")
        val is0 = JSONString("IS ZERO IN HERE?")
        val s0 = JSONString("ZERO")

        val searched = mutableListOf<JSONValue>()
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

        val searchForArraysWithMoreThan2Values: (JSONValue) -> Boolean = { value -> value is JSONArray && value.arrayList.size > 2 }

        val n1 = JSONNumber(1.0)
        val n3 = JSONNumber(3.0)
        val n5 = JSONNumber(5.0)
        val odd = JSONArray()
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
        assertEquals(testJSONGenerator.toJSON(testDataClass).serialize(), test.serialize())

    }

}