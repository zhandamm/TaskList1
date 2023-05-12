package tasklist

import kotlinx.datetime.*
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class Task(

    private var taskPriority: String = "",
    private var day: LocalDate? = null,
    private var time: LocalTime? = null,
    private var taskText: String = "",
) {

    private val tasks = mutableListOf<Task>()

    private fun createTaskList(taskPriority: String, day: LocalDate, time: LocalTime, taskText: String) {

        tasks.add(Task(taskPriority, day, time, taskText))

    }

    private fun deleteTask() {

        while (true) {
            if (tasks.isNotEmpty()) {
                try {
                    printList(tasks)
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

        while (true) {
            if (tasks.isNotEmpty()) {
                try {
                    printList(tasks)
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
                    task.taskText = task.setText()
                    break
                }

                else -> {
                    println("Invalid field")
                    continue
                }
            }
        }
    }

    private fun chooseTaskPriority(): String {
        while (true) {
            println("Input the task priority (C, H, N, L):")
            val input = readln().trim().uppercase()
            try {
                return when (input) {
                    "C" -> "\u001B[101m \u001B[0m"
                    "H" -> "\u001B[103m \u001B[0m"
                    "N" -> "\u001B[102m \u001B[0m"
                    "L" -> "\u001B[104m \u001B[0m"
                    else -> continue
                }
            } catch (e: IllegalArgumentException) {
                continue
            }
        }
    }

    private fun setShelfDate(task: Task): String {
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date

        val numberOfDays = task.day?.let { currentDate.daysUntil(it.toKotlinLocalDate()) }
        if (numberOfDays != null) {
            return (when {
                numberOfDays == 0 -> Color.GREEN.color
                numberOfDays > 0 -> Color.YELLOW.color
                else -> Color.RED.color
            })
        }
        return ""
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

            taskText += if (taskText.isBlank()) textInput else "\n$textInput"
        }
        return ""
    }

    fun chooseAction(task: Task) {
        while (true) {

            println("Input an action (add, print, edit, delete, end):")
            when (readln()) {

                "add" -> {
                    val taskPriorityInput = task.chooseTaskPriority()
                    val dayInput = task.chooseDay()
                    val timeInput = task.chooseTime()
                    val taskText = task.setText()
                    task.createTaskList(taskPriorityInput, dayInput, timeInput, taskText)
                }

                "print" -> task.printList(tasks)

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
        return taskText
    }

    private fun taskToLust(string: String): MutableList<String> {
        val listTask = mutableListOf<String>()
        var i = 0
        var str = ""
        for (ch in string) {
            if (ch == '\n') {
                listTask += str
                i = 0
                str = ""
            } else if (i == 44) {
                listTask += str
                i = 1
                str = ch.toString()
            } else {
                str += ch
                i++
            }
        }
        listTask += str
        if (listTask.size > 1) {
            for (j in 1..listTask.lastIndex) {
                listTask[j] = "|    |            |       |   |   |${listTask[j]}${" ".repeat(44 - listTask[j].length)}|"
            }
        }
        return listTask
    }

    private fun printList(task: MutableList<Task>) {
        if (task.isEmpty()) {
            println("No tasks have been input")
            return
        }



        println(
            "+----+------------+-------+---+---+--------------------------------------------+\n" +
                    "| N  |    Date    | Time  | P | D |                   Task                     |\n" +
                    "+----+------------+-------+---+---+--------------------------------------------+"
        )

        for (i in 1..task.size) {
            val listTask = taskToLust(task[i - 1].toString())
            println(
                "| $i  | ${task[i - 1].day} | ${task[i - 1].time} | ${task[i - 1].taskPriority} | ${
                    task[i - 1].setShelfDate(
                        task[i - 1]
                    )
                } |${listTask[0]}${" ".repeat(44 - listTask[0].length)}|"
            )
            if (listTask.size > 1) {
                for (j in 1..listTask.lastIndex) {
                    println(listTask[j])
                }
            }
            println("+----+------------+-------+---+---+--------------------------------------------+")
        }
    }
}


fun main() {
    val task = Task()
    task.chooseAction(task)

}