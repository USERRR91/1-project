package demo

import javax.swing.*
import javax.swing.table.DefaultTableModel
import java.awt.*
import java.awt.event.ActionEvent

class StudentManagerGUI(private val manager: StudentManager) : JFrame() {
    private val tableModel = DefaultTableModel()
    private val table = JTable(tableModel)
    private val statusLabel = JLabel("Готов")
    private val studentCountLabel = JLabel("Студентов: 0")

    init {
        setupUI()
        refreshTable()
    }

    private fun setupUI() {
        title = "Student Manager"
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(1000, 700)
        setLocationRelativeTo(null)
        isResizable = true

        // Main panel
        val mainPanel = JPanel(BorderLayout(10, 10))
        mainPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        // Top panel with controls
        val topPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 5))
        topPanel.add(JButton("➕ Добавить").apply { addActionListener { addStudent() } })
        topPanel.add(JButton("✏️ Обновить").apply { addActionListener { updateStudent() } })
        topPanel.add(JButton("🗑️ Удалить").apply { addActionListener { deleteStudent() } })
        topPanel.add(JSeparator(SwingConstants.VERTICAL).apply { preferredSize = Dimension(2, 30) })
        topPanel.add(JButton("👨‍🎓 Совершеннолетние").apply { addActionListener { showAdults() } })
        topPanel.add(JButton("👦 Несовершеннолетние").apply { addActionListener { showUnderages() } })
        topPanel.add(JSeparator(SwingConstants.VERTICAL).apply { preferredSize = Dimension(2, 30) })
        topPanel.add(JButton("📊 Статистика").apply { addActionListener { showStatistics() } })
        topPanel.add(JButton("🔄 Обновить").apply { addActionListener { refreshTable() } })

        // Search panel
        val searchPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 5))
        val searchField = JTextField(20)
        searchPanel.add(JLabel("🔍 Поиск по имени:"))
        searchPanel.add(searchField)
        searchPanel.add(JButton("Искать").apply {
            addActionListener {
                val name = searchField.text
                if (name.isNotBlank()) {
                    val student = manager.findStudent(name)
                    if (student != null) {
                        tableModel.setRowCount(0)
                        tableModel.addRow(arrayOf(student.name, student.age, student.group, student.course, String.format("%.2f", student.gpa)))
                        updateStatus("✅ Студент найден")
                    } else {
                        updateStatus("❌ Студент не найден")
                        tableModel.setRowCount(0)
                    }
                }
            }
        })
        searchPanel.add(JButton("Очистить").apply {
            addActionListener {
                searchField.text = ""
                refreshTable()
            }
        })

        // Filter panel
        val filterPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 5))
        filterPanel.add(JLabel("Фильтр по группе:"))
        val groupField = JTextField(10)
        filterPanel.add(groupField)
        filterPanel.add(JButton("Фильтровать").apply {
            addActionListener {
                val group = groupField.text
                if (group.isNotBlank()) {
                    val students = manager.findStudentsByGroup(group)
                    tableModel.setRowCount(0)
                    students.forEach {
                        tableModel.addRow(arrayOf(it.name, it.age, it.group, it.course, String.format("%.2f", it.gpa)))
                    }
                    updateStatus("Найдено студентов: ${students.size}")
                }
            }
        })

        filterPanel.add(JSeparator(SwingConstants.VERTICAL).apply { preferredSize = Dimension(2, 30) })
        filterPanel.add(JLabel("Фильтр по курсу:"))
        val courseField = JSpinner(SpinnerNumberModel(1, 1, 4, 1))
        filterPanel.add(courseField)
        filterPanel.add(JButton("Фильтровать").apply {
            addActionListener {
                val course = (courseField.value as Number).toInt()
                val students = manager.findStudentsByCourse(course)
                tableModel.setRowCount(0)
                students.forEach {
                    tableModel.addRow(arrayOf(it.name, it.age, it.group, it.course, String.format("%.2f", it.gpa)))
                }
                updateStatus("Найдено студентов: ${students.size}")
            }
        })

        // Table setup
        tableModel.addColumn("Имя")
        tableModel.addColumn("Возраст")
        tableModel.addColumn("Группа")
        tableModel.addColumn("Курс")
        tableModel.addColumn("GPA")

        table.rowHeight = 25
        table.selectionMode = ListSelectionModel.SINGLE_SELECTION
        val scrollPane = JScrollPane(table)
        scrollPane.preferredSize = Dimension(900, 400)

        // Bottom panel with info
        val bottomPanel = JPanel(FlowLayout(FlowLayout.LEFT, 20, 5))
        bottomPanel.add(studentCountLabel)
        bottomPanel.add(JSeparator(SwingConstants.VERTICAL).apply { preferredSize = Dimension(2, 30) })
        bottomPanel.add(statusLabel)

        // Assemble
        mainPanel.add(topPanel, BorderLayout.NORTH)
        mainPanel.add(searchPanel, BorderLayout.PAGE_START)
        mainPanel.add(filterPanel, BorderLayout.PAGE_START)
        mainPanel.add(scrollPane, BorderLayout.CENTER)
        mainPanel.add(bottomPanel, BorderLayout.SOUTH)

        add(mainPanel)
    }

    private fun addStudent() {
        val dialog = JDialog(this, "Добавить студента", true)
        dialog.size = Dimension(400, 300)
        dialog.setLocationRelativeTo(this)

        val panel = JPanel(GridLayout(5, 2, 10, 10))
        panel.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)

        val nameField = JTextField()
        val ageSpinner = JSpinner(SpinnerNumberModel(18, 16, 100, 1))
        val groupField = JTextField()
        val courseSpinner = JSpinner(SpinnerNumberModel(1, 1, 4, 1))
        val gpaSpinner = JSpinner(SpinnerNumberModel(4.0, 0.0, 5.0, 0.1))

        panel.add(JLabel("Имя:"))
        panel.add(nameField)
        panel.add(JLabel("Возраст:"))
        panel.add(ageSpinner)
        panel.add(JLabel("Группа:"))
        panel.add(groupField)
        panel.add(JLabel("Курс:"))
        panel.add(courseSpinner)
        panel.add(JLabel("GPA:"))
        panel.add(gpaSpinner)

        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER, 10, 10))
        buttonPanel.add(JButton("Добавить").apply {
            addActionListener {
                val name = nameField.text
                val age = (ageSpinner.value as Number).toInt()
                val group = groupField.text
                val course = (courseSpinner.value as Number).toInt()
                val gpa = (gpaSpinner.value as Number).toDouble()

                if (name.isBlank() || group.isBlank()) {
                    JOptionPane.showMessageDialog(dialog, "❌ Заполните все поля!", "Ошибка", JOptionPane.ERROR_MESSAGE)
                } else {
                    val added = manager.addStudent(Student(name, age, group, course, gpa))
                    if (added) {
                        updateStatus("✅ Студент добавлен")
                        refreshTable()
                        dialog.dispose()
                    } else {
                        JOptionPane.showMessageDialog(dialog, "❌ Студент с таким именем и группой уже существует!", "Ошибка", JOptionPane.ERROR_MESSAGE)
                    }
                }
            }
        })
        buttonPanel.add(JButton("Отмена").apply { addActionListener { dialog.dispose() } })

        dialog.add(panel, BorderLayout.CENTER)
        dialog.add(buttonPanel, BorderLayout.SOUTH)
        dialog.isVisible = true
    }

    private fun updateStudent() {
        val selectedRow = table.selectedRow
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "❌ Выберите студента!", "Ошибка", JOptionPane.ERROR_MESSAGE)
            return
        }

        val name = tableModel.getValueAt(selectedRow, 0) as String
        val dialog = JDialog(this, "Обновить данные", true)
        dialog.size = Dimension(400, 250)
        dialog.setLocationRelativeTo(this)

        val panel = JPanel(GridLayout(3, 2, 10, 10))
        panel.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)

        val ageSpinner = JSpinner(SpinnerNumberModel(18, 16, 100, 1))
        val gpaSpinner = JSpinner(SpinnerNumberModel(4.0, 0.0, 5.0, 0.1))

        panel.add(JLabel("Новый возраст:"))
        panel.add(ageSpinner)
        panel.add(JLabel("Новый GPA:"))
        panel.add(gpaSpinner)

        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER, 10, 10))
        buttonPanel.add(JButton("Обновить").apply {
            addActionListener {
                val newAge = (ageSpinner.value as Number).toInt()
                val newGPA = (gpaSpinner.value as Number).toDouble()

                manager.updateStudentAge(name, newAge)
                manager.updateStudentGPA(name, newGPA)
                updateStatus("✅ Данные обновлены")
                refreshTable()
                dialog.dispose()
            }
        })
        buttonPanel.add(JButton("Отмена").apply { addActionListener { dialog.dispose() } })

        dialog.add(panel, BorderLayout.CENTER)
        dialog.add(buttonPanel, BorderLayout.SOUTH)
        dialog.isVisible = true
    }

    private fun deleteStudent() {
        val selectedRow = table.selectedRow
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "❌ Выберите студента!", "Ошибка", JOptionPane.ERROR_MESSAGE)
            return
        }

        val name = tableModel.getValueAt(selectedRow, 0) as String
        val result = JOptionPane.showConfirmDialog(this, "Удалить студента: $name?", "Подтверждение", JOptionPane.YES_NO_OPTION)

        if (result == JOptionPane.YES_OPTION) {
            if (manager.removeStudent(name)) {
                updateStatus("✅ Студент удалён")
                refreshTable()
            } else {
                JOptionPane.showMessageDialog(this, "❌ Ошибка при удалении", "Ошибка", JOptionPane.ERROR_MESSAGE)
            }
        }
    }

    private fun showAdults() {
        val adults = manager.showAdults()
        if (adults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Совершеннолетних студентов нет", "Информация", JOptionPane.INFORMATION_MESSAGE)
            return
        }
        tableModel.setRowCount(0)
        adults.forEach {
            tableModel.addRow(arrayOf(it.name, it.age, it.group, it.course, String.format("%.2f", it.gpa)))
        }
        updateStatus("Показаны совершеннолетние: ${adults.size}")
    }

    private fun showUnderages() {
        val underages = manager.showUnderages()
        if (underages.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Несовершеннолетних студентов нет", "Информация", JOptionPane.INFORMATION_MESSAGE)
            return
        }
        tableModel.setRowCount(0)
        underages.forEach {
            tableModel.addRow(arrayOf(it.name, it.age, it.group, it.course, String.format("%.2f", it.gpa)))
        }
        updateStatus("Показаны несовершеннолетние: ${underages.size}")
    }

    private fun showStatistics() {
        val totalCount = manager.getTotalStudents()
        val adultsCount = manager.showAdults().size
        val underagesCount = manager.showUnderages().size
        val averageGPA = String.format("%.2f", manager.getAverageGPA())
        val topStudents = manager.getTopStudents(5)

        val dialog = JDialog(this, "📊 Статистика", true)
        dialog.size = Dimension(500, 400)
        dialog.setLocationRelativeTo(this)

        val textArea = JTextArea()
        textArea.isEditable = false
        textArea.font = Font("Monospaced", Font.PLAIN, 12)
        textArea.text = """
            ═══════════════════════════════════════
            📊 СТАТИСТИКА СТУДЕНТОВ
            ═══════════════════════════════════════
            
            📈 Общая информация:
               • Всего студентов: $totalCount
               • Совершеннолетних: $adultsCount
               • Несовершеннолетних: $underagesCount
            
            📐 Успеваемость:
               • Среднее GPA: $averageGPA
            
            ⭐ ТОП-5 студентов по GPA:
        """.trimIndent()

        topStudents.forEachIndexed { index, student ->
            textArea.append("\n   ${index + 1}. ${student.name} (${student.group}) - GPA: ${String.format("%.2f", student.gpa)}")
        }

        textArea.append("\n\n═══════════════════════════════════════")

        val scrollPane = JScrollPane(textArea)
        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER))
        buttonPanel.add(JButton("Закрыть").apply { addActionListener { dialog.dispose() } })

        dialog.add(scrollPane, BorderLayout.CENTER)
        dialog.add(buttonPanel, BorderLayout.SOUTH)
        dialog.isVisible = true
    }

    private fun refreshTable() {
        tableModel.setRowCount(0)
        val students = manager.showStudents()
        students.forEach {
            tableModel.addRow(arrayOf(it.name, it.age, it.group, it.course, String.format("%.2f", it.gpa)))
        }
        studentCountLabel.text = "Студентов: ${students.size}"
        updateStatus("✅ Таблица обновлена")
    }

    private fun updateStatus(message: String) {
        statusLabel.text = message
    }
}

fun mainGUI() {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (e: Exception) {
        e.printStackTrace()
    }

    val initial = mutableListOf(
        Student("Андрей", 18, "ИТ-101", 1, 4.5),
        Student("Иван", 19, "ИТ-101", 1, 4.2),
        Student("Анна", 20, "ИТ-102", 2, 4.8),
        Student("Максим", 17, "ИТ-103", 1, 3.9)
    )

    val manager = StudentManager(initial)
    val gui = StudentManagerGUI(manager)

    SwingUtilities.invokeLater {
        gui.isVisible = true
    }
}
