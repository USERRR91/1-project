package demo

data class Student(
    val name: String,
    var age: Int,
    val group: String,
    var course: Int,
    var gpa: Double = 4.0
) {
    override fun toString(): String = "$name, $age лет, $group, курс $course, GPA: $gpa"
    
    fun getInfo(): String = "Имя: $name | Возраст: $age | Группа: $group | Курс: $course | GPA: $gpa"
}

class StudentManager(students: MutableList<Student> = mutableListOf()) {
    private val students = students.toMutableList()

    fun showStudents(): List<Student> {
        return students.toList()
    }

    fun addStudent(student: Student): Boolean {
        // Проверка на дубликаты
        if (students.any { it.name.equals(student.name, ignoreCase = true) && it.group == student.group }) {
            return false
        }
        students.add(student)
        return true
    }

    fun findStudent(name: String): Student? =
        students.find { it.name.equals(name, ignoreCase = true) }

    fun findStudentsByGroup(group: String): List<Student> =
        students.filter { it.group.equals(group, ignoreCase = true) }

    fun findStudentsByCourse(course: Int): List<Student> =
        students.filter { it.course == course }

    fun showAdults(): List<Student> =
        students.filter { it.age >= 18 }

    fun showUnderages(): List<Student> =
        students.filter { it.age < 18 }

    fun removeStudent(name: String): Boolean =
        students.removeIf { it.name.equals(name, ignoreCase = true) }

    fun updateStudentAge(name: String, newAge: Int): Boolean {
        val student = findStudent(name) ?: return false
        student.age = newAge
        return true
    }

    fun updateStudentGPA(name: String, gpa: Double): Boolean {
        if (gpa < 0.0 || gpa > 5.0) return false
        val student = findStudent(name) ?: return false
        student.gpa = gpa
        return true
    }

    fun getAverageGPA(): Double {
        return if (students.isEmpty()) 0.0 else students.map { it.gpa }.average()
    }

    fun getTopStudents(count: Int = 5): List<Student> =
        students.sortedByDescending { it.gpa }.take(count)

    fun getTotalStudents(): Int = students.size

    fun exportToString(): String {
        return students.joinToString("\n") { it.getInfo() }
    }
}

private fun prompt(msg: String): String? {
    print(msg)
    return readLine()?.trim()
}

