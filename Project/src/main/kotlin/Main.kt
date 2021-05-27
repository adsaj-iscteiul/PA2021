@Identifier("Pessoa")
data class Person(
    @Index(1)
    @Identifier("Nome")
    val name: String,
    @Index(2)
    @Identifier("Idade")
    val age: Int,
    @Index(3)
    @Ignore
    val height: Double,
    @Index(4)
    @Ignore
    val weight: Double,
    @Index(5)
    @Identifier("Estado Civil")
    val status: PersonStatus? = null,
    @Index(6)
    @Identifier("Vivo")
    val isAlive: Boolean,
    @Index(7)
    @Identifier("Amigos")
    val friends: MutableList<Person>
)

enum class PersonStatus {
    Single, Married, Widow
}

fun main() {

    val person1 = Person("André", 21, 1.75, 78.5, PersonStatus.Single, true, mutableListOf())
    val person2 = Person("Daniel", 21, 1.75, 83.0, PersonStatus.Single, true, mutableListOf(person1))
    val person3 = Person("Inês", 22, 1.72, 63.5, PersonStatus.Single, true, mutableListOf(person1,person2))

    val window = Injector.create(Window::class)
    window.open(mutableListOf(person1, person2, person3))
    //Window().open(mutableListOf(person1, person2, person3))

}