package tasklist

import kotlinx.datetime.*
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class Task(
    private var text: String = "",
    private var taskPriority: String = "",
    private var day: LocalDate? = null,
    private var time: LocalTime? = null,
    // изм
    private var shelfDate: String = ""
) {

    private val tasks = mutableListOf<Task>()

    private fun createTaskList(taskPriority: String, day: LocalDate, time: LocalTime) {
        var taskText = ""

        while (true) {

            val textInput = readln().trim()

            if (textInput.isBlank() && taskText.isNotBlank()) {

                tasks.add(Task(taskText, taskPriority, day, time))
                break
            } else if (textInput.isBlank() && taskText.isBlank()) {
                println("The task is blank")
                break
            }

            taskText += if (taskText.isBlank()) textInput else "\n   $textInput"
        }
    }

    private fun chooseTaskPriority(): String {
        while (true) {
            println("Input the task priority (C, H, N, L):")
            val input = readln().trim().uppercase()
            try {
                val taskPriority = TaskPriority.valueOf(input)
                return taskPriority.toString()
            } catch (e: IllegalArgumentException) {
                continue
            }
        }
    }

    private fun chooseDay(): LocalDate {
        DateTimeFormatter.ofPattern("yyyy-MM-dd")

        while (true) {
            print("Input the date (yyyy-mm-dd):")
            val input = readln()

            try {
                // Проверяем, является ли дата допустимой
                val (year, month, day) = input.split("-").map { it.toInt() }
                val yearMonth = YearMonth.of(year, month)
                val date = LocalDate.of(year, month, day)
                if (date.isAfter(yearMonth.atEndOfMonth()) || date.isBefore(yearMonth.atDay(1))) {
                    println("The input date is invalid")
                    continue
                }

                return date
            } catch (e: DateTimeParseException) {
                println("The input date is invalid")
                continue
            } catch (e: DateTimeException) {
                println("The input date is invalid")
                continue
            } catch (e: NumberFormatException) {
                println("The input date is invalid")
                continue
            }
        }

    }

    private fun chooseTime(): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        var time: LocalTime?

        while (true) {
            print("Input the time (hh:mm):")
            val input = readln()

            try {
                val timeParts = input.split(":")
                val hour = timeParts[0].toInt()
                val minute = timeParts[1].toInt()
                if (hour in 0..23 && minute in 0..59) {
                    val formattedInput = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
                    time = LocalTime.parse(formattedInput, formatter)
                    return time
                } else println("The input time is invalid")
            } catch (e: DateTimeParseException) {
                println("The input time is invalid")
                continue
            } catch (e: NumberFormatException) {
                println("The input time is invalid")
                continue
            } catch (e: IndexOutOfBoundsException) {
                println("The input time is invalid")
                continue
            }
        }

    }

    private fun setText(): String {
        var taskText = ""
        println("Input a new task (enter a blank line to end):")
        while (true) {

            val textInput = readln().trim()

            if (textInput.isBlank() && taskText.isNotBlank()) {
                return taskText
            } else if (textInput.isBlank() && taskText.isBlank()) {
                println("The task is blank")
                break
            }

            taskText += if (taskText.isBlank()) textInput else "\n   $textInput"
        }
        return ""
    }

    // TODO Доделать функцию

    private fun setShelfDate(task: Task): String {
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date

        val numberOfDays = task.day?.let { currentDate.daysUntil(it.toKotlinLocalDate()) }
        if (numberOfDays != null) {
            return (when {
                numberOfDays == 0 -> "T"
                numberOfDays > 0 -> "I"
                else -> "O"
            })
        }
        return ""
    }

    private fun printTasks(tasks: MutableList<Task>) {
        tasks.forEachIndexed { index, task ->
            println(
                "${index + 1}".padEnd(
                    3,
                    ' '
                ) + "${task.day} ${task.time} ${task.taskPriority} ${setShelfDate(task)}" + "\n   " + task + "\n"
            )
        }
    }

    private fun printTaskList() {
        if (tasks.isEmpty()) {
            println("No tasks have been input")
        } else printTasks(tasks)
    }

    private fun deleteTask() {
        printTasks(tasks)
        while (true) {
            if (tasks.isNotEmpty()) {
                try {

                    println("Input the task number (1-${tasks.size}):")
                    val input = readln().trim().toInt()
                    tasks.removeAt(input - 1)
                    println("The task is deleted")
                    break
                } catch (e: IllegalArgumentException) {
                    println("Invalid task number")
                    continue
                } catch (e: IndexOutOfBoundsException) {
                    println("Invalid task number")
                    continue
                }
            } else {
                println("No tasks have been input")
                break
            }
        }

    }

    private fun editTask() {
        printTasks(tasks)
        while (true) {
            if (tasks.isNotEmpty()) {
                try {
                    println("Input the task number (1-${tasks.size}):")

                    val input = readln().trim().toInt()

                    changeTaskField(tasks[input - 1])
                    println("The task is changed")
                    break

                } catch (e: IllegalArgumentException) {
                    println("Invalid task number")
                    continue
                } catch (e: IndexOutOfBoundsException) {
                    println("Invalid task number")
                    continue
                }

            } else {
                println("No tasks have been input")
                break
            }
        }

    }

    private fun changeTaskField(task: Task) {
        while (true) {
            println("Input a field to edit (priority, date, time, task):")
            when (readln()) {
                "priority" -> {
                    task.taskPriority = task.chooseTaskPriority()
                    break
                }

                "date" -> {
                    task.day = task.chooseDay()
                    break
                }

                "time" -> {
                    task.time = task.chooseTime()
                    break
                }

                "task" -> {
                    task.text = task.setText()
                    break
                }

                else -> {
                    println("Invalid field")
                    continue
                }
            }
        }
    }

    fun chooseAction(task: Task) {
        while (true) {

            println("Input an action (add, print, edit, delete, end):")
            when (readln()) {

                "add" -> {
                    val taskPriorityInput = task.chooseTaskPriority()
                    val dayInput = task.chooseDay()
                    val timeInput = task.chooseTime()

                    println("Input a new task (enter a blank line to end):")
                    task.createTaskList(taskPriorityInput, dayInput, timeInput)
                }

                "print" -> task.printTaskList()

                "end" -> {
                    println("Tasklist exiting!")
                    break
                }

                "edit" -> {
                    editTask()
                }

                "delete" -> {
                    deleteTask()
                }

                else -> {
                    println("The input action is invalid")
                    continue
                }
            }
        }
    }

    override fun toString(): String {
        return text
    }
}
//TODO Сделать чтобы пустой ввод не добавлялся


fun main() {
    val task = Task()
    task.chooseAction(task)

}