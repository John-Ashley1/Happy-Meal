package com.ror.gameutil;

import com.ror.gamemodel.Entity;
import com.ror.gamemodel.Skill;
import com.ror.gamemodel.Playable.*;

import java.util.*;
import java.util.concurrent.*;

public class ConsolePvPBattle {

    // COLOR CODES
    static final String RESET   = "\u001B[0m";
    static final String BOLD    = "\u001B[1m";
    static final String RED     = "\u001B[31m";
    static final String GREEN   = "\u001B[32m";
    static final String YELLOW  = "\u001B[33m";
    static final String BLUE    = "\u001B[34m";
    static final String MAGENTA = "\u001B[35m";
    static final String CYAN    = "\u001B[36m";
    static final String WHITE   = "\u001B[37m";

    // ─── STATUS EFFECT CONSTANTS
    static final String STATUS_NONE   = "NONE";
    static final String STATUS_POISON = "POISON";
    static final String STATUS_STUN   = "STUN";
    static final String STATUS_BURN   = "BURN";

    // ─── STATUS EFFECT TRACKING
    static Map<Entity, String> statusEffects   = new HashMap<>();
    static Map<Entity, Integer> statusDuration = new HashMap<>();

    // ─── TURN TIMER
    static final int TURN_TIME_SECONDS = 20;

    // ─── USER INPUT (shared for timer thread)
    static volatile int timedChoice = -1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BattleView view = new ConsoleBattleView();

        printBanner();

        //  Character Selection Phase
        Entity player1 = selectHero(scanner, "Player 1", CYAN);
        Entity player2 = selectHero(scanner, "Player 2", MAGENTA);

        statusEffects.put(player1, STATUS_NONE);
        statusEffects.put(player2, STATUS_NONE);
        statusDuration.put(player1, 0);
        statusDuration.put(player2, 0);

        System.out.println("\n" + BOLD + YELLOW +
                "★ BATTLE START: " + player1.getName().toUpperCase() +
                " VS " + player2.getName().toUpperCase() + " ★" + RESET + "\n");

        boolean isPlayer1Turn = true;

        // The Combat Loop
        while (!player1.isDead() && !player2.isDead()) {
            Entity activePlayer = isPlayer1Turn ? player1 : player2;
            Entity targetPlayer = isPlayer1Turn ? player2 : player1;
            String activeColor  = isPlayer1Turn ? CYAN : MAGENTA;

            System.out.println("\n" + activeColor + BOLD +
                    "╔══════════════════════════════════════╗");
            System.out.println("   ► " + activePlayer.getName().toUpperCase() + "'S TURN ◄");
            System.out.println("╚══════════════════════════════════════╝" + RESET);

            // Display Stats
            printStats(player1, player2);

            // Apply Status Effects
            if (applyStatusEffect(activePlayer, view)) {
                // Player is stunned — skip turn
                isPlayer1Turn = !isPlayer1Turn;
                pause(1000);
                continue;
            }

            // reduce cooldowns
            for (Skill skill : activePlayer.getSkills()) {
                skill.reduceCooldown();
            }

            // display ang skills
            System.out.println("\n" + BOLD + WHITE + "Choose your skill:" + RESET);
            int skillIndex = 1;
            for (Skill skill : activePlayer.getSkills()) {
                if (skill.isReady()) {
                    System.out.println(GREEN + "  " + skillIndex + ". " + skill.getName() +
                            " [READY] " + RESET + "- " + skill.getDescription());
                } else {
                    System.out.println(RED + "  " + skillIndex + ". " + skill.getName() +
                            " [COOLDOWN: " + skill.getCooldown() + " turns] " + RESET +
                            "- " + skill.getDescription());
                }
                skillIndex++;
            }

            // timed input
            int choice = getTimedInput(scanner, activePlayer, TURN_TIME_SECONDS);

            // Auto-pick first ready skill if time runs out
            if (choice == -1) {
                System.out.println(YELLOW + "\n⏰ Time's up! Auto-selecting first available skill..." + RESET);
                choice = getFirstReadySkillIndex(activePlayer);
            }

            Skill selectedSkill = activePlayer.getSkills().get(choice - 1);

            // Execute Skill
            System.out.println();
            selectedSkill.apply(activePlayer, targetPlayer, view);
            selectedSkill.resetCooldown();

            // Random chance to apply status effect on hit
            applyRandomStatusOnHit(targetPlayer);

            // end turn
            if (targetPlayer.isDead()) {
                printKO(targetPlayer, activePlayer);
                break;
            }

            isPlayer1Turn = !isPlayer1Turn;
            pause(1500);
        }

