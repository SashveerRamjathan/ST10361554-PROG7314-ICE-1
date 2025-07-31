package com.st10361554.prog7314_ice_task_1_snake.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random
import androidx.core.graphics.toColorInt

enum class Direction { UP, DOWN, LEFT, RIGHT }

class SnakeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var cellSize = 0
    private val numCells = 30 // Modern grid!
    private val snake = mutableListOf<Pair<Int, Int>>()
    private var direction = Direction.RIGHT
    private var food = Pair(numCells / 2, numCells / 2)
    private var score = 0
    private var running = true

    @Suppress("DEPRECATION")
    private val handler = Handler()
    private val gameRunnable = object : Runnable {
        override fun run() {
            if (running) {
                update()
                invalidate()
                handler.postDelayed(this, 120)
            }
        }
    }

    // Colors from resources for professional look
    private val snakePaint = Paint().apply { color = "#00C853".toColorInt() }
    private val foodPaint = Paint().apply { color = "#FF5252".toColorInt() }
    private val gridPaint = Paint().apply {
        color = "#E0E0E0".toColorInt()
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private var gameOverListener: ((Int) -> Unit)? = null

    init {
        resetGame()
        handler.post(gameRunnable)
    }

    fun setGameOverListener(listener: (Int) -> Unit) {
        gameOverListener = listener
    }

    fun getScore(): Int = score

    fun changeDirection(newDir: Direction) {
        if ((direction == Direction.UP && newDir == Direction.DOWN) ||
            (direction == Direction.DOWN && newDir == Direction.UP) ||
            (direction == Direction.LEFT && newDir == Direction.RIGHT) ||
            (direction == Direction.RIGHT && newDir == Direction.LEFT)
        ) return
        direction = newDir
    }

    fun pauseGame() {
        running = false
    }

    fun resumeGame() {
        if (!running) {
            running = true
            handler.post(gameRunnable)
        }
    }

    private fun resetGame() {
        snake.clear()
        snake.add(Pair(numCells / 2, numCells / 2))
        direction = Direction.RIGHT
        score = 0
        spawnFood()
        running = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cellSize = minOf(w, h) / numCells
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw only the outer edge lines of the grid
        val boardSize = numCells * cellSize
        // Top
        canvas.drawLine(0f, 0f, boardSize.toFloat(), 0f, gridPaint)
        // Bottom
        canvas.drawLine(0f, boardSize.toFloat(), boardSize.toFloat(), boardSize.toFloat(), gridPaint)
        // Left
        canvas.drawLine(0f, 0f, 0f, boardSize.toFloat(), gridPaint)
        // Right
        canvas.drawLine(boardSize.toFloat(), 0f, boardSize.toFloat(), boardSize.toFloat(), gridPaint)

        // Draw Snake
        for (segment in snake) {
            canvas.drawRoundRect(
                segment.first * cellSize.toFloat(),
                segment.second * cellSize.toFloat(),
                (segment.first + 1) * cellSize.toFloat(),
                (segment.second + 1) * cellSize.toFloat(),
                cellSize * 0.3f, cellSize * 0.3f, snakePaint
            )
        }
        // Draw Food as a circle
        canvas.drawCircle(
            food.first * cellSize + cellSize / 2f,
            food.second * cellSize + cellSize / 2f,
            cellSize * 0.4f, foodPaint
        )
    }

    private fun update() {
        if (!running) return
        val head = snake.first()
        val newHead = when (direction) {
            Direction.UP -> Pair(head.first, head.second - 1)
            Direction.DOWN -> Pair(head.first, head.second + 1)
            Direction.LEFT -> Pair(head.first - 1, head.second)
            Direction.RIGHT -> Pair(head.first + 1, head.second)
        }
        if (newHead.first !in 0 until numCells || newHead.second !in 0 until numCells ||
            snake.contains(newHead)
        ) {
            running = false
            gameOverListener?.invoke(score)
            return
        }
        snake.add(0, newHead)
        if (newHead == food) {
            score++
            spawnFood()
        } else {
            snake.removeAt(snake.lastIndex)
        }
    }

    private fun spawnFood() {
        var position: Pair<Int, Int>
        do {
            position = Pair(Random.nextInt(numCells), Random.nextInt(numCells))
        } while (snake.contains(position))
        food = position
    }
}