package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class BFGOnHit implements OnHitEffectPlugin {

    private static final Color COLOR1 = new Color(120, 220, 90, 70);
    private static final Color COLOR2 = new Color(150, 255, 130, 125);
    private static final Vector2f ZERO = new Vector2f();


    public void onHit(DamagingProjectileAPI projectile,
            CombatEntityAPI target,
            Vector2f point,
            boolean shieldHit,
            ApplyDamageResultAPI damageResult, 
            CombatEngineAPI engine)
	{
        if (target == null || point == null) {
            return;
        }
		if (!(target instanceof MissileAPI)){
			engine.spawnExplosion(point, ZERO, COLOR1, 800f, 1.3f);
			engine.spawnExplosion(point, ZERO, COLOR2, 500f, 0.6f);
			Global.getSoundPlayer().playSound("BFG_Explode", 1f, 1f, point, ZERO);
		}
    }
}
