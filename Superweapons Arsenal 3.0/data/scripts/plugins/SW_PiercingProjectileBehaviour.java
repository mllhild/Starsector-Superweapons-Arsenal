package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.DamageType;
import org.lwjgl.util.vector.Vector2f;
import java.awt.Color;
import java.util.List;

public class SW_PiercingProjectileBehaviour extends BaseEveryFrameCombatPlugin {
	
	private static final Color COLOR = new Color(255, 255, 255, 0);
	private final float DAMAGE_MODIFIER = 0.55f; 	//A factor for adjusting damage based on distance and rate of occurance.
	private final float BASE_SPEED = 5000f;
	private float counter = 0f;
	
    @Override
    public void advance(float amount, List events) {
		counter += amount;
		CombatEngineAPI engine = Global.getCombatEngine();
		if (engine.isPaused()) {return;}
		
		if (counter > 0.01f){
			counter = 0f;
			for (DamagingProjectileAPI Proj : engine.getProjectiles()){
				if (Proj != null){
					if (Proj.getCollisionClass() == CollisionClass.NONE && Proj.getWeapon() != null && Proj.getWeapon().getSpec() != null && !Proj.isFading()){
						
						Vector2f point = Proj.getLocation();
						float Damage = Proj.getDamageAmount();
						float Speed = Proj.getMoveSpeed();
						
						//Electromagnetic Railgun
						if (Proj.getWeapon().getSpec().hasTag("sw_railgun")){
							DamagingExplosionSpec PierceEffect = CalculateDamage(Damage,Speed,20f);
							
							PierceEffect.setDamageType(DamageType.ENERGY);
							engine.spawnDamagingExplosion(PierceEffect, Proj.getSource(), point, false);
						}
						
						//LightSplinter
						if (Proj.getWeapon().getSpec().hasTag("sw_lightspear")){
							DamagingExplosionSpec PierceEffect = CalculateDamage(Damage,Speed,15f);
							
							PierceEffect.setDamageType(DamageType.ENERGY);
							engine.spawnDamagingExplosion(PierceEffect, Proj.getSource(), point, true);
						}
						
						//Flash
						if (Proj.getWeapon().getSpec().hasTag("sw_flash")){
							DamagingExplosionSpec PierceEffect1 = CalculateDamage(Damage,Speed,30f);
							DamagingExplosionSpec PierceEffect2 = CalculateDamage(Damage,Speed,30f);
							DamagingExplosionSpec PierceEffect3 = CalculateDamage(Damage,Speed,30f);
							DamagingExplosionSpec PierceEffect4 = CalculateDamage(Damage,Speed,30f);
							
							//sicko mode
							PierceEffect1.setDamageType(DamageType.ENERGY);
							PierceEffect2.setDamageType(DamageType.KINETIC);
							PierceEffect3.setDamageType(DamageType.HIGH_EXPLOSIVE);
							PierceEffect4.setDamageType(DamageType.FRAGMENTATION);
							
							engine.spawnDamagingExplosion(PierceEffect1, Proj.getSource(), point, false);
							engine.spawnDamagingExplosion(PierceEffect2, Proj.getSource(), point, false);
							engine.spawnDamagingExplosion(PierceEffect3, Proj.getSource(), point, false);
							engine.spawnDamagingExplosion(PierceEffect4, Proj.getSource(), point, false);
						}
					}
				}
			}
		}
	}
	public DamagingExplosionSpec CalculateDamage(float Damage, float ProjSpeed, float AoE){
		float Impact = Damage * DAMAGE_MODIFIER * (ProjSpeed / BASE_SPEED);
		DamagingExplosionSpec DamageSpec = new DamagingExplosionSpec(
			0.05f,				//duration
			AoE * 1.5f,			//radius
			AoE, 				//coreRadius
			Impact,				//maxDamage
			Impact,				//minDamage
			CollisionClass.HITS_SHIPS_AND_ASTEROIDS,	//collisionClass //PROJECTILE_FF
			CollisionClass.PROJECTILE_FF,				//collisionClassByFighter
			12f,				//particleSizeMin,
			20f,				//particleSizeRange
			0.5f,				//particleDuration
			10, 				//particleCount
			COLOR,
			COLOR);
			
		DamageSpec.setShowGraphic(false);
		return DamageSpec;
	}
}
