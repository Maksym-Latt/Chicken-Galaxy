package com.manacode.chickengalaxy.ui.main.gamescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.max
import kotlin.random.Random

class GameViewModel : ViewModel() {

    private val random = Random(System.currentTimeMillis())

    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<GameEvent>()
    val events: SharedFlow<GameEvent> = _events.asSharedFlow()

    private var tickJob: Job? = null
    private var entityId: Long = 0L
    private var enemyCooldown = 1.2f
    private var eggCooldown = 2.5f
    private var elapsedSecondsFraction = 0f

    init {
        startTickLoop()
    }

    fun showIntroOnEnter() {
        _state.value = initialState()
    }

    fun startRun() {
        entityId = 0
        enemyCooldown = 1.2f
        eggCooldown = 2.5f
        elapsedSecondsFraction = 0f
        _state.update {
            initialState().copy(
                phase = GamePhase.Running,
                stars = it.stars.ifEmpty { generateStars() }
            )
        }
    }

    fun playAgain() {
        startRun()
    }

    fun pause() {
        _state.update {
            if (it.phase == GamePhase.Running) it.copy(phase = GamePhase.Paused) else it
        }
    }

    fun resume() {
        _state.update {
            if (it.phase == GamePhase.Paused) it.copy(phase = GamePhase.Running) else it
        }
    }

    fun exitToMenu() {
        _state.value = initialState()
    }

    fun movePlayerBy(deltaX: Float, deltaY: Float) {
        _state.update { state ->
            if (!state.phase.isControllable()) return@update state
            val newX = (state.playerX + deltaX).coerceIn(0.08f, 0.92f)
            val newY = (state.playerY + deltaY).coerceIn(0.2f, 0.95f)
            state.copy(playerX = newX, playerY = newY)
        }
    }

    fun fire() {
        _state.update { state ->
            if (!state.phase.isControllable() || state.energy < 0.15f) return@update state
            val bullet = GameEntity(
                id = nextId(),
                kind = EntityKind.Bullet,
                x = state.playerX,
                y = state.playerY - state.playerSize * 0.6f,
                size = 0.035f,
                speed = 1.4f
            )
            state.copy(
                bullets = state.bullets + bullet,
                energy = (state.energy - 0.15f).coerceAtLeast(0f)
            )
        }
    }

    private fun startTickLoop() {
        tickJob?.cancel()
        tickJob = viewModelScope.launch {
            while (true) {
                delay(16L)
                applyTick(0.016f)
            }
        }
    }

    private fun applyTick(delta: Float) {
        var gameOverResult: GameResult? = null
        _state.update { state ->
            if (state.phase != GamePhase.Running) return@update state.copy(stars = updateStars(state.stars, delta))

            val updated = step(state, delta)
            gameOverResult = updated.result.takeIf { updated.phase == GamePhase.Result }
            updated
        }
        gameOverResult?.let { result ->
            viewModelScope.launch { _events.emit(GameEvent.GameOver(result)) }
        }
    }