        scanner.close();
    }

    // ─── PRINT BANNER
    static void printBanner() {
        System.out.println(YELLOW + BOLD);
        System.out.println("  ██╗  ██╗ █████╗ ██████╗ ██████╗ ██╗   ██╗");
        System.out.println("  ██║  ██║██╔══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝");
        System.out.println("  ███████║███████║██████╔╝██████╔╝ ╚████╔╝ ");
        System.out.println("  ██╔══██║██╔══██║██╔═══╝ ██╔═══╝   ╚██╔╝  ");
        System.out.println("  ██║  ██║██║  ██║██║     ██║        ██║   ");
        System.out.println("  ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝     ╚═╝        ╚═╝   ");
        System.out.println("       MEAL TOURNAMENT - PvP EDITION         ");
        System.out.println(RESET);
    }

    // ─── PRINT STATS
    static void printStats(Entity p1, Entity p2) {
        System.out.println(BOLD + "\n  ┌─────────────────────────────────────┐");
        printPlayerStat(p1, CYAN);
        printPlayerStat(p2, MAGENTA);
        System.out.println("  └─────────────────────────────────────┘" + RESET);
    }

    static void printPlayerStat(Entity p, String color) {
        String status = statusEffects.getOrDefault(p, STATUS_NONE);
        String statusTag = status.equals(STATUS_NONE) ? "" :
                (status.equals(STATUS_POISON) ? RED + " [☠ POISONED]" :
                        status.equals(STATUS_BURN)   ? YELLOW + " [🔥 BURNED]" :
                                RED + " [⚡ STUNNED]") + RESET;

        int hpPercent = (int)((double) p.getCurrentHealth() / p.getMaxHealth() * 20);
        String hpBar = GREEN + "█".repeat(hpPercent) + RED + "░".repeat(20 - hpPercent) + RESET;

        System.out.println(color + "  │ " + BOLD + p.getName() + RESET + color +
                " | HP: " + p.getCurrentHealth() + "/" + p.getMaxHealth() +
                " [" + hpBar + color + "]" +
                " | Mana: " + p.getCurrentMana() + statusTag + color + "  │" + RESET);
    }

    // ─── TIMED INPUT RA
    static int getTimedInput(Scanner scanner, Entity activePlayer, int seconds) {
        timedChoice = -1;
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<?> inputFuture = executor.submit(() -> {
            System.out.print(CYAN + "\n⏱ Enter skill number (" + seconds + "s): " + RESET);
            while (true) {
                if (scanner.hasNextInt()) {
                    int input = scanner.nextInt();
                    if (input >= 1 && input <= activePlayer.getSkills().size()) {
                        Skill s = activePlayer.getSkills().get(input - 1);
                        if (s.isReady()) {
                            timedChoice = input;
                            return;
                        } else {
                            System.out.println(RED + "That skill is on cooldown! Try another." + RESET);
                            System.out.print(CYAN + "Enter skill number: " + RESET);
                        }
                    } else {
                        System.out.println(RED + "Invalid number." + RESET);
                        System.out.print(CYAN + "Enter skill number: " + RESET);
                    }
                } else {
                    scanner.next();
                }
            }
        });

        try {
            inputFuture.get(seconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            inputFuture.cancel(true);
        } catch (Exception e) {
            inputFuture.cancel(true);
        } finally {
            executor.shutdownNow();
        }

        return timedChoice;
    }

    // ─── FIRST KILL READY
    static int getFirstReadySkillIndex(Entity player) {
        int i = 1;
        for (Skill skill : player.getSkills()) {
            if (skill.isReady()) return i;
            i++;
        }
        return 1;
    }

    // ─── STATUS EFFECTS NI
    static void applyRandomStatusOnHit(Entity target) {
        Random rand = new Random();
        int roll = rand.nextInt(100);
        String current = statusEffects.getOrDefault(target, STATUS_NONE);

        if (!current.equals(STATUS_NONE)) return; // already has a status

        if (roll < 15) {
            statusEffects.put(target, STATUS_POISON);
            statusDuration.put(target, 3);
            System.out.println(RED + "☠ " + target.getName() + " is POISONED for 3 turns!" + RESET);
        } else if (roll < 25) {
            statusEffects.put(target, STATUS_STUN);
            statusDuration.put(target, 1);
            System.out.println(MAGENTA + "⚡ " + target.getName() + " is STUNNED for 1 turn!" + RESET);
        } else if (roll < 35) {
            statusEffects.put(target, STATUS_BURN);
            statusDuration.put(target, 3);
            System.out.println(YELLOW + "🔥 " + target.getName() + " is BURNING for 3 turns!" + RESET);
        }
    }

    // (skip turn)
    static boolean applyStatusEffect(Entity player, BattleView view) {
        String status = statusEffects.getOrDefault(player, STATUS_NONE);
        int duration  = statusDuration.getOrDefault(player, 0);

        if (status.equals(STATUS_NONE) || duration <= 0) {
            statusEffects.put(player, STATUS_NONE);
            return false;
        }

        if (status.equals(STATUS_POISON)) {
            int dmg = 8;
            player.takeDamage(dmg);
            System.out.println(RED + "☠ " + player.getName() + " takes " + dmg + " poison damage! (" + (duration - 1) + " turns left)" + RESET);
        } else if (status.equals(STATUS_BURN)) {
            int dmg = 12;
            player.takeDamage(dmg);
            System.out.println(YELLOW + "🔥 " + player.getName() + " takes " + dmg + " burn damage! (" + (duration - 1) + " turns left)" + RESET);
        } else if (status.equals(STATUS_STUN)) {
            System.out.println(MAGENTA + "⚡ " + player.getName() + " is STUNNED and cannot move!" + RESET);
            statusDuration.put(player, duration - 1);
            if (statusDuration.get(player) <= 0) statusEffects.put(player, STATUS_NONE);
            return true; // skip ra
        }

        statusDuration.put(player, duration - 1);
        if (statusDuration.get(player) <= 0) statusEffects.put(player, STATUS_NONE);
        return false;
    }

    // ─── KO SCREEN NI
    static void printKO(Entity loser, Entity winner) {
        System.out.println("\n" + RED + BOLD);
        System.out.println("  ██╗  ██╗   ██████╗  ██╗");
        System.out.println("  ██║ ██╔╝  ██╔═══██╗ ██║");
        System.out.println("  █████╔╝   ██║   ██║ ██║");
        System.out.println("  ██╔═██╗   ██║   ██║ ╚═╝");
        System.out.println("  ██║  ██╗  ╚██████╔╝ ██╗");
        System.out.println("  ╚═╝  ╚═╝   ╚═════╝  ╚═╝" + RESET);
        System.out.println(RED + "  " + loser.getName() + " has been defeated!" + RESET);
        System.out.println(YELLOW + BOLD + "\n  🏆 WINNER: " + winner.getName().toUpperCase() + "! 🏆" + RESET + "\n");
    }

    // ─── HERO SELECTION NI
    static Entity selectHero(Scanner scanner, String playerLabel, String color) {
        System.out.println("\n" + color + BOLD + playerLabel + ", choose your hero:" + RESET);
        System.out.println(color +
                "  1. Mark    2. Ted\n" +
                "  3. Den     4. Ashley\n" +
                "  5. Vince   6. Zack\n" +
                "  7. Clent   8. Trone" + RESET);

        while (true) {
            System.out.print(color + "Enter hero number (1-8): " + RESET);
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1: return new Mark();
                    case 2: return new Ted();
                    case 3: return new Den();
                    case 4: return new Ashley();
                    case 5: return new Vince();
                    case 6: return new Zack();
                    case 7: return new Clent();
                    case 8: return new Trone();
                    default: System.out.println(RED + "Invalid choice. Try again." + RESET);
                }
            } else {
                System.out.println(RED + "Please enter a number." + RESET);
                scanner.next();
            }
        }
    }

    // ─── UTILITY ra NI
    static void pause(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }
}
