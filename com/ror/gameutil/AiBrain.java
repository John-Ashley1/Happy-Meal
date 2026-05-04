package com.ror.gameutil;

import com.ror.gamemodel.Entity;
import com.ror.gamemodel.Skill;

import java.util.List;
import java.util.Random;


public class AiBrain {

    // ── tuneable thresholds ────────────────────────────────────────────────

    private static final double LOW_HP_THRESHOLD   = 0.30;   // 30 %


    private static final double LOW_MANA_THRESHOLD = 0.20;   // 20 %

    // ── state ─────────────────────────────────────────────────────────────
    private final AiDifficulty difficulty;
    private final Random        rng;

    // ── constructor ────────────────────────────────────────────────────────
    public AiBrain(AiDifficulty difficulty) {
        this.difficulty = difficulty;
        this.rng        = new Random();
    }

    // ── public API ─────────────────────────────────────────────────────────


    public int chooseSkill(Entity ai, Entity target) {
        List<Skill> skills = ai.getSkills();

        switch (difficulty) {
            case EASY:   return easyChoice(skills);
            case MEDIUM: return mediumChoice(ai, target, skills);
            default:     return easyChoice(skills);
        }
    }

    // ── EASY ───────────────────────────────────────────────────────────────


    private int easyChoice(List<Skill> skills) {
        // collect ready indices
        java.util.List<Integer> ready = new java.util.ArrayList<>();
        for (int i = 0; i < skills.size(); i++) {
            if (skills.get(i).isReady()) ready.add(i);
        }
        if (ready.isEmpty()) return -1;
        return ready.get(rng.nextInt(ready.size()));
    }

    // ── MEDIUM ─────────────────────────────────────────────────────────────


    private int mediumChoice(Entity ai, Entity target, List<Skill> skills) {
        double hpRatio   = (double) ai.getCurrentHealth() / ai.getMaxHealth();
        double manaRatio = (double) ai.getCurrentMana()   / ai.getMaxMana();


        if (hpRatio <= LOW_HP_THRESHOLD) {
            if (skills.size() > 2 && skills.get(2).isReady()) return 2;
        }


        if (manaRatio <= LOW_MANA_THRESHOLD) {
            if (skills.get(0).isReady()) return 0;
        }


        int    bestIdx   = -1;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < skills.size(); i++) {
            Skill s = skills.get(i);
            if (!s.isReady()) continue;

            double score = scoreSkill(i, ai, target, hpRatio, manaRatio);
            // add a small random jitter so the AI doesn't feel deterministic
            score += rng.nextDouble() * 5;

            if (score > bestScore) {
                bestScore = score;
                bestIdx   = i;
            }
        }
        return bestIdx; // -1 if nothing ready
    }


    private double scoreSkill(int index, Entity ai, Entity target,
                              double aiHpRatio, double manaRatio) {
        double score = 0;

        // prefer skills at higher indices (assumed stronger)
        score += index * 20;

        // aggression bonus when target is nearly dead
        double targetHpRatio = (double) target.getCurrentHealth() / target.getMaxHealth();
        if (targetHpRatio <= 0.25) score += 30;

        // slight penalty for using powerful skills when mana is plentiful
        // (simulate "saving" them, adds variety)
        if (manaRatio > 0.80 && index == 2) score -= 10;

        return score;
    }

    // ── helpers ────────────────────────────────────────────────────────────

    public AiDifficulty getDifficulty() { return difficulty; }
}