package esm.aoc.y15.d22;

import java.util.List;
import java.util.stream.Collectors;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class GameState {
    private final boolean hard;
    private final Wizard wizard;
    private final Boss boss;
    private final int turnNumber;
    private final int manaTotal;

    public GameState(final boolean hard, final Wizard wizard, final Boss boss, final int turnNumber, final int manaTotal) {
        this.hard = hard;
        this.wizard = wizard;
        this.boss = boss;
        this.turnNumber = turnNumber;
        this.manaTotal = manaTotal;
    }

    public List<GameState> getNextStates() {
        if (isGameOver()) {
            return emptyList();
        }
        final int type = turnNumber % 6;

        // 0: (if hard) -1 hit point
        // 1: Play effects / decrement counters
        // 2: Wizard plays action
        // 3: (if hard) - 1 hit point
        // 4: Play effects / decrement counters
        // 5: Boss attacks
        return switch (type) {
            case 0, 3 -> singletonList(new GameState(
                    hard,
                    hard ? decrementHitPoints() : wizard,
                    boss,
                    turnNumber + 1,
                    manaTotal
            ));
            case 1, 4 -> singletonList(new GameState(
                    hard,
                    playEffectsOnWizard(),
                    playEffectsOnBoss(),
                    turnNumber + 1,
                    manaTotal
            ));
            case 2 -> wizard.getPlayableActions().stream().map(action ->
                    new GameState(
                            hard,
                            playsActionOnWizard(action),
                            playsActionOnBoss(action),
                            turnNumber + 1,
                            manaTotal + action.getCost()
                    )
            ).collect(Collectors.toList());
            case 5 -> singletonList(new GameState(
                    hard,
                    bossAttacks(),
                    boss,
                    turnNumber + 1,
                    manaTotal
            ));
            default -> throw new IllegalStateException();
        };
    }

    private boolean isGameOver() {
        return isBossDead()|| wizard.isDead();
    }

    public boolean isBossDead() {
        return boss.isDead();
    }

    private Wizard bossAttacks() {
        return new Wizard(
                wizard.getMana(),
                wizard.getHitPoints() + wizard.getArmor() - boss.getStrength(),
                wizard.getShieldEffect(),
                wizard.getPoisonEffect(),
                wizard.getRechargeEffect()
        );
    }

    public Wizard playsActionOnWizard(final Action action) {
        return new Wizard(
                wizard.getMana() - action.getCost(),
                wizard.getHitPoints() + (action == Action.DRAIN ? 2 : 0),
                action == Action.SHIELD ? 6 : wizard.getShieldEffect(),
                action == Action.POISON ? 6 : wizard.getPoisonEffect(),
                action == Action.RECHARGE ? 5 : wizard.getRechargeEffect()
        );
    }

    public Boss playsActionOnBoss(final Action action) {
        return new Boss(
                boss.getHitPoints() - action.getImmediateDamage(),
                boss.getStrength()
        );
    }

    public Wizard playEffectsOnWizard() {
        return new Wizard(
                wizard.getMana() + (wizard.getRechargeEffect() > 0 ? 101 : 0),
                wizard.getHitPoints(),
                Math.max(0, wizard.getShieldEffect() - 1),
                Math.max(0, wizard.getPoisonEffect() - 1),
                Math.max(0, wizard.getRechargeEffect() - 1)
        );
    }

    public Wizard decrementHitPoints() {
        return new Wizard(
                wizard.getMana(),
                wizard.getHitPoints() - 1,
                wizard.getShieldEffect(),
                wizard.getPoisonEffect(),
                wizard.getRechargeEffect()
        );
    }

    public Boss playEffectsOnBoss() {
        return new Boss(
                boss.getHitPoints() - (wizard.getPoisonEffect() > 0 ? 3 : 0),
                boss.getStrength()
        );
    }

    public int getManaTotal() {
        return manaTotal;
    }

    @Override
    public String toString() {
        return String.format("%s: (%s) (%s) %s", turnNumber, wizard, boss, manaTotal);
    }

}