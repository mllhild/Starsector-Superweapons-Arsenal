package data.scripts.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.GameState;
import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.CollisionClass;


public class ClusterTorpedoEveryFrame implements EveryFrameWeaponEffectPlugin {
		
	private static final Color COLOR1 = new Color(115, 100, 235, 70);
    private static final Color COLOR2 = new Color(130, 120, 230, 145);
    private static final Vector2f ZERO = new Vector2f();
	private float Angle = 18f;
	private DamagingProjectileAPI Proj;
	private boolean triggered = false;
	private float Timer = 0f;
	
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
		weapon.getAmmoTracker().setMaxAmmo(1);
		
        if (engine.isPaused()) {return;}
		if (Global.getSettings().getCurrentState() == GameState.COMBAT){
			
			for(DamagingProjectileAPI p : engine.getProjectiles())
				if(p.getWeapon()==weapon)
					Proj = p;
				
				
			if (Proj != null){
				
				Vector2f point = Proj.getLocation();
				float Damage = Proj.getDamageAmount();
				
				
					
				if (!triggered && !Proj.didDamage() && (Proj.getElapsed() >= 5f || Proj.isExpired() || Proj.getHitpoints() <= 0f)){
				
					triggered = true;
				
					engine.spawnEmpArc(Proj.getSource(), point, Proj, Proj,
					DamageType.ENERGY,
					10000,
					0f, // emp 
					100000f, // max range 
					"Photon_Fire",
					0f, // thickness
					COLOR1,
					COLOR1);
				
					DamagingExplosionSpec Impact = new DamagingExplosionSpec(
						0.1f,				//duration
						400f,				//radius
						250f, 				//coreRadius
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
					
						Impact.setShowGraphic(false);
					
					engine.spawnDamagingExplosion(Impact, Proj.getSource(), point, true);
			
					engine.spawnExplosion(point, ZERO, COLOR1, 1000f, 1.6f);
					engine.spawnExplosion(point, ZERO, COLOR2, 750f, 1.1f);
					Global.getSoundPlayer().playSound("Photon_Explode", 1f, 1f, point, ZERO);
			
					for (float i = 1f; i <= 18f; i++){
			
						if (i < 10f)
							if (i == 2f || i == 5f || i == 8f || i == 11f || i == 14f || i == 17f)
								engine.spawnProjectile(Proj.getSource(), null, "sw_clustermine_slow", point , i* Angle, null);
							else
								engine.spawnProjectile(Proj.getSource(), null, "sw_clustermine_fast", point , i* Angle, null);
						else
							if (i == 2f || i == 5f || i == 8f || i == 11f || i == 14f || i == 17f)
								engine.spawnProjectile(Proj.getSource(), null, "sw_clustermine_slow", point , i* Angle + Angle, null);
							else
								engine.spawnProjectile(Proj.getSource(), null, "sw_clustermine_fast", point , i* Angle + Angle, null);
			
						Global.getSoundPlayer().playSound("Photon_Fire", 1f, 1f, point, ZERO);
					}
				}
				
				if (Proj.getHitpoints() <= 0f && Timer < 10f){
					Timer += amount;
					Global.getSoundPlayer().playLoop("Mine_Loop", engine, 1f, 0.7f, point, ZERO);
				}
			}
			
			if (triggered)
				weapon.setAmmo(0);
		}
	}
}
