package tasklist

import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class Task(
        private var text: String = "",
        val taskPriority: String = "",
        val day: LocalDate? = null,
        val time: LocalTime? = null,
) {

    private val tasks = mutableListOf<Task>()

    fun createTaskList(taskPriority: String, day: LocalDate, time: LocalTime) {
        var taskText = ""

        while (true) {

            val textInput = readln().trim()

            if (textInput.isBlank() && taskText.isNotBlank()) {
                //TODO добавить taskPriority и date time
                tasks.add(Task(taskText, taskPriority, day, time))
                break
            } else if (textInput.isBlank() && taskText.isBlank()) {
                println("The task is blank")
                break
            }

            taskText += if (taskText.isBlank()) textInput else "\n   $textInput"
        }
    }

    fun chooseDay(): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        var date: LocalDate

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
            }
            catch (e: NumberFormatException){
                println("The input date is invalid")
                continue
            }
        }

    }

    fun chooseTime(): LocalTime {
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
            } catch (e: NumberFormatException) {
                println("The input time is invalid")

            }
        }

    }

    fun chooseTaskPriority(): String {
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

    fun printTaskList() {
        if (tasks.isEmpty()) {
            println("No tasks have been input")
        } else printTasks(tasks)
    }

    override fun toString(): String {
        return text
    }
}
//TODO Сделать чтобы пустой ввод не добавлялся

fun chooseAction(task: Task) {
    while (true) {
        println("Input an action (add, print, end):")
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

            else -> {
                println("The input action is invalid")
                continue
            }
        }
    }
}

fun printTasks(tasks: MutableList<Task>) {
    tasks.forEachIndexed { index, task ->
        println("${index + 1}".padEnd(3, ' ') + "${task.day} ${task.time} ${task.taskPriority}" + "\n   " + task + "\n")
    }
}

fun main() {
    val task = Task()
    chooseAction(task)

}