package data.scripts.weapons;

import java.util.ArrayList;
import java.util.List;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;
import org.lazywizard.lazylib.combat.CombatUtils;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class PhalanxOnHit implements OnHitEffectPlugin {

    private static final float SPLASH_RANGE = 350f;
	List<ShipAPI> SPLASH_TARGETS = new ArrayList();
	List<MissileAPI> SPLASH_MISSILES = new ArrayList();
    private static final Color COLOR1 = new Color(220, 120, 40, 70);
    private static final Color COLOR2 = new Color(255, 180, 60, 125);
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
		float DAMAGE = projectile.getDamageAmount();
           
		if (!(target instanceof MissileAPI)){
			for (ShipAPI Starget : CombatUtils.getShipsWithinRange(point, SPLASH_RANGE))
				if (Starget != target)
					SPLASH_TARGETS.add(Starget);
		
			for (MissileAPI Missile : CombatUtils.getMissilesWithinRange(point, SPLASH_RANGE))
				SPLASH_MISSILES.add(Missile);
		
			for (CombatEntityAPI Missile : SPLASH_MISSILES)
				engine.applyDamage(Missile , point, DAMAGE, DamageType.ENERGY, 0f, false, false, projectile.getSource(), true);
		
			for (ShipAPI Starget : SPLASH_TARGETS){
				for (int i = 0; i < 5; i++){
					if (!Starget.isStationModule()){
						engine.spawnEmpArc(projectile.getSource(), point, Starget, Starget,
							DamageType.ENERGY,
							DAMAGE /5f,
							0f, // emp 
							100000f, // max range 
							"Phalanx_Explode",
							0f, // thickness
							COLOR1,
							COLOR1.brighter());
					}
					else{
						engine.spawnEmpArc(projectile.getSource(), point, Starget, Starget,
							DamageType.ENERGY,
							DAMAGE /15f,
							0f, // emp 
							100000f, // max range 
							"Phalanx_Explode",
							0f, // thickness
							COLOR1,
							COLOR1.brighter());
					}
				}
			}
			engine.spawnExplosion(point, ZERO, COLOR1, 800f, 1.3f);
			engine.spawnExplosion(point, ZERO, COLOR2, 500f, 0.6f);
			Global.getSoundPlayer().playSound("Phalanx_Explode", 1f, 1f, point, ZERO);
		}
    }
}
