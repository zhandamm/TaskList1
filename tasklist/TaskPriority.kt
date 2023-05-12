package tasklist

enum class TaskPriority(val color: String) {
    C(Color.RED.color),
    H(Color.YELLOW.color),
    N(Color.GREEN.color),
    L(Color.BLUE.color)
}