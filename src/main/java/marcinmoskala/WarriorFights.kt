package marcinmoskala

/**
 * https://blog.kotlin-academy.com/inheritance-composition-delegation-and-traits-b11c64f11b27
 */

interface Fighter {
    var lifePoints: Int
    fun attack(opponent: Fighter)
    fun turnEnd() {}
}

data class Goblin(override var lifePoints: Int = 50) : Fighter {
    override fun attack(opponent: Fighter) {
        println("Goblin attack (5)")
        opponent.lifePoints -= 5
    }
}

interface Character : Fighter {
    val name: String
        get() = this.javaClass.simpleName
}

interface Warrior : Character {
    val strength: Int

    fun meleeAttack(opponent: Fighter) {
        println("$name melee attack with power $strength")
        opponent.lifePoints -= strength
    }
}

interface Sorcerer : Character {
    val spell: Spell
    var manaPoints: Int

    fun canCastSpell() = manaPoints > spell.manaCost

    fun castSpell(opponent: Fighter) {
        if (manaPoints < spell.manaCost) {
            println("$name tried to cast spell but not enough mana")
            return
        }
        println("$name cast spell ${spell.strength}")
        manaPoints -= spell.manaCost
        opponent.lifePoints -= spell.strength
    }

    override fun turnEnd() {
        manaPoints += 1
    }
}

data class Spell(val manaCost: Int, val strength: Int)

data class Minsk(
        override var lifePoints: Int = 60
) : Warrior {
    override val strength: Int = 15

    override fun attack(opponent: Fighter) {
        meleeAttack(opponent)
    }
}

data class Artur(
        override var lifePoints: Int = 80,
        override var manaPoints: Int = 10
) : Warrior, Sorcerer {
    override var spell = Spell(4, 17)
    override val strength: Int = 5

    override fun attack(opponent: Fighter) {
        if (canCastSpell()) {
            castSpell(opponent)
        } else {
            meleeAttack(opponent)
        }
    }
}

fun main(args: Array<String>) {
    simulateCombat(Artur(), Minsk())
    simulateCombat(Goblin(), Minsk())
}

fun simulateCombat(c1: Fighter, c2: Fighter) {
    while (c1.lifePoints > 0 && c2.lifePoints > 0) {
        c1.attack(c2)
        c2.attack(c1)
        c1.turnEnd()
        c2.turnEnd()
    }
    val text = when {
        c1.lifePoints > 0 -> "$c1 won"
        c2.lifePoints > 0 -> "$c2 won"
        else -> "Both $c1 and $c2 are dead"
    }
    println(text)
}




