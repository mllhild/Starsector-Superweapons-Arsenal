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
import org.dark.shaders.distortion.RippleDistortion;
import org.dark.shaders.distortion.DistortionShader;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import java.awt.Color;
import java.util.*;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class SupernovaOnHit implements OnHitEffectPlugin {

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
				if (Missile.getWeapon() != null){
					if (Missile.getWeapon().getSpec() != null)
						if(!Missile.getWeapon().getSpec().hasTag("supernova"))
							SPLASH_MISSILES.add(Missile);
				}
				else
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
			
			RippleDistortion ripple = new RippleDistortion(point, ZERO);
					ripple.setSize(1000f);
					ripple.setIntensity(200f);
					ripple.setFrameRate(30f);
					ripple.fadeInSize(0.8f);
					ripple.fadeOutIntensity(0.5f);
					
			engine.spawnExplosion(point, ZERO, COLOR1, 1000f, 1.5f);
			engine.spawnExplosion(point, ZERO, COLOR2, 650f, 0.7f);
			Global.getSoundPlayer().playSound("Phalanx_Explode", 1f, 1f, point, ZERO);
			DistortionShader.addDistortion(ripple);
			
			for (int i = 0; i < 6; i++){
				
				float Offset = 300f;
				float RotationAmount = 60 * i + (float) Math.random() * 25f;
				
				Vector2f Loc = new Vector2f(point.x + Offset, point.y);
				Loc = VectorUtils.rotateAroundPivot(Loc, point, RotationAmount, Loc);
				
				Vector2f targetOffset = MathUtils.getRandomPointOnCircumference(Loc, 130f);
				
				engine.spawnEmpArc(projectile.getSource(), Loc , null, new SimpleEntity(targetOffset), 
					DamageType.ENERGY, 
					0f,
					0f, 
					100000f,
					null,
					50f,
					COLOR2,
					COLOR2.brighter());
				}
		}
    }
}
