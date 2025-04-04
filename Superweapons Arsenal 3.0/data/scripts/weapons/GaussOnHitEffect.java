package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import java.awt.Color;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.combat.CollisionClass;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class GaussOnHitEffect implements OnHitEffectPlugin {

    private static final Color COLOR1 = new Color(255, 159, 104, 70);
    private static final Color COLOR2 = new Color(255, 209, 144, 125);
    private static final Vector2f ZERO = new Vector2f();
	float KineticMulti = 0f;
	float KineticDamage = 0f;
	float Damage = 0f;

    public void onHit(DamagingProjectileAPI projectile,
            CombatEntityAPI target,
            Vector2f point,
            boolean shieldHit,
            ApplyDamageResultAPI damageResult, 
            CombatEngineAPI engine)
	{
        if (target == null || point == null)
            return;
		
		Damage = projectile.getDamageAmount();
		
		if (projectile.getElapsed() < 1f)
			KineticMulti = projectile.getElapsed();
		else
			KineticMulti = 1f;
		
		KineticDamage = Damage * KineticMulti;
		
		DamagingExplosionSpec KineticBlast = new DamagingExplosionSpec(
					0.1f,				//duration
					20f,				//radius
					20f, 				//coreRadius
					KineticDamage,		//maxDamage
					KineticDamage,		//minDamage
					CollisionClass.HITS_SHIPS_AND_ASTEROIDS,	//collisionClass
					CollisionClass.HITS_SHIPS_AND_ASTEROIDS,	//collisionClassByFighter
					12f,				//particleSizeMin,
					20f,				//particleSizeRange
					0.5f,				//particleDuration
					10, 				//particleCount
					COLOR1,
					COLOR2);
					
					KineticBlast.setShowGraphic(false); 
					KineticBlast.setDamageType(DamageType.KINETIC);
			
			engine.spawnDamagingExplosion(KineticBlast, projectile.getSource(), point, true);
			
			engine.spawnExplosion(point, ZERO, COLOR1, 150f + 150f * KineticMulti, 1.3f);
			engine.spawnExplosion(point, ZERO, COLOR2, 100f + 100f * KineticMulti, 0.9f);
			Global.getSoundPlayer().playSound("Gauss_Explode", 1f, 0.5f + 0.5f * KineticMulti, point, ZERO);
    }
}
