@Identifier("Pessoa")
data class Person(
    @Identifier("Nome")
    val name: String,
    @Identifier("Idade")
    val age: Int,
    @Ignore
    val height: Double,
    @Ignore
    val weight: Double,
    @Identifier("Estado Civil")
    val status: PersonStatus? = null,
    @Identifier("Vivo")
    val isAlive: Boolean,
    @Identifier("Amigos")
    val friends: MutableList<Person>
)

enum class PersonStatus {
    Single, Married, Widow
}



fun main() {

    val person1 = Person("André", 21, 1.75, 78.5, PersonStatus.Single, true, mutableListOf())
    val person2 = Person("Daniel", 21, 1.75, 83.0, PersonStatus.Single, true, mutableListOf(person1))
    val person3 = Person("Inês", 22, 1.72, 63.5, PersonStatus.Single, true, mutableListOf(person1, person2))

    val JSON_Generator = Generator()
    JSON_Generator.generateJSON(person1)
    JSON_Generator.generateJSON(person2)
    JSON_Generator.generateJSON(person3)

}