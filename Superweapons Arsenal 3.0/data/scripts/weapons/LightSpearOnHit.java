package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.MissileAPI;

public class LightSpearOnHit implements OnHitEffectPlugin {

    private static final Color COLOR1 = new Color(90, 140, 200, 50);
    private static final Color COLOR2 = new Color(170, 200, 250, 115);
    private static final Vector2f ZERO = new Vector2f();
	float OFFSET = 25f;

    public void onHit(DamagingProjectileAPI projectile,
            CombatEntityAPI target,
            Vector2f point,
            boolean shieldHit,
            ApplyDamageResultAPI damageResult, 
            CombatEngineAPI engine){
				
        if (target == null || point == null) {
            return;
        }
		if (!(target instanceof MissileAPI)){
			engine.spawnExplosion(point, ZERO, COLOR1, 600f, 0.7f);
			engine.spawnExplosion(point, ZERO, COLOR2, 400f, 0.5f);
			Global.getSoundPlayer().playSound("LightSpear_Explode", 1f, 0.25f, point, ZERO);
			
			float Angle = projectile.getFacing();
				
				engine.spawnProjectile(projectile.getSource(), projectile.getWeapon(), "sw_lightsplinter", point ,  Angle         + ((float) Math.random() * 12f) - ((float) Math.random() * 12f) , null);
				
				engine.spawnProjectile(projectile.getSource(), projectile.getWeapon(), "sw_lightsplinter", point , (Angle + 40f)  + ((float) Math.random() * OFFSET) - ((float) Math.random() * OFFSET) , null);
					
				engine.spawnProjectile(projectile.getSource(), projectile.getWeapon(), "sw_lightsplinter", point , (Angle - 40f)  + ((float) Math.random() * OFFSET) - ((float) Math.random() * OFFSET) , null);
					
				engine.spawnProjectile(projectile.getSource(), projectile.getWeapon(), "sw_lightsplinter", point , (Angle + 100f) + ((float) Math.random() * OFFSET) - ((float) Math.random() * OFFSET) , null);
					
				engine.spawnProjectile(projectile.getSource(), projectile.getWeapon(), "sw_lightsplinter", point , (Angle - 100f) + ((float) Math.random() * OFFSET) - ((float) Math.random() * OFFSET) , null);
				
		}
    }
}
