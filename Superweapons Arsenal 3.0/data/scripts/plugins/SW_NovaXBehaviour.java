package data.scripts.plugins;

import com.fs.starfarer.api.Global;

import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CollisionClass;
import org.lazywizard.lazylib.combat.CombatUtils;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;
import java.awt.Color;

public class SW_NovaXBehaviour extends BaseEveryFrameCombatPlugin {
	
    private static final float SEARCH_RANGE = 100f;
    private static final float SPLASH_RANGE = 250f;
	private static final Color COLOR = new Color(255, 255, 255, 0);
    private static final Color COLOR1 = new Color(150, 100, 70, 70);
    private static final Color COLOR2 = new Color(185, 150, 110, 125);
    private static final Color NULL = new Color(0, 0, 0, 0);
    private static final Vector2f ZERO = new Vector2f();
		
    @Override
    public void advance(float amount, List events) {
		CombatEngineAPI engine = Global.getCombatEngine();
		if (engine.isPaused()) {return;}
		
		for (MissileAPI Missile : engine.getMissiles()){
			if (Missile.getWeapon() != null)
			if (Missile.getWeapon().getSpec().hasTag("sw_novax")){
				
				Vector2f point = Missile.getLocation();
				WeaponAPI weapon = Missile.getWeapon();
				float Damage = Missile.getDamageAmount();
				
				if (!Missile.didDamage() && !Missile.isFading() && !Missile.isExpired() && Missile.getHitpoints() > 0f){
					boolean Empty = true;
					
					//Search for nearby (hostile) Missiles
					for (MissileAPI Target : CombatUtils.getMissilesWithinRange(point, SEARCH_RANGE))
						if (Target.getSource().getOriginalOwner() != Missile.getSource().getOriginalOwner() && !Target.getSource().isAlly() && Target.getHitpoints() > 0f)
							Empty = false;
					
					//Search for nearby (hostile) Fighters
					for (ShipAPI Target : CombatUtils.getShipsWithinRange(point, SEARCH_RANGE))
						if (Target.isAlive() && Target.isFighter() && !Target.isAlly() && Target.getOriginalOwner() != Missile.getSource().getOriginalOwner())
							Empty = false;
						
						
					//Trigger (if any)
					if (!Empty){
						
						//kill self
						engine.applyDamage(Missile , point, 15000, DamageType.FRAGMENTATION, 0f, false, false, Missile.getSource(), true);
								
						//Damage nearby missiles
						for (MissileAPI Target : CombatUtils.getMissilesWithinRange(point, SPLASH_RANGE)){
							if (Target.getWeapon() != null){
							if (!Target.getWeapon().getSpec().hasTag("sw_novax"))
								engine.applyDamage(Target , point, Damage, DamageType.FRAGMENTATION, 0f, false, false, Missile.getSource(), true);
							else if (Target.getWeapon().getSpec().hasTag("sw_novax") && !Target.getSource().isAlly() && Target.getSource().getOriginalOwner() != Missile.getSource().getOriginalOwner())
								engine.applyDamage(Target , point, Damage, DamageType.FRAGMENTATION, 0f, false, false, Missile.getSource(), true);
							}
							else{
								engine.applyDamage(Target , point, Damage, DamageType.FRAGMENTATION, 0f, false, false, Missile.getSource(), true);
							}
						}
						
						//Damage nearby Fighters
						for (ShipAPI Target : CombatUtils.getShipsWithinRange(point, SPLASH_RANGE)){
							if (Target != Missile.getSource() && !Target.isAlly() && Target.isFighter()){	
								for (int i = 0; i < 3; i++){
									engine.spawnEmpArc(Missile.getSource(), point, Target, Target,
									DamageType.FRAGMENTATION,
									Damage / 3f,
									0f, // emp 
									100000f, // max range 
									null,
									0f, // thickness
									NULL,
									NULL);
								}
							}
						}
						
						DamagingExplosionSpec DamageSpec = new DamagingExplosionSpec(
								0.05f,				//duration
								SPLASH_RANGE + 50f, //radius
								150f, 				//coreRadius
								Damage,				//maxDamage
								Damage /2f,			//minDamage
								CollisionClass.HITS_SHIPS_AND_ASTEROIDS,	//collisionClass //PROJECTILE_FF
								CollisionClass.PROJECTILE_FF,				//collisionClassByFighter
								12f,				//particleSizeMin,
								20f,				//particleSizeRange
								0.5f,				//particleDuration
								10, 				//particleCount
								COLOR,
								COLOR);
								
						DamageSpec.setShowGraphic(false);
						DamageSpec.setDamageType(DamageType.FRAGMENTATION);
						engine.spawnDamagingExplosion(DamageSpec, Missile.getSource(), point, false);
						
						//Visuals
						engine.spawnExplosion(point, ZERO, COLOR1, 400f, 1f);
						engine.spawnExplosion(point, ZERO, COLOR2, 130f, 0.4f);
						Global.getSoundPlayer().playSound("Photon_Explode", 1f, 0.75f, point, ZERO);
					}
				}
			}
		}
	}
}
