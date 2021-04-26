fun main() {

    //println("A1" + JSON_Whitespace.Space.whitespace + "B1")
    //println("A2" + JSON_Whitespace.Linefeed.whitespace + "B2")
    //println("A3" + JSON_Whitespace.CarriageReturn.whitespace + "B3")
    //println("A4" + JSON_Whitespace.HorizontalTab.whitespace + "B4")

    val s1 = JSON_String("S1")
    val s2 = JSON_String("S2")
    val s3 = JSON_String("S3")
    val s4 = JSON_String("S4")
    val s5 = JSON_String("S5")

    val n1 = JSON_Number(-0.9)
    val n2 = JSON_Number(12.0)
    val n3 = JSON_Number(250000000000.0)

    val o1 = JSON_Object()
    val o2 = JSON_Object()
    val o3 = JSON_Object()

    val a1 = JSON_Array()
    a1.addToArray(n1)
    a1.addToArray(n2)
    a1.addToArray(n3)

    o3.addToObject(s3, a1)
    o3.addToObject(s4, s5)
    o2.addToObject(s2, o3)
    o1.addToObject(s1, o2)

    println(o1.serialize())

    val searchForStrings: (JSON_Value) -> Boolean = { value -> value is JSON_String }
    println(o1.search(searchForStrings))

}