fun main() {
    val initial = mutableListOf(
        Student("Андрей", 18, "ИТ-101", 1, 4.5),
        Student("Иван", 19, "ИТ-101", 1, 4.2),
        Student("Анна", 20, "ИТ-102", 2, 4.8),
        Student("Максим", 17, "ИТ-103", 1, 3.9)
    )

    val manager = StudentManager(initial)

    menu@ while (true) {
        println(
            """
            
            === STUDENT MANAGER ===
            1. Показать всех студентов
            2. Добавить студента
            3. Найти студента
            4. Показать совершеннолетних
            5. Показать несовершеннолетних
            6. Найти студентов по группе
            7. Найти студентов по курсу
            8. Удалить студента
            9. Обновить возраст студента
            10. Обновить GPA студента
            11. Показать топ студентов
            12. Показать среднее GPA
            13. Статистика
            0. Выход
            """.trimIndent()
        )

        when (prompt(">")) {
            "1" -> {
                val students = manager.showStudents()
                if (students.isEmpty()) {
                    println("Список студентов пуст.")
                } else {
                    println("\n📚 Список студентов (всего: ${students.size}):")
                    students.forEach { println("  • $it") }
                }
            }
            "2" -> {
                val name = prompt("Имя:").orEmpty()
                if (name.isBlank()) { println("❌ Имя не может быть пустым."); continue@menu }

                val age = prompt("Возраст:")?.toIntOrNull()
                if (age == null || age < 16 || age > 100) { println("❌ Некорректный возраст."); continue@menu }

                val group = prompt("Группа:").orEmpty()
                if (group.isBlank()) { println("❌ Группа не может быть пустой."); continue@menu }

                val course = prompt("Курс:")?.toIntOrNull()
                if (course == null || course !in 1..4) { println("❌ Некорректный курс (1-4)."); continue@menu }

                val gpa = prompt("GPA (0-5, по умолчанию 4.0):").orEmpty()
                val gpaValue = if (gpa.isBlank()) 4.0 else gpa.toDoubleOrNull() ?: 4.0
                if (gpaValue < 0.0 || gpaValue > 5.0) { println("❌ GPA должна быть от 0 до 5."); continue@menu }

                val added = manager.addStudent(Student(name, age, group, course, gpaValue))
                if (added) {
                    println("✅ Студент добавлен.")
                } else {
                    println("❌ Студент с таким именем и группой уже существует.")
                }
            }
            "3" -> {
                val name = prompt("Имя:").orEmpty()
                if (name.isBlank()) { println("❌ Имя не может быть пустым."); continue@menu }

                val student = manager.findStudent(name)
                println(if (student != null) "✅ ${student.getInfo()}" else "❌ Студент не найден")
            }
            "4" -> {
                val adults = manager.showAdults()
                if (adults.isEmpty()) {
                    println("Совершеннолетних студентов нет.")
                } else {
                    println("\n👨‍🎓 Совершеннолетние студенты (${adults.size}):")
                    adults.forEach { println("  • $it") }
                }
            }
            "5" -> {
                val underages = manager.showUnderages()
                if (underages.isEmpty()) {
                    println("Несовершеннолетних студентов нет.")
                } else {
                    println("\n👦 Несовершеннолетние студенты (${underages.size}):")
                    underages.forEach { println("  • $it") }
                }
            }
            "6" -> {
                val group = prompt("Введите группу:").orEmpty()
                if (group.isBlank()) { println("❌ Группа не может быть пустой."); continue@menu }

                val students = manager.findStudentsByGroup(group)
                if (students.isEmpty()) {
                    println("❌ Студентов в группе $group не найдено.")
                } else {
                    println("\n👥 Студенты группы $group (${students.size}):")
                    students.forEach { println("  • $it") }
                }
            }
            "7" -> {
                val course = prompt("Введите курс:")?.toIntOrNull()
                if (course == null || course !in 1..4) { println("❌ Некорректный курс."); continue@menu }

                val students = manager.findStudentsByCourse(course)
                if (students.isEmpty()) {
                    println("❌ Студентов на $course курсе не найдено.")
                } else {
                    println("\n📖 Студенты $course курса (${students.size}):")
                    students.forEach { println("  • $it") }
                }
            }
            "8" -> {
                val name = prompt("Имя:").orEmpty()
                if (name.isBlank()) { println("❌ Имя не может быть пустым."); continue@menu }

                val removed = manager.removeStudent(name)
                println(if (removed) "✅ Студент удалён." else "❌ Студент не найден.")
            }
            "9" -> {
                val name = prompt("Имя:").orEmpty()
                if (name.isBlank()) { println("❌ Имя не может быть пустым."); continue@menu }

                val newAge = prompt("Новый возраст:")?.toIntOrNull()
                if (newAge == null || newAge < 16 || newAge > 100) { println("❌ Некорректный возраст."); continue@menu }

                val updated = manager.updateStudentAge(name, newAge)
                println(if (updated) "✅ Возраст обновлён." else "❌ Студент не найден.")
            }
            "10" -> {
                val name = prompt("Имя:").orEmpty()
                if (name.isBlank()) { println("❌ Имя не может быть пустым."); continue@menu }

                val newGPA = prompt("Новый GPA (0-5):")?.toDoubleOrNull()
                if (newGPA == null || newGPA < 0.0 || newGPA > 5.0) { println("❌ GPA должна быть от 0 до 5."); continue@menu }

                val updated = manager.updateStudentGPA(name, newGPA)
                println(if (updated) "✅ GPA обновлён." else "❌ Студент не найден.")
            }
            "11" -> {
                val top = manager.getTopStudents(5)
                if (top.isEmpty()) {
                    println("Нет студентов для отображения.")
                } else {
                    println("\n⭐ ТОП-5 студентов по GPA:")
                    top.forEachIndexed { index, student -> println("  ${index + 1}. $student") }
                }
            }
            "12" -> {
                val avg = manager.getAverageGPA()
                println("📊 Среднее GPA всех студентов: ${"%.2f".format(avg)}")
            }
            "13" -> {
                println(
                    """
                    
                    📈 СТАТИСТИКА:
                    Всего студентов: ${manager.getTotalStudents()}
                    Совершеннолетних: ${manager.showAdults().size}
                    Несовершеннолетних: ${manager.showUnderages().size}
                    Среднее GPA: ${"%.2f".format(manager.getAverageGPA())}
                    """.trimIndent()
                )
            }
            "0" -> break@menu
            else -> println("❌ Неверная команда")
        }
    }
    println("\n👋 До свидания!")
}
