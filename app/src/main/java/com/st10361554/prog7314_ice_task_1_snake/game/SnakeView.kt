package com.st10361554.prog7314_ice_task_1_snake.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random
import androidx.core.graphics.toColorInt

// Enum class representing the four possible directions the snake can move.
enum class Direction { UP, DOWN, LEFT, RIGHT }

/**
 * Custom View that implements the Snake game logic and rendering.
 * Handles game updates, snake movement, collision detection, food spawning, and drawing.
 */
class SnakeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var cellSize = 0                  // Size of each cell on the grid
    private val numCells = 30                 // Number of cells per row/column (creates a 30x30 grid)
    private val snake = mutableListOf<Pair<Int, Int>>() // List representing the snake's segments (as pairs of x,y coordinates)
    private var direction = Direction.RIGHT    // Current moving direction of the snake
    private var food = Pair(numCells / 2, numCells / 2) // Current position of the food
    private var score = 0                     // Player's current score
    private var running = true                // Whether the game is running or paused

    // Handler and Runnable for managing game loop/ticks
    @Suppress("DEPRECATION")
    private val handler = Handler()
    private val gameRunnable = object : Runnable {
        override fun run() {
            if (running) {
                update()        // Update game state (move snake, check collisions, etc)
                invalidate()    // Redraw the view
                handler.postDelayed(this, 120) // Run this again after 120ms
            }
        }
    }

    // Paint objects for drawing snake, food, and grid edges
    private val snakePaint = Paint().apply { color = "#00C853".toColorInt() } // Green for snake
    private val foodPaint = Paint().apply { color = "#FF5252".toColorInt() }  // Red for food
    private val gridPaint = Paint().apply {                                   // Gray for grid outline
        color = "#E0E0E0".toColorInt()
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    // Game-over callback, invoked with the final score
    private var gameOverListener: ((Int) -> Unit)? = null

    // Initialization block: resets the game and starts the game loop
    init {
        resetGame()
        handler.post(gameRunnable)
    }

    /**
     * Sets a listener to be invoked when the game is over.
     */
    fun setGameOverListener(listener: (Int) -> Unit) {
        gameOverListener = listener
    }

    /**
     * Returns the current score.
     */
    fun getScore(): Int = score

    /**
     * Changes the snake's moving direction unless the new direction is directly opposite.
     */
    fun changeDirection(newDir: Direction) {
        if ((direction == Direction.UP && newDir == Direction.DOWN) ||
            (direction == Direction.DOWN && newDir == Direction.UP) ||
            (direction == Direction.LEFT && newDir == Direction.RIGHT) ||
            (direction == Direction.RIGHT && newDir == Direction.LEFT)
        ) return // Ignore if trying to reverse directly
        direction = newDir
    }

    /**
     * Pauses the game.
     */
    fun pauseGame() {
        running = false
    }

    /**
     * Resumes the game if it was paused.
     */
    fun resumeGame() {
        if (!running) {
            running = true
            handler.post(gameRunnable)
        }
    }

    /**
     * Resets the game state: snake position, direction, score, and food.
     */
    private fun resetGame() {
        snake.clear()
        snake.add(Pair(numCells / 2, numCells / 2)) // Start snake in center
        direction = Direction.RIGHT
        score = 0
        spawnFood()
        running = true
    }

    /**
     * Called when the view size changes; calculates the cell size.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cellSize = minOf(w, h) / numCells
    }

    /**
     * Draws the grid, snake, and food.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw only the outer edge lines of the grid (board border)
        val boardSize = numCells * cellSize
        // Top border
        canvas.drawLine(0f, 0f, boardSize.toFloat(), 0f, gridPaint)
        // Bottom border
        canvas.drawLine(0f, boardSize.toFloat(), boardSize.toFloat(), boardSize.toFloat(), gridPaint)
        // Left border
        canvas.drawLine(0f, 0f, 0f, boardSize.toFloat(), gridPaint)
        // Right border
        canvas.drawLine(boardSize.toFloat(), 0f, boardSize.toFloat(), boardSize.toFloat(), gridPaint)

        // Draw each segment of the snake as a rounded rectangle
        for (segment in snake) {
            canvas.drawRoundRect(
                segment.first * cellSize.toFloat(),
                segment.second * cellSize.toFloat(),
                (segment.first + 1) * cellSize.toFloat(),
                (segment.second + 1) * cellSize.toFloat(),
                cellSize * 0.3f, cellSize * 0.3f, snakePaint
            )
        }
        // Draw the food as a circle
        canvas.drawCircle(
            food.first * cellSize + cellSize / 2f,
            food.second * cellSize + cellSize / 2f,
            cellSize * 0.4f, foodPaint
        )
    }

    /**
     * Updates the game state: moves the snake, checks for collisions and food, handles game over.
     */
    private fun update() {
        if (!running) return
        val head = snake.first()
        // Calculate the new head position based on direction
        val newHead = when (direction) {
            Direction.UP -> Pair(head.first, head.second - 1)
            Direction.DOWN -> Pair(head.first, head.second + 1)
            Direction.LEFT -> Pair(head.first - 1, head.second)
            Direction.RIGHT -> Pair(head.first + 1, head.second)
        }
        // Check if the snake hits the wall or itself
        if (newHead.first !in 0 until numCells || newHead.second !in 0 until numCells ||
            snake.contains(newHead)
        ) {
            running = false
            gameOverListener?.invoke(score) // Trigger game over
            return
        }
        // Insert new head at the front
        snake.add(0, newHead)
        if (newHead == food) {
            score++         // Increase score if snake eats food
            spawnFood()     // Spawn new food
        } else {
            snake.removeAt(snake.lastIndex) // Remove tail segment if no food eaten
        }
    }

    /**
     * Spawns food at a random position not occupied by the snake.
     */
    private fun spawnFood() {
        var position: Pair<Int, Int>
        do {
            position = Pair(Random.nextInt(numCells), Random.nextInt(numCells))
        } while (snake.contains(position)) // Ensure food doesn't spawn on the snake
        food = position
    }
}