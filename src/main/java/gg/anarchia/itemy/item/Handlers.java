package gg.anarchia.itemy.item;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Zestaw interfejsow ktore moze implementowac {@link CustomItem}.
 * Listenery pluginu sprawdzaja instanceof i wywoluja odpowiednia metode.
 */
public final class Handlers {

    private Handlers() {
    }

    /** Klikniecie PPM przedmiotem. */
    public interface RightClick {
        void onRightClick(Player player, ItemStack item, PlayerInteractEvent event);
    }

    /** Klikniecie LPM przedmiotem. */
    public interface LeftClick {
        void onLeftClick(Player player, ItemStack item, PlayerInteractEvent event);
    }

    /** Uderzenie przeciwnika trzymanym przedmiotem. */
    public interface Attack {
        void onAttack(Player attacker, LivingEntity victim, ItemStack item, EntityDamageByEntityEvent event);
    }

    /** Otrzymanie obrazen gdy przedmiot jest w rece / na sobie. */
    public interface Defend {
        void onDefend(Player victim, Entity attacker, ItemStack item, EntityDamageByEntityEvent event);
    }

    /** Dowolne obrazenia otrzymane przez gracza (np. upadek). */
    public interface Damaged {
        void onDamaged(Player player, ItemStack item, EntityDamageEvent event);
    }

    /** Wywolywane cyklicznie gdy gracz trzyma przedmiot w rece. */
    public interface Hold {
        void onHold(Player player, ItemStack item);
    }

    /** Wywolywane cyklicznie gdy gracz ma przedmiot zalozony (zbroja). */
    public interface Wear {
        void onWear(Player player, ItemStack item);
    }

    /** Strzal z luku / kuszy. */
    public interface Shoot {
        void onShoot(Player player, ItemStack bow, EntityShootBowEvent event);
    }

    /** Pocisk wystrzelony tym przedmiotem w cos trafil. */
    public interface Hit {
        void onProjectileHit(Player shooter, Projectile projectile, ProjectileHitEvent event);
    }

    /** Pocisk wystrzelony tym przedmiotem zadal komus obrazenia. */
    public interface ProjectileDamage {
        void onProjectileDamage(Player shooter, Projectile projectile, LivingEntity victim, EntityDamageByEntityEvent event);
    }

    /** Zjedzenie / wypicie przedmiotu. */
    public interface Consume {
        void onConsume(Player player, ItemStack item, PlayerItemConsumeEvent event);
    }

    /** Uzycie wedki. */
    public interface Fish {
        void onFish(Player player, ItemStack item, PlayerFishEvent event);
    }

    /** Gracz z tym przedmiotem w ekwipunku kogos zabil. */
    public interface Kill {
        void onKill(Player killer, Player victim, ItemStack item, PlayerDeathEvent event);
    }

    /** Wlasciciel przedmiotu zginal - zwroc true jesli przedmiot "zuzyl sie". */
    public interface Death {
        boolean onDeath(Player player, ItemStack item, PlayerDeathEvent event);
    }

    /** Sprzatanie po graczu (wyjscie z serwera / schowanie przedmiotu). */
    public interface Cleanup {
        void cleanup(Player player);
    }
}
