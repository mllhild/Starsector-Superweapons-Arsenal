package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

public class PositronOnHit implements OnHitEffectPlugin {

    private static final Color COLOR1 = new Color(70, 120, 250, 70);
    private static final Color COLOR2 = new Color(120, 170, 250, 145);
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
			
			if (shieldHit && target instanceof ShipAPI){
				ShipAPI Ship = (ShipAPI) target;
				Ship.getMutableStats().getDynamic().getStat("SW_POSITRON_SHIELD").modifyFlat("SW_PS2" , 0.1f);
			}
			else if (!shieldHit && target instanceof ShipAPI){
				ShipAPI Ship = (ShipAPI) target;
				Ship.getMutableStats().getDynamic().getStat("SW_POSITRON").modifyFlat("SW_P2" , 0.1f);
			}
		
			float Damage = projectile.getDamageAmount();
			DamagingExplosionSpec Impact = new DamagingExplosionSpec(
					0.1f,				//duration
					30f,				//radius
					30f, 				//coreRadius
					Damage,				//maxDamage
					Damage,		    	//minDamage
					CollisionClass.HITS_SHIPS_AND_ASTEROIDS,	//collisionClass
					CollisionClass.FIGHTER,						//collisionClassByFighter
					12f,				//particleSizeMin,
					20f,				//particleSizeRange
					0.5f,				//particleDuration
					10, 				//particleCount
					COLOR1,
					COLOR2);
					
					Impact.setDamageType(DamageType.HIGH_EXPLOSIVE);
					Impact.setShowGraphic(false);
					
			engine.spawnDamagingExplosion(Impact, projectile.getSource(), point, true);
		
			engine.spawnExplosion(point, ZERO, COLOR1, 450f, 0.75f);
			engine.spawnExplosion(point, ZERO, COLOR2, 400f, 0.65f);
			Global.getSoundPlayer().playSound("Positron_Explode", 1f, 0.8f, point, ZERO);
		}
    }
}
