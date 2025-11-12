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

private const val EXPLOSION_DURATION = 0.6f

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
    private var enemyShotCooldown = 1.8f
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
        enemyShotCooldown = 1.8f
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
        var fired = false
        _state.update { state ->
            if (!state.phase.isControllable() || state.energy < 0.15f) return@update state
            fired = true
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
        if (fired) {
            viewModelScope.launch { _events.emit(GameEvent.PlayerShot) }
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
        val emittedEvents = mutableListOf<GameEvent>()
        _state.update { state ->
            if (state.phase != GamePhase.Running) {
                return@update state.copy(stars = updateStars(state.stars, delta))
            }

            val outcome = step(state, delta)
            emittedEvents += outcome.events
            outcome.state
        }
        if (emittedEvents.isNotEmpty()) {
            viewModelScope.launch {
                emittedEvents.forEach { event -> _events.emit(event) }
            }
        }
    }

    private data class StepOutcome(
        val state: GameUiState,
        val events: List<GameEvent>
    )

    private fun step(state: GameUiState, delta: Float): StepOutcome {
        val stars = updateStars(state.stars, delta)

        var energy = (state.energy + delta * 0.12f).coerceAtMost(1f)
        var score = state.score + (delta * 12f).toInt()
        var bonusEggs = state.bonusEggs
        var enemiesDown = state.enemiesDown
        var lives = state.lives

        val events = mutableListOf<GameEvent>()

        val explosions = state.explosions
            .map { it.copy(age = it.age + delta) }
            .filter { it.age < EXPLOSION_DURATION }
            .toMutableList()

        val bullets = state.bullets
            .map { bullet ->
                val newX = bullet.x + bullet.horizontalSpeed * delta
                val newY = bullet.y - bullet.speed * delta
                bullet.copy(x = newX, y = newY)
            }
            .filter { it.y > -0.1f && it.x > -0.2f && it.x < 1.2f }
            .toMutableList()

        val enemyBullets = state.enemyBullets
            .map { bullet ->
                val newX = bullet.x + bullet.horizontalSpeed * delta
                val newY = bullet.y + bullet.speed * delta
                bullet.copy(x = newX, y = newY)
            }
            .filter { it.y < 1.1f && it.x > -0.2f && it.x < 1.2f }
            .toMutableList()

        val enemies = state.enemies
            .map { enemy ->
                var horizontalSpeed = enemy.horizontalSpeed
                var newX = enemy.x + horizontalSpeed * delta
                if (newX < 0.08f || newX > 0.92f) {
                    horizontalSpeed = -horizontalSpeed
                    newX = newX.coerceIn(0.08f, 0.92f)
                }
                enemy.copy(
                    x = newX,
                    y = enemy.y + enemy.speed * delta,
                    horizontalSpeed = horizontalSpeed
                )
            }
            .filter { it.y < 1.1f }
            .toMutableList()

        val eggs = state.eggs
            .map { it.copy(y = it.y + it.speed * delta) }
            .filter { it.y < 1.1f }
            .toMutableList()

        // Bullet collisions
        val remainingBullets = mutableListOf<GameEntity>()
        val remainingEnemyBullets = mutableListOf<GameEntity>()
        bullets.forEach { bullet ->
            var destroyed = false
            val iterator = enemies.listIterator()
            while (iterator.hasNext()) {
                val enemy = iterator.next()
                if (collides(bullet, enemy)) {
                    iterator.remove()
                    explosions += Explosion(
                        id = nextId(),
                        x = enemy.x,
                        y = enemy.y,
                        size = enemy.size
                    )
                    destroyed = true
                    score += 90
                    enemiesDown += 1
                    energy = (energy + 0.05f).coerceAtMost(1f)
                    events += GameEvent.EnemyDestroyed
                    break
                }
            }
            if (!destroyed) {
                val enemyBulletIterator = enemyBullets.listIterator()
                while (enemyBulletIterator.hasNext()) {
                    val enemyBullet = enemyBulletIterator.next()
                    if (collides(bullet, enemyBullet)) {
                        enemyBulletIterator.remove()
                        destroyed = true
                        score += 30
                        energy = (energy + 0.02f).coerceAtMost(1f)
                        break
                    }
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
            speed = 0f,
            horizontalSpeed = 0f
        )
        enemies.forEach { enemy ->
            val reachedBottom = enemy.y >= 0.98f
            if (reachedBottom || collides(enemy, playerEntity)) {
                val newLives = max(0, lives - 1)
                if (newLives < lives) {
                    events += GameEvent.PlayerHit
                }
                lives = newLives
            } else {
                remainingEnemies += enemy
            }
        }

        enemyBullets.forEach { bullet ->
            if (collides(bullet, playerEntity)) {
                val newLives = max(0, lives - 1)
                if (newLives < lives) {
                    events += GameEvent.PlayerHit
                }
                lives = newLives
            } else {
                remainingEnemyBullets += bullet
            }
        }

        val remainingEggs = mutableListOf<GameEntity>()
        eggs.forEach { egg ->
            if (collides(egg, playerEntity)) {
                bonusEggs += 1
                score += 120
                energy = (energy + 0.18f).coerceAtMost(1f)
                events += GameEvent.EggCollected
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
                speed = speed.coerceAtMost(0.45f),
                horizontalSpeed = (random.nextFloat() - 0.5f) * 0.45f
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

        enemyShotCooldown -= delta
        if (enemyShotCooldown <= 0f && remainingEnemies.isNotEmpty()) {
            val shooter = remainingEnemies.random(random)
            val horizontalSpeed = ((state.playerX - shooter.x) * 0.9f).coerceIn(-0.4f, 0.4f)
            val bulletSpeed = (0.6f + state.timeSeconds * 0.003f).coerceAtMost(0.95f)
            val spawn = GameEntity(
                id = nextId(),
                kind = EntityKind.EnemyBullet,
                x = shooter.x,
                y = shooter.y + shooter.size * 0.6f,
                size = 0.03f,
                speed = bulletSpeed,
                horizontalSpeed = horizontalSpeed
            )
            remainingEnemyBullets += spawn
            enemyShotCooldown = random.nextDouble(0.8, 1.6).toFloat().coerceAtLeast(0.45f)
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
            enemyBullets = remainingEnemyBullets,
            eggs = remainingEggs,
            explosions = explosions,
            result = null
        )

        return if (lives <= 0) {
            val result = GameResult(
                score = score,
                timeSeconds = timeSeconds,
                bonusEggs = bonusEggs,
                enemiesDown = enemiesDown
            )
            events += GameEvent.GameOver(result)
            StepOutcome(
                state = runningState.copy(
                    phase = GamePhase.Result,
                    result = result
                ),
                events = events
            )
        } else {
            StepOutcome(runningState, events)
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
    val enemyBullets: List<GameEntity> = emptyList(),
    val eggs: List<GameEntity> = emptyList(),
    val stars: List<Star> = emptyList(),
    val explosions: List<Explosion> = emptyList(),
    val result: GameResult? = null
)

enum class GamePhase { Intro, Running, Paused, Result }

val GamePhase.isIntro: Boolean get() = this == GamePhase.Intro
val GamePhase.isPaused: Boolean get() = this == GamePhase.Paused
val GamePhase.isResult: Boolean get() = this == GamePhase.Result

private fun GamePhase.isControllable(): Boolean = this == GamePhase.Running

sealed interface GameEvent {
    object PlayerShot : GameEvent
    object PlayerHit : GameEvent
    object EnemyDestroyed : GameEvent
    object EggCollected : GameEvent
    data class GameOver(val result: GameResult) : GameEvent
}

data class Explosion(
    val id: Long,
    val x: Float,
    val y: Float,
    val size: Float,
    val age: Float = 0f
) {
    val progress: Float get() = age / EXPLOSION_DURATION
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
    val speed: Float,
    val horizontalSpeed: Float = 0f
)

enum class EntityKind { Player, Enemy, Bullet, EnemyBullet, Egg }

data class Star(
    val id: Int,
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float
)