    private fun step(state: GameUiState, delta: Float): GameUiState {
        val stars = updateStars(state.stars, delta)

        var energy = (state.energy + delta * 0.12f).coerceAtMost(1f)
        var score = state.score + (delta * 12f).toInt()
        var bonusEggs = state.bonusEggs
        var enemiesDown = state.enemiesDown
        var lives = state.lives

        val bullets = state.bullets
            .map { it.copy(y = it.y - it.speed * delta) }
            .filter { it.y > -0.1f }
            .toMutableList()

        val enemies = state.enemies
            .map { it.copy(y = it.y + it.speed * delta) }
            .filter { it.y < 1.1f }
            .toMutableList()

        val eggs = state.eggs
            .map { it.copy(y = it.y + it.speed * delta) }
            .filter { it.y < 1.1f }
            .toMutableList()

        // Bullet collisions
        val remainingBullets = mutableListOf<GameEntity>()
        bullets.forEach { bullet ->
            var destroyed = false
            val iterator = enemies.listIterator()
            while (iterator.hasNext()) {
                val enemy = iterator.next()
                if (collides(bullet, enemy)) {
                    iterator.remove()
                    destroyed = true
                    score += 90
                    enemiesDown += 1
                    energy = (energy + 0.05f).coerceAtMost(1f)
                    break
                }
            }
            if (!destroyed) {
                remainingBullets += bullet
            }
        }

        // Player collisions
        val remainingEnemies = mutableListOf<GameEntity>()
        val playerEntity = GameEntity(
            id = -1,
            kind = EntityKind.Player,
            x = state.playerX,
            y = state.playerY,
            size = state.playerSize,
            speed = 0f
        )
        enemies.forEach { enemy ->
            val reachedBottom = enemy.y >= 0.98f
            if (reachedBottom || collides(enemy, playerEntity)) {
                lives = max(0, lives - 1)
            } else {
                remainingEnemies += enemy
            }
        }

        val remainingEggs = mutableListOf<GameEntity>()
        eggs.forEach { egg ->
            if (collides(egg, playerEntity)) {
                bonusEggs += 1
                score += 120
                energy = (energy + 0.18f).coerceAtMost(1f)
            } else {
                remainingEggs += egg
            }
        }

        // Spawn new enemies and eggs
        enemyCooldown -= delta
        if (enemyCooldown <= 0f) {
            val speed = 0.25f + state.timeSeconds * 0.003f
            val spawn = GameEntity(
                id = nextId(),
                kind = EntityKind.Enemy,
                x = random.nextFloat().coerceIn(0.1f, 0.9f),
                y = -0.12f,
                size = random.nextFloat().coerceIn(0.12f, 0.18f),
                speed = speed.coerceAtMost(0.45f)
            )
            remainingEnemies += spawn
            enemyCooldown = (1.1f - state.timeSeconds * 0.02f).coerceIn(0.35f, 1.0f)
        }

        eggCooldown -= delta
        if (eggCooldown <= 0f) {
            val spawn = GameEntity(
                id = nextId(),
                kind = EntityKind.Egg,
                x = random.nextFloat().coerceIn(0.1f, 0.9f),
                y = -0.15f,
                size = 0.08f,
                speed = 0.18f
            )
            remainingEggs += spawn
            eggCooldown = random.nextDouble(2.5, 4.5).toFloat()
        }

        elapsedSecondsFraction += delta
        var timeSeconds = state.timeSeconds
        while (elapsedSecondsFraction >= 1f) {
            timeSeconds += 1
            elapsedSecondsFraction -= 1f
        }

        val runningState = state.copy(
            stars = stars,
            score = score,
            timeSeconds = timeSeconds,
            energy = energy,
            bonusEggs = bonusEggs,
            enemiesDown = enemiesDown,
            lives = lives,
            bullets = remainingBullets,
            enemies = remainingEnemies,
            eggs = remainingEggs
        )

        return if (lives <= 0) {
            runningState.copy(
                phase = GamePhase.Result,
                result = GameResult(
                    score = score,
                    timeSeconds = timeSeconds,
                    bonusEggs = bonusEggs,
                    enemiesDown = enemiesDown
                )
            )
        } else {
            runningState
        }
    }

    private fun nextId(): Long = ++entityId

    private fun collides(a: GameEntity, b: GameEntity): Boolean {
        val dx = a.x - b.x
        val dy = a.y - b.y
        val distance = hypot(dx, dy)
        val radius = (a.size + b.size) * 0.5f
        return distance < radius
    }

    private fun updateStars(stars: List<Star>, delta: Float): List<Star> =
        stars.map {
            var y = it.y + it.speed * delta
            if (y > 1f) y -= 1f
            it.copy(y = y)
        }

    private fun initialState(): GameUiState = GameUiState(
        phase = GamePhase.Intro,
        stars = generateStars()
    )

    private fun generateStars(count: Int = 70): List<Star> =
        List(count) { index ->
            Star(
                id = index,
                x = random.nextFloat(),
                y = random.nextFloat(),
                size = random.nextFloat().coerceIn(0.003f, 0.012f),
                speed = random.nextFloat().coerceIn(0.04f, 0.12f)
            )
        }
}

data class GameUiState(
    val phase: GamePhase = GamePhase.Intro,
    val score: Int = 0,
    val timeSeconds: Int = 0,
    val lives: Int = 3,
    val energy: Float = 1f,
    val bonusEggs: Int = 0,
    val enemiesDown: Int = 0,
    val playerX: Float = 0.5f,
    val playerY: Float = 0.8f,
    val playerSize: Float = 0.18f,
    val enemies: List<GameEntity> = emptyList(),
    val bullets: List<GameEntity> = emptyList(),
    val eggs: List<GameEntity> = emptyList(),
    val stars: List<Star> = emptyList(),
    val result: GameResult? = null
)

enum class GamePhase { Intro, Running, Paused, Result }

val GamePhase.isIntro: Boolean get() = this == GamePhase.Intro
val GamePhase.isPaused: Boolean get() = this == GamePhase.Paused
val GamePhase.isResult: Boolean get() = this == GamePhase.Result

private fun GamePhase.isControllable(): Boolean = this == GamePhase.Running

sealed interface GameEvent {
    data class GameOver(val result: GameResult) : GameEvent
}

data class GameResult(
    val score: Int,
    val timeSeconds: Int,
    val bonusEggs: Int,
    val enemiesDown: Int
)

data class GameEntity(
    val id: Long,
    val kind: EntityKind,
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float
)

enum class EntityKind { Player, Enemy, Bullet, Egg }

data class Star(
    val id: Int,
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float
)
